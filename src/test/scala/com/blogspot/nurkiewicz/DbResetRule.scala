package com.blogspot.nurkiewicz

import org.springframework.transaction.support.AbstractPlatformTransactionManager
import java.io.File
import javax.sql.DataSource
import org.springframework.jdbc.core.{JdbcTemplate, JdbcOperations}
import java.util.Date
import org.scalatest.{BeforeAndAfterAll, Suite, BeforeAndAfterEach}
import javax.annotation.Resource


/**
 * @author Tomasz Nurkiewicz
 * @since 09.11.11, 22:27
 */

trait DbResetRule extends Suite with BeforeAndAfterEach with BeforeAndAfterAll { this: SpringRule =>

	@Resource val dataSource: DataSource = null

	val dbScriptFile = File.createTempFile(classOf[DbResetRule].getSimpleName + "-", ".sql")

	override protected def beforeAll() {
		val jdbc = new JdbcTemplate(dataSource)
		jdbc.execute("SCRIPT NOPASSWORDS DROP TO '" + dbScriptFile.getPath + "'")
		dbScriptFile.deleteOnExit()
		super.beforeAll()
	}

	override protected def afterEach() {
		super.afterEach()
		new JdbcTemplate(dataSource).execute("RUNSCRIPT FROM '" + dbScriptFile.getPath + "'")

	}

}

trait DbResetSpringRule extends DbResetRule with SpringRule
