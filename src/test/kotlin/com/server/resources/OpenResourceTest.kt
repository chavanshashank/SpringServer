package com.server.resources

import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class OpenResourceTest : BaseResourceTest() {

    @Test
    fun test() {
        mvc.perform(get("/api/open")).andExpect(status().isOk)
    }
}