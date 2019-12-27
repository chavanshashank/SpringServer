package com.server.resources

import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class TestResourceTest : BaseResourceTest() {

    @Test
    fun test() {
        mvc.perform(get("/api/test")).andExpect(status().isOk)
    }
}