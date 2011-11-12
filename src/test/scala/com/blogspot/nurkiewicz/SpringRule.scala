package com.blogspot.nurkiewicz

import org.springframework.test.context.TestContextManager
import org.scalatest._
import org.springframework.context.ApplicationContext
import javax.annotation.Resource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.BeanFactory

/**
 * @author Tomasz Nurkiewicz
 * @since 08.10.11, 00:06
 */

trait SpringRule extends Suite with BeforeAndAfterAll {

	@Autowired val beanFactory: BeanFactory = null

	override protected def beforeAll() {
		new TestContextManager(this.getClass).prepareTestInstance(this)
		super.beforeAll();
	}

}
