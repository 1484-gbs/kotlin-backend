package com.example.demo.annotation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import org.hibernate.validator.constraints.Length
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [])
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Length(max = 15)
annotation class Tel(
    val message: String = "message",
    val groups: Array<KClass<out Any>> = [],
    val payload: Array<KClass<out Payload>> = []
)
