package com.example.demo.type.dynamodb

enum class OneTimeTokenType(val value: String) {
    TABLE_NAME("one_time_token"),
    EMPLOYEE_ID("employee_id"),
    ONE_TIME_TOKEN("one_time_token"),
    OPT_REQ_ID("otp_req_id"),
    TTL("ttl")
}