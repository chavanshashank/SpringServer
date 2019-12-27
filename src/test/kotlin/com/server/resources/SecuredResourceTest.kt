package com.server.resources

import org.junit.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class SecuredResourceTest : BaseResourceTest() {

    @Test
    fun test() {
        mvc.perform(get("/api/secured")).andExpect(status().isOk)
    }
}