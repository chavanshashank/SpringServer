package com.server.suite

import com.server.database.AuthorizationCodeDatabaseTest
import com.server.database.ClientDatabaseTest
import com.server.database.TokenDatabaseTest
import com.server.database.UserDatabaseTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(TokenDatabaseTest::class, UserDatabaseTest::class, ClientDatabaseTest::class, AuthorizationCodeDatabaseTest::class)
class AllDatabaseTests