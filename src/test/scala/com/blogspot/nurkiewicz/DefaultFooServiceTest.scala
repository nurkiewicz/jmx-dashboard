package com.blogspot.nurkiewicz

import org.springframework.test.context.ContextConfiguration
import javax.annotation.Resource
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

/**
 * @author Tomasz Nurkiewicz
 * @since 25.09.11, 19:30
 */
@RunWith(classOf[JUnitRunner])
@ContextConfiguration(classes = Array[Class[_]](classOf[SpringConfiguration]))
class DefaultFooServiceTest extends FunSuite with ShouldMatchers with SpringRule{

	@Resource
	private val fooService: FooService = null

	test("calling method from interface should apply transactional aspect") {
		fooService.inInterfaceTransactional()
	}

	test("calling non-transactional method from interface should start transaction for all called methods") {
		fooService.inInterfaceNotTransactional()
	}

}