package com.blogspot.nurkiewicz

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import javax.annotation.PostConstruct

/**
 * @author Tomasz Nurkiewicz
 * @since 30.10.11, 18:42
 */
@Service
@Transactional
class BookService @Autowired() (bookDao: BookDao) {

	@Deprecated
	def this() {this(null)}

	@PostConstruct
	def addBestSellers() {
		if(bookDao.count() == 0)
			insertBestSellers()
	}

	def insertBestSellers() {
		bookDao save new Book("A Tale of Two Cities", "Charles Dickens", 1859)
		bookDao save new Book("The Lord of the Rings", "J. R. R. Tolkien", 1954)
		bookDao save new Book("The Hobbit", "J. R. R. Tolkien", 1937)
		bookDao save new Book("红楼梦 (Dream of the Red Chamber)", "Cao Xueqin", 1759)
		bookDao save new Book("And Then There Were None", "Agatha Christie", 1939)
		bookDao save new Book("The Lion, the Witch and the Wardrobe", "C. S. Lewis", 1950)
		bookDao save new Book("She", "H. Rider Haggard", 1887)
		bookDao save new Book("Le Petit Prince (The Little Prince)", "Antoine de Saint-Exupéry", 1943)
		bookDao save new Book("The Da Vinci Code", "Dan Brown", 2003)
		bookDao save new Book("Think and Grow Rich", "Napoleon Hill", 1937)
		bookDao save new Book("The Catcher in the Rye", "J. D. Salinger", 1951)
		bookDao save new Book("O Alquimista (The Alchemist)", "Paulo Coelho", 1988)
		bookDao save new Book("Steps to Christ", "Ellen G. White", 1892)
		bookDao save new Book("Lolita", "Vladimir Nabokov", 1955)
		bookDao save new Book("Heidis Lehr- und Wanderjahre (Heidi's Years of Wandering and Learning)", "Johanna Spyri", 1880)
		bookDao save new Book("The Common Sense Book of Baby and Child Care", "Dr. Benjamin Spock", 1946)
		bookDao save new Book("Anne of Green Gables", "Lucy Maud Montgomery", 1908)
		bookDao save new Book("Black Beauty: His Grooms and Companions: The autobiography of a horse", "Anna Sewell", 1877)
		bookDao save new Book("Il Nome della Rosa (The Name of the Rose)", "Umberto Eco", 1980)
		bookDao save new Book("The Hite Report", "Shere Hite", 1976)
		bookDao save new Book("Charlotte's Web", "E.B. White; illustrated by Garth Williams", 1952)
		bookDao save new Book("The Tale of Peter Rabbit", "Beatrix Potter", 1902)
		bookDao save new Book("Harry Potter and the Deathly Hallows", "J. K. Rowling", 2007)
		bookDao save new Book("Jonathan Livingston Seagull", "Richard Bach", 1970)
		bookDao save new Book("A Message to Garcia", "Elbert Hubbard", 1899)
		bookDao save new Book("Angels and Demons", "Dan Brown", 2000)
		bookDao save new Book("Как закалялась сталь (Kak zakalyalas' stal'; How the Steel Was Tempered)", "Nikolai Ostrovsky", 1932)
		bookDao save new Book("Война и мир (Voyna i mir; War and Peace)", "Leo Tolstoy", 1869)
		bookDao save new Book("Le avventure di Pinocchio. Storia di un burattino (The Adventures of Pinocchio)", "Carlo Collodi", 1881)
		bookDao save new Book("You Can Heal Your Life", "Louise Hay", 1984)
		bookDao save new Book("Kane and Abel", "Jeffrey Archer", 1979)
		bookDao save new Book("Het Achterhuis (The Diary of a Young Girl, The Diary of Anne Frank)", "Anne Frank", 1947)
		bookDao save new Book("In His Steps: What Would Jesus Do?", "Charles M. Sheldon", 1896)
		bookDao save new Book("To Kill a Mockingbird", "Harper Lee", 1960)
		bookDao save new Book("Valley of the Dolls", "Jacqueline Susann", 1966)
		bookDao save new Book("Gone with the Wind", "Margaret Mitchell", 1936)
		bookDao save new Book("Cien Años de Soledad (One Hundred Years of Solitude)", "Gabriel García Márquez", 1967)
		bookDao save new Book("The Purpose Driven Life", "Rick Warren", 2002)
		bookDao save new Book("The Thorn Birds", "Colleen McCullough", 1977)
		bookDao save new Book("The Revolt of Mamie Stover", "William Bradford Huie", 1951)
		bookDao save new Book("The Girl with the Dragon Tattoo (original title: Män som hatar kvinnor)", "Stieg Larsson", 2005)
		bookDao save new Book("The Very Hungry Caterpillar", "Eric Carle", 1969)
		bookDao save new Book("Sophie's World", "Jostein Gaarder", 1991)
	}

	def deleteBy(id: Int) {bookDao delete id}

	def update(book: Book) = bookDao save book

	def save(book: Book) = bookDao save book

	def listBooks(page: PageRequest) = bookDao findAll page

	def findBy(id: Int) = Option(bookDao.findOne(id))

}