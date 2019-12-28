package com.server.resources

import com.server.repository.user.User
import com.server.repository.user.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/me"])
class OAuthResource: ResourceBase() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @RequestMapping(method = [RequestMethod.GET])
    fun me(): User? {
        return userRepository.findByUsername(currentUsername)
    }
}