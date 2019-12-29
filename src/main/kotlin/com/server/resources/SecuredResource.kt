package com.server.resources

import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/secured"])
class SecuredResource {

    @RequestMapping(method = [RequestMethod.GET])
    fun get(): String {
        return "Secured"
    }
}