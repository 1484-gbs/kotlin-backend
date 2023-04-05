package com.example.demo.response

import com.example.demo.entity.Skill
import com.fasterxml.jackson.annotation.JsonProperty

data class GetSkillPerTypeResponse(
    val skills: SkillPerTypeResponse
) {
    data class SkillPerTypeResponse(
        val language: List<SkillResponse>,
        val framework: List<SkillResponse>,
        val database: List<SkillResponse>,
        val infra: List<SkillResponse>
    ) {
        data class SkillResponse(
            @JsonProperty("id")
            val skillId: Long,
            @JsonProperty("name")
            val skillName: String
        ) {
            companion object {
                fun of(entity: Skill): SkillResponse {
                    return SkillResponse(
                        skillId = entity.skillId,
                        skillName = entity.skillName
                    )
                }
            }

        }
    }
}
