package kz.danekerscode.coassembleapi.service

import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Mono

interface FileService {

    fun uploadFile(filePart: Mono<FilePart>): Mono<String>

    fun deleteFile(id: String): Mono<Void>

    fun downloadFile(id: String): Mono<ReactiveGridFsResource>
}
