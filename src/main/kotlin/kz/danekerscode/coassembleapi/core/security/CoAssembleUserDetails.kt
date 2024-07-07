package kz.danekerscode.coassembleapi.core.security

import kz.danekerscode.coassembleapi.features.user.data.entity.User
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CoAssembleUserDetails(
    val user: User
) : UserDetails {
    override fun getAuthorities(): List<SimpleGrantedAuthority> = user.roles
        .map { SimpleGrantedAuthority(it.name) }

    override fun getPassword(): String = user.password!!

    override fun getUsername(): String = user.email
}
