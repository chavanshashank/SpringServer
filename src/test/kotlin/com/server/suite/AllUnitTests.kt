package com.server.suite

import com.server.unit.AuthenticationSerializerTest
import com.server.unit.AuthorizationCodeTest
import com.server.unit.CryptoTest
import com.server.unit.PropertyFileTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(PropertyFileTest::class, CryptoTest::class, AuthenticationSerializerTest::class, AuthorizationCodeTest::class)
class AllUnitTests