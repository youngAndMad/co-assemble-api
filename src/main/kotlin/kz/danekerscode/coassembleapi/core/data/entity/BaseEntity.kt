package kz.danekerscode.coassembleapi.core.data.entity

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

abstract class BaseEntity {
    @CreatedDate
    var createdDate: LocalDateTime? = null

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
    var deletedDate: LocalDateTime? = null

    @Version
    var version: Long? = null
}
