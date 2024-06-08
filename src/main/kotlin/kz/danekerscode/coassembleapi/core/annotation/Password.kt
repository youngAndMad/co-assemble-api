package kz.danekerscode.coassembleapi.core.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [])
@Size(min = 6)
annotation class Password(
    val message: String = "Password must be at least 6 characters long",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

