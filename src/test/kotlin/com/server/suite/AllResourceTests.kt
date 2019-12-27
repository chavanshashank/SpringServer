package com.server.suite

import com.server.resources.OAuthResourceTest
import com.server.resources.SecuredResourceTest
import com.server.resources.TestResourceTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(OAuthResourceTest::class, TestResourceTest::class, SecuredResourceTest::class)
class AllResourceTests