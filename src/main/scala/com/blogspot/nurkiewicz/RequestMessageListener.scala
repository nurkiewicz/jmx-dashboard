package com.blogspot.nurkiewicz

import javax.jms.{Message, MessageListener}
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit


/**
 * @author Tomasz Nurkiewicz
 * @since 02.02.12, 23:27
 */
@Service
class RequestMessageListener extends MessageListener {
	def onMessage(message: Message) {
		println("Message received: " + message)
		TimeUnit.SECONDS.sleep(2)
	}
}
