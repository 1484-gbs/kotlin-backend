package com.example.demo.repository

import com.example.demo.entity.OneTimeToken
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface OneTimeTokenMapper {
    fun create(@Param("entity") entity: OneTimeToken)
    fun deleteByEmployeeId(@Param("employeeId") employeeId: Long)
}