package com.example.demo.usecase.common

import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.PositionMapper
import com.example.demo.repository.SkillMapper

abstract class AbstractEmployeeUseCase(
    private val skillMapper: SkillMapper,
    private val positionMapper: PositionMapper
) {
    fun validateSkill(skillIds: List<Long>) {
        skillIds.takeIf {
            it.isNotEmpty()
        }?.let {
            val skillEntities = skillMapper.findAll()
            it.forEach { skillId ->
                skillEntities.firstOrNull { se -> se.skillId == skillId }
                    ?: throw InvalidRequestException("invalid skill_id: $skillId.")
            }
        }
    }
    fun validatePosition(positionId: Long) {
        positionMapper.findAll().firstOrNull{ it.positionId == positionId }
            ?: throw InvalidRequestException("invalid position_id: $positionId.")
    }
}