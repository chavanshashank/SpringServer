package com.server.auth.clientdetails

import com.server.repository.client.ClientRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.oauth2.provider.ClientDetails
import org.springframework.security.oauth2.provider.ClientDetailsService
import org.springframework.security.oauth2.provider.ClientRegistrationException

class OAuthClientDetailsService : ClientDetailsService {

    @Autowired
    private lateinit var clientRepository: ClientRepository

    override fun loadClientByClientId(clientId: String?): ClientDetails {
        val client = clientRepository.findByIdOrNull(clientId)
        if (client == null) {
            throw ClientRegistrationException("No client found for id $clientId")
        } else {
            return client
        }
    }
}