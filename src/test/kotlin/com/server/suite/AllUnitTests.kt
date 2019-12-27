package com.server.suite

import com.server.unit.CryptoTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(CryptoTest::class)
class AllUnitTests