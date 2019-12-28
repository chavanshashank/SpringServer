package com.server.auth.clientdetails

import com.server.repository.client.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService

class OAuthClientDetailsService : ClientDetailsService {

    @Autowired
    private lateinit var clientRepository: ClientRepository

    override fun loadClientByClientId(clientId: String): ClientDetails? {
        return clientRepository.findByIdOrNull(clientId)
    }
}