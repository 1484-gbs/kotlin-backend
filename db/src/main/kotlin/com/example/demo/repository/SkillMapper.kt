package com.example.demo.repository

import com.example.demo.entity.Skill
import org.apache.ibatis.annotations.Mapper

@Mapper
interface SkillMapper {
    fun findAll(): List<Skill>
}