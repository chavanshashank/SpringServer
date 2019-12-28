package com.server

import org.springframework.boot.test.context.SpringBootTest

/**
 * Custom annotation to enable @SpringBootTest and the same profile for all test cases.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@SpringBootTest
@MyActiveProfile
annotation class MySpringBootTest