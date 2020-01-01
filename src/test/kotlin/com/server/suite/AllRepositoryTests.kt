package com.server.suite

import com.server.repository.AuthorizationCodeRepositoryTest
import com.server.repository.ClientRepositoryTest
import com.server.repository.UserRepositoryTest
import com.server.repository.token.AccessTokenRepositoryTest
import com.server.repository.token.MongoTokenStoreTest
import com.server.repository.token.RefreshTokenRepositoryTest
import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses

@RunWith(Suite::class)
@SuiteClasses(AccessTokenRepositoryTest::class, RefreshTokenRepositoryTest::class, MongoTokenStoreTest::class, UserRepositoryTest::class, ClientRepositoryTest::class, AuthorizationCodeRepositoryTest::class)
class AllRepositoryTests