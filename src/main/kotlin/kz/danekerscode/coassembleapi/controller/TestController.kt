package kz.danekerscode.coassembleapi.controller

import kz.danekerscode.coassembleapi.service.FileService
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.security.Principal

@RestController
class TestController(
    private val fileService: FileService
) {

    @GetMapping("/")
    fun test(principal: Principal) = principal

    @PostMapping("/test1")
    fun test1(
        @RequestPart filePart: Mono<FilePart>
    ) = fileService.uploadFile(filePart)

    @GetMapping("/test2")
    fun test2(
        @RequestParam id: String,
        exchange: ServerWebExchange
    ) = fileService.downloadFile(id)
        .flatMapMany {
            exchange.response.headers.set("Content-Disposition", "attachment; filename=\"${it.filename}\"")
            exchange.response.writeWith(it.downloadStream)
        }
}