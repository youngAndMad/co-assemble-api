package kz.danekerscode.coassembleapi.service

import org.springframework.data.mongodb.gridfs.GridFsResource
import org.springframework.web.multipart.MultipartFile

interface FileService {

    suspend fun uploadFile(file: MultipartFile): String

    suspend fun deleteFile(id: String)

    suspend fun downloadFile(id: String): GridFsResource
}
