package com.server.resources

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.server.MyActiveProfile
import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext


@RunWith(SpringRunner::class)
@SpringBootTest
@MyActiveProfile
abstract class BaseResourceTest {

    @Autowired
    protected lateinit var webApplicationContext: WebApplicationContext

    private val mapper = ObjectMapper()
    protected val jsonContent = MediaType.APPLICATION_JSON

    protected lateinit var mvc: MockMvc

    @Before
    fun setup() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build()
    }

    protected fun toJson(`object`: Any): String? {
        return try {
            mapper.writeValueAsString(`object`)
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
            null
        }
    }
}