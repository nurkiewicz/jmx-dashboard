package com.blogspot.nurkiewicz

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.beans.factory.annotation.{Autowired, Configurable}

/**
 * @author Tomasz Nurkiewicz
 * @since 25.09.11, 18:51
 */
@Service
class DefaultFooService extends FooService {

	private def throwIfNotInTransaction() {
		assume(TransactionSynchronizationManager.isActualTransactionActive)
	}

	@Transactional
	private def privateMethod() {
		throwIfNotInTransaction()
	}

	@Transactional
	def publicNotInInterfaceButTransactional() {
		throwIfNotInTransaction()
	}

	@Transactional
	def publicNotInInterfaceAndNotTransactional() {
		inInterfaceTransactional()
		publicNotInInterfaceButTransactional()
		privateMethod();
	}

	@Transactional
	override def inInterfaceTransactional() {
		throwIfNotInTransaction()
	}

	override def inInterfaceNotTransactional() {
		inInterfaceTransactional()
		publicNotInInterfaceButTransactional()
		privateMethod();
	}
}
