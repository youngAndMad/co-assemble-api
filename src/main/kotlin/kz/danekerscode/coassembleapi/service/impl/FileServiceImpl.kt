package kz.danekerscode.coassembleapi.service.impl

import com.mongodb.BasicDBObject
import kz.danekerscode.coassembleapi.model.entity.CoAssembleFile
import kz.danekerscode.coassembleapi.service.FileService
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.ReactiveGridFsOperations
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


import  org.springframework.data.mongodb.core.query.Criteria.where;
import  org.springframework.data.mongodb.core.query.Query.query;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource


@Service
class FileServiceImpl(
    private val gridFsOperations: ReactiveGridFsOperations,
    private val gridFsTemplate: ReactiveGridFsTemplate
) : FileService {

    override fun uploadFile(filePart: Mono<FilePart>): Mono<String> =
        filePart.flatMap {
            val metaData = BasicDBObject().apply {
                put("type", "file")
                put("size", it.headers().contentLength)
                put("name", it.filename())
                put("extension", getFileExtension(it.filename()))
            }
            gridFsTemplate
                .store(it.content(), it.filename(), it.headers().contentType.toString(), metaData)
                .map { objectId -> objectId.toHexString() }
        }

    override fun deleteFile(id: String): Mono<Void> {
        TODO("Not yet implemented")
    }

    override fun downloadFile(id: String): Mono<ReactiveGridFsResource> =
        gridFsOperations.findOne(query(where("_id").`is`(id)))
            .switchIfEmpty(Mono.error(RuntimeException("File not found"))) // TODO: extend exception
            .flatMap { gridFsTemplate.getResource(it) }

    private fun getFileExtension(fileName: String?) = fileName?.substringAfterLast('.', "") ?: ""

}