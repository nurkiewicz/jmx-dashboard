package com.blogspot.nurkiewicz

import javax.persistence.{Id, GeneratedValue, Entity, ManyToOne}
import org.springframework.data.repository.PagingAndSortingRepository
import java.{lang => jl}

/**
 * @author Tomasz Nurkiewicz
 * @since 08.11.11, 22:08
 */
@Entity
class Review(var author: String, var contents: String, _book: Book) {

	@deprecated
	def this() {this("", "", new Book("", "", 0))}

	@Id
	@GeneratedValue
	var id = 0

	@ManyToOne
	var book = _book

}

trait ReviewDao extends PagingAndSortingRepository[Review, jl.Integer]