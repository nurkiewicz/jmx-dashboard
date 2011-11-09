package com.blogspot.nurkiewicz

import reflect.BeanProperty
import org.springframework.data.repository.PagingAndSortingRepository
import javax.persistence.{OneToMany, GeneratedValue, Id, Entity}
import java.{util => ju}
import java.{lang => jl}

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

	@OneToMany(mappedBy = "book")
	var reviews: ju.Set[Review] = new ju.HashSet()

}

trait BookDao extends PagingAndSortingRepository[Book, jl.Integer]