package com.server.repository

import com.server.MySpringBootTest
import com.server.repository.user.User
import com.server.repository.user.UserRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@MySpringBootTest
class UserRepositoryTest {

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

        val authorities = listOf("USER")
        val user = User("username", "pw")
        user.grantedAuthorities = authorities
        assertNotNull(userRepository.save(user).id)
        assertEquals(1, userRepository.count())

        val loaded = userRepository.findByIdOrNull(user.id)
        assertNotNull(loaded)
        assertEquals(user.id, loaded?.id)
        assertEquals(user.username, loaded?.username)
        assertEquals(user.password, loaded?.password)
        assertEquals(authorities.size, loaded?.authorities?.size)

        assertEquals(user.id, userRepository.findByUsername(user.username)?.id)
    }

    @Test
    fun testUpdate() {

        val user = User("username", "pw")
        assertNotNull(userRepository.save(user).id)
        assertEquals(1, userRepository.count())

        user.username = "updated-username-1"
        userRepository.save(user) // save updates (insert would create a new database object)
        assertEquals(1, userRepository.count())

        assertEquals("updated-username-1", userRepository.findByIdOrNull(user.id)?.username)
    }

    @Test
    fun testRemove() {

        val user1 = User("username1", "pw1")
        assertNotNull(userRepository.save(user1).id)
        assertEquals(1, userRepository.count())

        val user2 = User("username2", "pw2")
        assertNotNull(userRepository.save(user2).id)
        assertEquals(2, userRepository.count())

        userRepository.deleteById("unknown-id")
        assertEquals(2, userRepository.count())
        userRepository.delete(user1)
        assertEquals(1, userRepository.count())
        userRepository.deleteById(user2.id)
        assertEquals(0, userRepository.count())
    }
}