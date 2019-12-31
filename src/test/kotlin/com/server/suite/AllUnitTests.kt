package com.server.suite

import com.server.unit.*
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(PropertyFileTest::class, CryptoTest::class, PasswordEncoderTest::class, AuthenticationSerializerTest::class, AuthorizationCodeTest::class)
class AllUnitTests