package com.example.demo.repository

import com.example.demo.entity.EmployeeSkill
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface EmployeeSkillMapper {
    fun bulkInsert(@Param("entities") entities: List<EmployeeSkill>)
    fun deleteByEmployeeId(@Param("employeeId") employeeId: Long)
    fun deleteByEmployeeIdAndSkillIds(@Param("employeeId") employeeId: Long, @Param("skillIds") skillIds: List<Long>)
}