package com.blogspot.nurkiewicz.spring

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.orm.jpa.JpaTransactionManager
import org.hibernate.dialect.H2Dialect
import scalaj.collection.Implicits._
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor
import org.springframework.context.annotation._
import org.hibernate.cfg.ImprovedNamingStrategy
import org.h2.tools.Server
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.cache.ehcache.{EhCacheManagerFactoryBean, EhCacheCacheManager}
import management.ManagementFactory
import net.sf.ehcache.management.ManagementService

/**
 * @author Tomasz Nurkiewicz
 * @since 09.10.11, 23:01
 */
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@ComponentScan(basePackages = Array("com.blogspot.nurkiewicz"),
	scopedProxy = ScopedProxyMode.TARGET_CLASS,
	excludeFilters = Array(
		new ComponentScan.Filter(value = Array[Class[_]](classOf[Controller], classOf[ComponentScan], classOf[EnableWebMvc]))
))
@ImportResource(Array("classpath:/applicationContext.xml"))
class SpringConfiguration {

	@Bean(destroyMethod = "close")
	def dataSource() = {
		val ds = new ManagedBasicDataSource()
		ds.setDriverClassName("org.h2.Driver")
		ds.setUrl("jdbc:h2:mem:pitfalls")
		ds.setUsername("sa")
		ds
	}

	@Bean
	def transactionManager() = new JpaTransactionManager(entityManagerFactory())

	def entityManagerFactory() = entityManagerFactoryBean().getObject

	@Bean
	def entityManagerFactoryBean() = {
		val entityManagerFactoryBean = new JmxLocalContainerEntityManagerFactoryBean()
		entityManagerFactoryBean.setDataSource(dataSource())
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter())
		entityManagerFactoryBean.setPackagesToScan("com.blogspot.nurkiewicz")
		entityManagerFactoryBean.setJpaPropertyMap(
			Map(
				"hibernate.hbm2ddl.auto" -> "create",
				"hibernate.format_sql" -> "true",
				"hibernate.ejb.naming_strategy" -> classOf[ImprovedNamingStrategy].getName,
				"hibernate.generate_statistics" -> true.toString
			).asJava
		)
		entityManagerFactoryBean
	}

	@Bean
	def jpaVendorAdapter() = {
		val vendorAdapter = new HibernateJpaVendorAdapter()
		vendorAdapter.setDatabasePlatform(classOf[H2Dialect].getName)
		vendorAdapter
	}

	@Bean
	def persistenceExceptionTranslationPostProcessor() = new PersistenceExceptionTranslationPostProcessor()

	@Bean
	def persistenceAnnotationBeanPostProcessor() = new PersistenceAnnotationBeanPostProcessor()

	@Bean(initMethod = "start", destroyMethod = "stop")
	def h2WebServer() = Server.createWebServer("-webDaemon", "-webAllowOthers")

	@Bean
	def schedulerFactory() = {
	    val schedulerFactoryBean = new SchedulerFactoryBean()
	    schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"))
	    schedulerFactoryBean.setWaitForJobsToCompleteOnShutdown(true)
	    schedulerFactoryBean
	}

	@Bean def cacheManager = {
		val ehCacheCacheManager = new EhCacheCacheManager
		ehCacheCacheManager.setCacheManager(ehCacheManager())
		ehCacheCacheManager
	}

	@Bean def ehCacheManagerFactoryBean = {
		val ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean
		ehCacheManagerFactoryBean.setShared(true)
		ehCacheManagerFactoryBean.setCacheManagerName("spring-pitfalls")
		ehCacheManagerFactoryBean
	}

	def ehCacheManager() = ehCacheManagerFactoryBean.getObject

	@Bean def platformMBeanServer() = ManagementFactory.getPlatformMBeanServer

	@Bean(initMethod = "init", destroyMethod = "dispose")
	def managementService = new ManagementService(ehCacheManager(), platformMBeanServer(), true, true, true, true, true)

}
