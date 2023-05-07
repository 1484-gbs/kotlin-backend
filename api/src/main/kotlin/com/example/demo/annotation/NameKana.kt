package com.example.demo.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@NotBlank
@Length(max = 20)
@Pattern(regexp = "^[ァ-ヶー]*\$", message = "全角カナで入力してください")
annotation class NameKana(
    val message: String = "message",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)
