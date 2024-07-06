package kz.danekerscode.coassembleapi.service.impl

import kz.danekerscode.coassembleapi.model.exception.EntityNotFoundException
import kz.danekerscode.coassembleapi.service.FileService
import kz.danekerscode.coassembleapi.utils.basicDbObject
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsResource
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class FileServiceImpl(
    private val gridFsOperations: GridFsOperations,
    private val gridFsTemplate: GridFsTemplate
) : FileService {

    override suspend fun uploadFile(file: MultipartFile): String = gridFsTemplate
        .store(
            file.inputStream,
            file.originalFilename,
            file.basicDbObject()
        ).toHexString()

    override suspend fun deleteFile(id: String) = gridFsOperations
        .delete(query(where("_id").`is`(id)))

    override suspend fun downloadFile(id: String): GridFsResource {
        val gridFSFile = gridFsOperations.findOne(query(where("_id").`is`(id)))
            ?: throw EntityNotFoundException(File::class.java, "File not found with id: $id")

        return gridFsTemplate.getResource(gridFSFile)
    }

}