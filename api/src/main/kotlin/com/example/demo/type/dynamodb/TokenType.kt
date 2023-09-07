package com.example.demo.type.dynamodb

enum class TokenType(val value: String) {
    TABLE_NAME("token"),
    TOKEN_ID("token_id"),
    EMPLOYEE_ID("employee_id"),
    ACCESS_TOKEN("access_token"),
    REFRESH_TOKEN("refresh_token"),
    TTL("ttl")
}