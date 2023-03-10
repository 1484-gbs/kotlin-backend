package com.example.demo.usecase

import com.example.demo.entity.Skill
import com.example.demo.repository.SkillMapper
import com.example.demo.response.GetSkillPerTypeResponse
import com.example.demo.type.SkillType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface GetSkillPerTypeUseCase {
    fun execute(): GetSkillPerTypeResponse
}

@Service
@Transactional(readOnly = true)
class GetSkillPerTypeUseCaseImpl(
    private val skillMapper: SkillMapper
) : GetSkillPerTypeUseCase {
    override fun execute(): GetSkillPerTypeResponse {
        val skills = skillMapper.findAll()
        return GetSkillPerTypeResponse(
            skills = GetSkillPerTypeResponse.SkillPerTypeResponse(
                language = getSkill(skills, SkillType.LANGUAGE),
                framework = getSkill(skills, SkillType.FW),
                database = getSkill(skills, SkillType.DB),
                infra = getSkill(skills, SkillType.INFRA),
            )
        )
    }

    private fun getSkill(
        skills: List<Skill>,
        type: SkillType
    ): List<GetSkillPerTypeResponse.SkillPerTypeResponse.SkillResponse> {
        return skills.filter { s -> type == s.skillType }
            .map { GetSkillPerTypeResponse.SkillPerTypeResponse.SkillResponse.of(it) }
            .toList()
    }
}