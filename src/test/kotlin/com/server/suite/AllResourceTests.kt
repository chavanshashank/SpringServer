package com.server.suite

import com.server.resources.OAuthTest
import com.server.resources.SecuredResourceTest
import com.server.resources.OpenResourceTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(OAuthTest::class, OpenResourceTest::class, SecuredResourceTest::class)
class AllResourceTests