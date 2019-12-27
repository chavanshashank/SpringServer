package com.server.suite

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(AllUnitTests::class, AllDatabaseTests::class, AllResourceTests::class)
class AllTestSuites