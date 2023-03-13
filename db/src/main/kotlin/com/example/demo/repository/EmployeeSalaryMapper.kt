package com.example.demo.repository

import com.example.demo.entity.EmployeeSalary
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param

@Mapper
interface EmployeeSalaryMapper {
    fun upsert(@Param("entities") entities: List<EmployeeSalary>)
}