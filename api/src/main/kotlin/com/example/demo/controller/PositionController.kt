package com.example.demo.controller

import com.example.demo.response.GetPositionResponse
import com.example.demo.usecase.GetPositionUseCase
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class PositionController(
        private val getPositionUseCase: GetPositionUseCase
): AbstractController() {

    /**
     * 役職マスタ一覧取得
     */
    @GetMapping("/position")
    @ResponseBody
    fun getPositions(): GetPositionResponse {
        return getPositionUseCase.execute()
    }
}