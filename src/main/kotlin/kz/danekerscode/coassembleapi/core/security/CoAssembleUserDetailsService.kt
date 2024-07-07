package kz.danekerscode.coassembleapi.core.security

import kotlinx.coroutines.runBlocking
import kz.danekerscode.coassembleapi.core.domain.errors.AuthProcessingException
import kz.danekerscode.coassembleapi.features.user.domain.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CoAssembleUserDetailsService(
    val userService: UserService,
) : UserDetailsService {

    override fun loadUserByUsername(userEmail: String?): UserDetails = runBlocking { // todo delete blocking
        userEmail ?: throw AuthProcessingException("Username is required.", HttpStatus.UNAUTHORIZED)

        val user = userService.findByEmail(userEmail) ?: throw AuthProcessingException(
            "Invalid credentials",
            HttpStatus.UNAUTHORIZED
        )

        if (!user.emailVerified) {
            throw AuthProcessingException("Email not verified", HttpStatus.UNAUTHORIZED)
        }

        CoAssembleUserDetails(user)
    }
}
