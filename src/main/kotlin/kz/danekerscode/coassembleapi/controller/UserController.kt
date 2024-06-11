package kz.danekerscode.coassembleapi.controller

import kz.danekerscode.coassembleapi.security.CoAssembleUserDetails
import kz.danekerscode.coassembleapi.service.UserService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/avatar")
    fun uploadAvatar(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails,
        @RequestPart avatar: Mono<FilePart>
    ) = userService.uploadAvatar(currentUser, avatar)

    @DeleteMapping("/avatar")
    fun deleteAvatar(
        @AuthenticationPrincipal currentUser: CoAssembleUserDetails
    ) = userService.deleteAvatar(currentUser)

}