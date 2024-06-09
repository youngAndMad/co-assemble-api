package kz.danekerscode.coassembleapi.controller

import kz.danekerscode.coassembleapi.service.FileService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private var fileService: FileService
) {

    @GetMapping("/download/{id}")
    fun downloadFile(
        @PathVariable id: String,
        exchange: ServerWebExchange
    ) = fileService.downloadFile(id)
        .flatMapMany {
            exchange.response.headers.set("Content-Disposition", "attachment; filename=\"${it.filename}\"")
            exchange.response.writeWith(it.downloadStream)
        }

    @GetMapping("/view/{id}")
    fun viewFile(
        @PathVariable id: String,
        exchange: ServerWebExchange
    ) = fileService.downloadFile(id)
        .flatMapMany {
            exchange.response.headers.set("Content-Disposition", "inline; filename=\"${it.filename}\"")
            exchange.response.writeWith(it.downloadStream)
        }
}