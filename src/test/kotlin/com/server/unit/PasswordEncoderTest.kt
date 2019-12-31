package com.server.unit

import com.server.MySpringBootTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner::class)
@MySpringBootTest
class PasswordEncoderTest {

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Test
    fun testEncode() {

        val string = "123456&$.abc"

        val encoded1 = passwordEncoder.encode(string)
        val encoded2 = passwordEncoder.encode(string)
        assertFalse(encoded1 == encoded2)
        assertTrue(passwordEncoder.matches(string, encoded1))
        assertTrue(passwordEncoder.matches(string, encoded2))
        assertFalse(passwordEncoder.matches(encoded1, string))
        assertFalse(passwordEncoder.matches(encoded2, string))
        assertFalse(string == encoded1)
        assertFalse(string == encoded2)
    }
}