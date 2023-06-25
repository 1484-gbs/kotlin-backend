package com.example.demo.repository

import com.example.demo.entity.*
import com.example.demo.type.GenderType
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import java.time.LocalDateTime

@Mapper
interface EmployeeMapper {
    fun create(@Param("entity") entity: Employee)

    fun findById(@Param("employeeId") employeeId: Long): Employee?

    fun update(@Param("entity") entity: Employee)

    fun updatePasswordAndTokenNullById(
        @Param("employeeId") employeeId: Long,
        @Param("updatedBy") updatedBy: String,
        @Param("password") password: String,
        @Param("now") now: LocalDateTime
    )

    fun updateTokenById(
        @Param("employeeId") employeeId: Long,
        @Param("token") token: String,
        @Param("updatedBy") updatedBy: String,
        @Param("now") now: LocalDateTime
    )

    fun delete(@Param("employeeId") employeeId: Long)

    fun findEmployeeAndSkillById(@Param("employeeId") employeeId: Long): EmployeeAndSkill?

    fun findList(
        @Param("name") name: String?,
        @Param("kana") kana: String?,
        @Param("gender") gender: GenderType?,
        @Param("positionId") positionId: Long?,
    ): List<EmployeeList>

    fun findAll(): List<Employee>

    fun findByLoginId(@Param("loginId") loginId: String): Employee?

    fun findByTokenId(@Param("tokenId") tokenId: String): EmployeeRole?

    fun findByLoginIdValidToken(
        @Param("loginId") loginId: String,
        @Param("otpReqId") otpReqId: String,
        @Param("now") now: LocalDateTime
    ): EmployeeTwoFactorAuth?
}