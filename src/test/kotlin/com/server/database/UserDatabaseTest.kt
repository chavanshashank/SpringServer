package com.server.database

import com.server.database.user.User
import com.server.database.user.UserRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@SpringBootTest
class UserDatabaseTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userRepository.deleteAll()
        assertEquals(0, userRepository.count())
    }

    @After
    fun cleanup() {
        userRepository.deleteAll()
        assertEquals(0, userRepository.count())
    }

    @Test
    fun testStoreLoad() {

        val user = User("username", "pw")
        assertNotNull(userRepository.save(user).id)
        assertEquals(1, userRepository.count())

        val loaded = userRepository.findByIdOrNull(user.id)
        assertNotNull(loaded)
        assertEquals(user.id, loaded?.id)
        assertEquals(user.username, loaded?.username)
        assertEquals(user.password, loaded?.password)

        assertEquals(user.id, userRepository.findByUsername(user.username)?.id)
    }
}