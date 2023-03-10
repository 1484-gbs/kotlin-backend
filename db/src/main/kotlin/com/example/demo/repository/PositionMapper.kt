package com.example.demo.repository

import com.example.demo.entity.Position
import org.apache.ibatis.annotations.Mapper

@Mapper
interface PositionMapper {
    fun findAll(): List<Position>
}