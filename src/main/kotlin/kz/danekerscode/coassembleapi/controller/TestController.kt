package kz.danekerscode.coassembleapi.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal

@RestController
class TestController {

    @GetMapping("/")
    fun test(principal: Principal) = principal
}