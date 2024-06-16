package kz.danekerscode.coassembleapi.service.impl


import com.mongodb.BasicDBObject
import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import kz.danekerscode.coassembleapi.service.FileService
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.gridfs.ReactiveGridFsOperations
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.io.File


@Service
class FileServiceImpl(
    private val gridFsOperations: ReactiveGridFsOperations,
    private val gridFsTemplate: ReactiveGridFsTemplate
) : FileService {

    override fun uploadFile(filePart: Mono<FilePart>): Mono<String> =
        filePart.flatMap {
            gridFsTemplate
                .store(it.content(), it.filename(), it.headers().contentType.toString(), basicDBObjectFromFilePart(it))
                .map { objectId -> objectId.toHexString() }
        }

    private fun basicDBObjectFromFilePart(it: FilePart): BasicDBObject =
        BasicDBObject().apply {
            put("type", "file")
            put("size", it.headers().contentLength)
            put("name", it.filename())
            put("extension", getFileExtension(it.filename()))
        }

    override fun deleteFile(id: String): Mono<Void> {
        val query = query(where("_id").`is`(id))
        return gridFsOperations
            .findOne(query)
            .switchIfEmpty(Mono.error(EntityNotFoundException(File::class.java, id)))
            .flatMap { gridFsOperations.delete(query) }
    }

    override fun downloadFile(id: String): Mono<ReactiveGridFsResource> =
        gridFsOperations.findOne(query(where("_id").`is`(id)))
            .switchIfEmpty(Mono.error(EntityNotFoundException(File::class.java, id)))
            .flatMap { gridFsTemplate.getResource(it) }

    private fun getFileExtension(fileName: String?) = fileName?.substringAfterLast('.', "") ?: ""

}