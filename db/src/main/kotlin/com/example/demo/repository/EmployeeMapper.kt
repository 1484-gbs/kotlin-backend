package com.example.demo.repository

import com.example.demo.entity.Employee
import com.example.demo.entity.EmployeeAndSkill
import com.example.demo.entity.EmployeeList
import com.example.demo.type.GenderType
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface EmployeeMapper {
    fun create(@Param("entity") entity: Employee)
    fun findById(@Param("employeeId") employeeId: Long): Employee?
    fun update(@Param("entity") entity: Employee)
    fun delete(@Param("employeeId") employeeId: Long)
    fun findEmployeeAndSkillById(@Param("employeeId") employeeId: Long): EmployeeAndSkill?
    fun findList(
        @Param("name") name: String?,
        @Param("kana") kana: String?,
        @Param("gender") gender: GenderType?,
        @Param("positionId") positionId: Long?,
    ): List<EmployeeList>
}