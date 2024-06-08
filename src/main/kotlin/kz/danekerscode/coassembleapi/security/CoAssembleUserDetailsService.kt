package kz.danekerscode.coassembleapi.security

import kz.danekerscode.coassembleapi.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CoAssembleUserDetailsService(
    val userRepository: UserRepository,
):UserDetailsService {
    val log: Logger = LoggerFactory.getLogger(this::class.java)

    override fun loadUserByUsername(username: String?): UserDetails {
        log.debug("Loading user by username: $username")

        return userRepository.findByEmail(username!!)
            .map { CoAssembleUserDetails(it) }
            .block()!!
    }

}