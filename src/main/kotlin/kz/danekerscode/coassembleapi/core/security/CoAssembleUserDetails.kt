package kz.danekerscode.coassembleapi.core.security

import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.security.Principal

class CoAssembleUserDetails(
    val user: User,
) : UserDetails, Principal {
    override fun getAuthorities(): List<SimpleGrantedAuthority> =
        user.roles
            .map { SimpleGrantedAuthority(it.name) }

    override fun getPassword(): String = user.password!!

    override fun getUsername(): String = user.email

    /**
     * Returns the name of this principal.
     *
     * @return the name of this principal.
     */
    override fun getName(): String = user.email
}
