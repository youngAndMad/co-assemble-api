package kz.danekerscode.coassembleapi.core.annotation

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import jakarta.validation.constraints.Size
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordValidator::class])
@Size(min = 6)
annotation class Password(
    val message: String = "Password must be at least 6 characters long",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)

class PasswordValidator : ConstraintValidator<Password, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ) = (value?.length ?: 0) >= 6
}
