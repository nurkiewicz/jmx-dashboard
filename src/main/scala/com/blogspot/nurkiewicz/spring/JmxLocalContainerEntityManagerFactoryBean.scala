package com.blogspot.nurkiewicz.spring

import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.hibernate.jmx.StatisticsService
import javax.persistence.EntityManagerFactory
import org.hibernate.ejb.EntityManagerFactoryImpl
import javax.management.ObjectName
import management.ManagementFactory


class JmxLocalContainerEntityManagerFactoryBean() extends LocalContainerEntityManagerFactoryBean {
	override def createNativeEntityManagerFactory() = {
		val managerFactory = super.createNativeEntityManagerFactory()
		registerStatisticsMBean(managerFactory)
		managerFactory
	}

	def registerStatisticsMBean(managerFactory: EntityManagerFactory) {
		managerFactory match {
			case impl: EntityManagerFactoryImpl =>
				val mBean = new StatisticsService();
				mBean.setStatisticsEnabled(true)
				mBean.setSessionFactory(impl.getSessionFactory);
				val name = new ObjectName("org.hibernate:type=Statistics,application=jmx-dashboard")
				ManagementFactory.getPlatformMBeanServer.registerMBean(mBean, name);
			case _ =>
		}
	}

}