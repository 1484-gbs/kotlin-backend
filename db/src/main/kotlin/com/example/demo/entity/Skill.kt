package com.example.demo.entity

import com.example.demo.type.SkillType

data class Skill(
    val skillId: Long,
    val skillName: String,
    val skillType: SkillType,
    val displayOrder: Int
)

