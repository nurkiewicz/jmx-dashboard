package com.blogspot.nurkiewicz

import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.springframework.test.context.ContextConfiguration
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import javax.annotation.Resource
import org.springframework.data.domain.PageRequest
import spring.SpringConfiguration

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 19:30
 */
@RunWith(classOf[JUnitRunner])
@ContextConfiguration(classes = Array[Class[_]](classOf[SpringConfiguration]))
class BookServiceTest extends FunSuite with ShouldMatchers with SpringRule{

	@Resource
	val bookService: BookService = null

	test("smoke test") {
		bookService listBooks new PageRequest(0, 100)
	}

}