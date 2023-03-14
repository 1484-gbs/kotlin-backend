package com.example.demo.controller

import com.example.demo.response.GetSkillPerTypeResponse
import com.example.demo.usecase.GetSkillPerTypeUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SkillController(
        private val getSkillPerTypeUseCase: GetSkillPerTypeUseCase
): AbstractController() {

    /**
     * スキルマスタ一覧取得
     */
    @GetMapping("/skill/per_type")
    @ResponseBody
    fun getSkillPerType(): GetSkillPerTypeResponse {
        return getSkillPerTypeUseCase.execute()
    }
}