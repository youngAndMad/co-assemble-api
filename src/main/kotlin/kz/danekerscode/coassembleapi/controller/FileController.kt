package kz.danekerscode.coassembleapi.controller

import jakarta.servlet.http.HttpServletResponse
import kz.danekerscode.coassembleapi.service.FileService
import org.springframework.core.io.InputStreamResource
import org.springframework.data.mongodb.gridfs.GridFsResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/files")
class FileController(
    private var fileService: FileService
) {

    @GetMapping("/download/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun downloadFile(
        @PathVariable id: String,
        response: HttpServletResponse
    ): ResponseEntity<InputStreamResource> {
        val resource = fileService.downloadFile(id)

        return constructContentResponse(resource, "attachment")
    }

    @GetMapping("/view/{id}", produces = [MediaType.APPLICATION_OCTET_STREAM_VALUE])
    suspend fun viewFile(
        @PathVariable id: String,
    ): ResponseEntity<InputStreamResource> {
        val resource = fileService.downloadFile(id)

        return constructContentResponse(resource, "inline")
    }

    private fun constructContentResponse(resource: GridFsResource, mode: String) = ResponseEntity.ok()
        .headers {
            it.set(HttpHeaders.CONTENT_DISPOSITION, "$mode; filename=\"${resource.filename}\"")
        }
        .contentLength(resource.contentLength())
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .body(InputStreamResource(resource.inputStream))
}