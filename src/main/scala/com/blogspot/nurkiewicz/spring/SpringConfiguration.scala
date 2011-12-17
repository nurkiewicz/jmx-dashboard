package com.blogspot.nurkiewicz.spring

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import reflect.Block
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.orm.jpa.{LocalContainerEntityManagerFactoryBean, JpaTransactionManager}
import net.sf.ehcache.hibernate.SingletonEhCacheRegionFactory
import org.hibernate.dialect.H2Dialect
import scalaj.collection.Implicits._
import org.springframework.data.repository.core.support.RepositoryFactorySupport
import java.io.Serializable
import org.springframework.data.repository.core.RepositoryMetadata
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory
import com.blogspot.nurkiewicz.BookDao
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor
import org.springframework.context.annotation._
import org.hibernate.cfg.ImprovedNamingStrategy
import org.h2.tools.Server

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
		val ds = new BasicDataSource()
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
		val entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean()
		entityManagerFactoryBean.setDataSource(dataSource())
		entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter())
		entityManagerFactoryBean.setPackagesToScan("com.blogspot.nurkiewicz")
		entityManagerFactoryBean.setJpaPropertyMap(
			Map(
				"hibernate.hbm2ddl.auto" -> "create",
				"hibernate.format_sql" -> "true",
				"hibernate.ejb.naming_strategy" -> classOf[ImprovedNamingStrategy].getName
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

}
