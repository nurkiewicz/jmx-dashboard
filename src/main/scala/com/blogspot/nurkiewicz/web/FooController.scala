package com.blogspot.nurkiewicz.web

import org.springframework.web.bind.annotation.{PathVariable, RequestMapping, ResponseBody, RequestMethod}
import org.springframework.stereotype.{Service, Controller}

/**
 * @author Tomasz Nurkiewicz
 * @since 24.09.11, 23:39
 */
@Controller
class FooController {

	println("FooController")

	@RequestMapping(value = Array("/echo/{payload}"), method=Array(RequestMethod.GET))
	@ResponseBody
	def echo(@PathVariable("payload") payload: String) = payload

}