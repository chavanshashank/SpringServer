package com.server

import org.springframework.test.context.ActiveProfiles

/**
 * Custom annotation to enable the same profile for all test cases.
 */
@ActiveProfiles(profiles = ["test"])
annotation class MyActiveProfile