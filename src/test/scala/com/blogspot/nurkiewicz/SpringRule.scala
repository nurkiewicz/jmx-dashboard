package com.blogspot.nurkiewicz

import org.springframework.test.context.TestContextManager
import org.scalatest._

/**
 * @author Tomasz Nurkiewicz
 * @since 08.10.11, 00:06
 */

trait SpringRule extends AbstractSuite { this: Suite =>

	abstract override def run(testName: Option[String], reporter: Reporter, stopper: Stopper, filter: Filter, configMap: Map[String, Any], distributor: Option[Distributor], tracker: Tracker) {
		new TestContextManager(this.getClass).prepareTestInstance(this)
		super.run(testName, reporter, stopper, filter, configMap, distributor, tracker)
	}

}
