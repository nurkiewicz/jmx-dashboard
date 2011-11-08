package com.blogspot.nurkiewicz

import reflect.BeanProperty
import javax.persistence.{GeneratedValue, Id, Entity}
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.transaction.annotation.Transactional

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 18:40
 */
@Entity
class Book(@BeanProperty var title: String, @BeanProperty var author: String, @BeanProperty var publishedYear: Int) {

	@Deprecated
	def this() {this("", "", 0)}

	@Id
	@GeneratedValue
	var id = 0

}

@Transactional
trait BookDao extends PagingAndSortingRepository[Book, java.lang.Integer]