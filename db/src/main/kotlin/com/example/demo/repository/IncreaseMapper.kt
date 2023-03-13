package com.example.demo.repository

import com.example.demo.entity.Increase
import org.apache.ibatis.annotations.Mapper

@Mapper
interface IncreaseMapper {
    fun findAll(): List<Increase>
}