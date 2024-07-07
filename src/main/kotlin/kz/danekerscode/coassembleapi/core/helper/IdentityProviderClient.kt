package kz.danekerscode.coassembleapi.core.helper

/**
 * Interface for fetching user email from identity provider
 * */
interface IdentityProviderClient {

    /**
     * Fetch user email from identity provider
     * @param clientRegistrationId - client registration id
     * @param principalName - principal name
     * */
    suspend fun getUserEmail(
        clientRegistrationId: String,
        principalName: String
    ): String?

}