package com.blogspot.nurkiewicz

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import reflect.Block
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.context.annotation.{ComponentScan, Bean, Configuration}
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.config.annotation.EnableWebMvc

/**
 * @author Tomasz Nurkiewicz
 * @since 09.10.11, 23:01
 */
@EnableTransactionManagement
@ComponentScan(basePackages = Array("com.blogspot.nurkiewicz"), excludeFilters = Array(
	new ComponentScan.Filter(value = classOf[Controller]),
	new ComponentScan.Filter(value = classOf[EnableWebMvc])
))
class SpringConfiguration {

	@Bean(destroyMethod = "close")
	def dataSource() = {
		val ds = new BasicDataSource()
		ds.setDriverClassName("org.h2.Driver")
		ds.setUrl("jdbc:h2:mem:")
		ds.setUsername("sa")
		ds
	}

	@Bean
	def transactionManager() = new DataSourceTransactionManager(dataSource());

}
