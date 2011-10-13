package com.blogspot.nurkiewicz

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.context.annotation.{Bean, Configuration}
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import reflect.Block

/**
 * @author Tomasz Nurkiewicz
 * @since 09.10.11, 23:01
 */
@Configuration
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
