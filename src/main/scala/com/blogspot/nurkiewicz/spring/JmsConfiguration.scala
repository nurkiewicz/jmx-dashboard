package com.blogspot.nurkiewicz.spring

import org.apache.activemq.pool.PooledConnectionFactory
import org.springframework.context.annotation.{Primary, Bean, Configuration}
import javax.annotation.Resource
import org.apache.activemq.spring.ActiveMQConnectionFactory
import org.springframework.jms.core.JmsTemplate
import org.apache.activemq.command.ActiveMQQueue
import org.springframework.jms.listener.DefaultMessageListenerContainer
import javax.jms.{MessageListener, ConnectionFactory}


/**
 * @author Tomasz Nurkiewicz
 * @since 02.02.12, 23:16
 */
@Configuration
class JmsConfiguration {
	
	@Resource
	val amqConnectionFactory: ActiveMQConnectionFactory = null

	@Resource
	val requestMessageListener: MessageListener = null

	@Bean
	@Primary
	def jmsConnectionFactory() = new PooledConnectionFactory(amqConnectionFactory)
	
	@Bean def jmsTemplate() = new JmsTemplate(jmsConnectionFactory())

	@Bean
	def requestsQueue() = new ActiveMQQueue("requests");

	@Bean
	def jmsContainer() = {
		val container = new DefaultMessageListenerContainer();
		container.setConnectionFactory(jmsConnectionFactory());
		container.setDestination(requestsQueue());
		container.setSessionTransacted(true);
		container.setConcurrentConsumers(1);
		container.setMessageListener(requestMessageListener);
		container;
	}

}
