import com.blogspot.nurkiewicz.*;
import com.blogspot.nurkiewicz.spring.SpringConfiguration;
import org.hibernate.LazyInitializationException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import scala.Option;

import javax.annotation.Resource;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.Fail.fail;

/**
 * @author Tomasz Nurkiewicz
 * @since 08.11.11, 22:19
 */
@Transactional
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = SpringConfiguration.class)
public class BookServiceTransactionalTest {

	@Resource
	private BookService bookService;

	@Resource
	private ReviewDao reviewDao;

	private ExecutorService executorService;

	@Before
	public void startThreadPool() {
		executorService = Executors.newFixedThreadPool(2);
	}

	@After
	public void shutDownThreadPool() {
		executorService.shutdownNow();
	}

	private Book findAnyExistingBook() {
		return bookService.listBooks(new PageRequest(0, 1)).getContent().get(0);
	}

	@Test
	public void shouldThrowLazyInitializationExceptionWhenFetchingLazyOneToManyRelationship() throws Exception {
		//given
		final Book someBook = findAnyExistingBook();

		//when
		try {
			someBook.reviews().size();
			fail();
		} catch(LazyInitializationException e) {
			//then
		}
	}

	@Test
	public void externalThreadShouldSeeChangesMadeInMainThread() throws Exception {
		//given
		final Book someBook = findAnyExistingBook();
		someBook.setAuthor("Bruce Wayne");
		bookService.save(someBook);

		//when
		final Future<Book> future = executorService.submit(new Callable<Book>() {
			@Override
			public Book call() throws Exception {
				return bookService.findBy(someBook.id()).get();
			}
		});

		//then
		assertThat(future.get().getAuthor()).isEqualTo("Bruce Wayne");
	}

	@Test
	public void shouldNotSaveAndLoadChangesMadeToNotManagedEntity() throws Exception {
		//given
		final Book unManagedBook = findAnyExistingBook();
		unManagedBook.setAuthor("Clark Kent");

		//when
		final Book loadedBook = bookService.findBy(unManagedBook.id()).get();

		//then
		assertThat(loadedBook.getAuthor()).isNotEqualTo("Clark Kent");
	}

	@Test
	public void shouldDeleteEntityAndThrowAnException() throws Exception {
		//given
		final Book someBook = findAnyExistingBook();

		try {
			//when
			bookService.deleteAndThrow(someBook);
			fail();
		} catch (OppsException e) {
			//then
			final Option<Book> deletedBook = bookService.findBy(someBook.id());
			assertThat(deletedBook.isEmpty()).isTrue();
		}

	}

	@Test
	public void shouldStoreReviewInSecondThread() throws Exception {
		//given
		final Book someBook = findAnyExistingBook();

		//when
		executorService.submit(new Callable<Review>() {
			@Override
			public Review call() throws Exception {
				return reviewDao.save(new Review("Unicorn", "Excellent!!!1!", someBook));
			}
		}).get();

		//then
	}

	@Test
	public void shouldNotSeeReviewStoredInPreviousTest() throws Exception {
		//given

		//when
		final Iterable<Review> reviews = reviewDao.findAll();

		//then
		assertThat(reviews).isEmpty();
	}

}
