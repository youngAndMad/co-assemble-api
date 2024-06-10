package kz.danekerscode.coassembleapi.core.helper

import reactor.core.publisher.Mono

/**
 * Interface for fetching user email from identity provider
 * */
interface IdentityProviderClient {

    /**
     * Fetch user email from identity provider
     * @param clientRegistrationId - client registration id
     * @param principalName - principal name
     * */
    fun getUserEmail(
        clientRegistrationId: String,
        principalName: String
    ): Mono<String>

}