package kz.danekerscode.coassembleapi.model.entity

import java.io.InputStream
import java.util.UUID

class CoAssembleFile (
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val size: Long,
    val extension: String,
    val stream: InputStream
)