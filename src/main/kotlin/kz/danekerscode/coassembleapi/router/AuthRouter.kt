package kz.danekerscode.coassembleapi.router//package kz.danekerscode.coassembleapi.router
//
//import kz.danekerscode.coassembleapi.model.dto.auth.LoginRequest
//import kz.danekerscode.coassembleapi.model.dto.auth.RegistrationRequest
//import kz.danekerscode.coassembleapi.service.AuthService
//import org.springframework.context.annotation.Bean
//import org.springframework.http.HttpStatus
//import org.springframework.stereotype.Component
//import org.springframework.web.reactive.function.server.ServerRequest
//import org.springframework.web.reactive.function.server.ServerResponse
//import org.springframework.web.reactive.function.server.router
//import reactor.core.publisher.Mono
//
//@Component
//class AuthRouter(
//    private val authService: AuthService
//) {
//
//    @Bean
//    fun authRoutes() = router {
//        ("/api/v1/auth").nest {
//            POST("/login", ::loginHandler)
//            POST("/register", ::registerHandler)
//        }
//    }
//
//    fun registerHandler(request: ServerRequest): Mono<ServerResponse> {
//        return request.bodyToMono(RegistrationRequest::class.java)
//            .flatMap { registerRequest ->
//                authService.register(registerRequest)
//                    .then(ServerResponse.status(HttpStatus.CREATED).build())
//            }
//    }
//
//
//    fun loginHandler(request: ServerRequest): Mono<ServerResponse> {
//        return request.bodyToMono(LoginRequest::class.java)
//            .flatMap { loginRequest ->
//                authService.login(loginRequest, request.exchange())
//                    .then(ServerResponse.ok().build())
//            }
//    }
//
//}