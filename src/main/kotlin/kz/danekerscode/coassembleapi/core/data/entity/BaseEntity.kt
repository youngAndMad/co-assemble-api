package kz.danekerscode.coassembleapi.core.data.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.Version
import java.time.LocalDateTime

@JsonIgnoreProperties(value = ["deletedDate", "version"], allowGetters = true)
abstract class BaseEntity {
    @CreatedDate
    open var createdDate: LocalDateTime? = null

    @LastModifiedDate
    var lastModifiedDate: LocalDateTime? = null
    var deletedDate: LocalDateTime? = null

    @Version
    var version: Long? = null
}
