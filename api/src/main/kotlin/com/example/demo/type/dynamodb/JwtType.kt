package com.example.demo.type.dynamodb

enum class JwtType(val value: String) {
    TABLE_NAME("jwt"),
    TOKEN_ID("token_id"),
    EMPLOYEE_ID("employee_id"),
    TOKEN("token"),
    TTL("ttl")
}