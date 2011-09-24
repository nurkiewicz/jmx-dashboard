package com.blogspot.nurkiewicz

import org.springframework.context.annotation.{Configuration, Bean}

@Configuration
class SpringConfiguration {

	@Bean def foo() = 5

}