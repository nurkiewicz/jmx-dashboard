package com.blogspot.nurkiewicz

import org.scalatest.matchers.ShouldMatchers
import org.springframework.test.context.ContextConfiguration
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import javax.annotation.Resource
import org.springframework.data.domain.PageRequest
import spring.SpringConfiguration
import org.fest.assertions.Assertions._
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import org.hibernate.LazyInitializationException
import java.util.concurrent.{Future, Callable, Executors, ExecutorService}
import collection.JavaConversions._

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 19:30
 */
@RunWith(classOf[JUnitRunner])
@ContextConfiguration(classes = Array[Class[_]](classOf[SpringConfiguration]))
class BookServiceTest extends FunSuite with ShouldMatchers with BeforeAndAfterAll with DbResetSpringRule {

	implicit def fun2Callable[T](fun: => T) = new Callable[T] {
		def call() = fun
	}

	@Resource
	val bookService: BookService = null

	@Resource
	private val reviewDao: ReviewDao = null

	private var executorService: ExecutorService = _

	override protected def beforeAll() {
		super.beforeAll()
		executorService = Executors.newFixedThreadPool(2)
	}

	override protected def afterAll() {
		executorService.shutdownNow()
		super.afterAll()
	}

	private def findAnyExistingBook() = bookService.listBooks(new PageRequest(0, 1)).getContent.get(0)

	test("should throw LazyInitializationException when fetching lazy one-to-many relationship") {
		val someBook = findAnyExistingBook()
		intercept[LazyInitializationException] {
			someBook.reviews.size
		}
	}

	test("external thread should see changes made in main thread") {
		val someBook = findAnyExistingBook()
		someBook.author = "Bruce Wayne"
		bookService.save(someBook)
		val foundBook = executorService.submit {
			bookService.findBy(someBook.id).get
		}.get
		foundBook.author should equal ("Bruce Wayne")
	}

	test("should not save and load changes made to not managed entity") {
		val unManagedBook = findAnyExistingBook()
		unManagedBook.author = "Clark Kent"
		val loadedBook = bookService.findBy(unManagedBook.id).get
		loadedBook.author should not equal ("Clark Kent")
	}

	test("should delete entity and throw an exception") {
		val someBook = findAnyExistingBook();

		intercept[OppsException] {
			bookService.deleteAndThrow(someBook);
		}

		bookService findBy someBook.id should be(None)

	}

	test("should store review in second thread") {
		val someBook = findAnyExistingBook()
		executorService.submit {
				reviewDao.save(new Review("Unicorn", "Excellent!!!1!", someBook))
			}.get
	}

	test("should not see review stored in previous test") {
		val reviews = reviewDao.findAll().toSeq
		reviews should have size (0)
	}

}