package com.example.demo.response

import com.example.demo.entity.Position
import com.fasterxml.jackson.annotation.JsonProperty

data class GetPositionResponse(
    val positions: List<PositionResponse>
) {
    data class PositionResponse(
        @JsonProperty("id")
        val positionId: Long,
        @JsonProperty("name")
        val positionName: String
    ) {
        companion object {
            fun of(entity: Position): PositionResponse {
                return PositionResponse(
                    positionId = entity.positionId,
                    positionName = entity.positionName
                )
            }
        }
    }
}
