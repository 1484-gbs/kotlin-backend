package com.example.demo.usecase

import com.example.demo.repository.PositionMapper
import com.example.demo.response.GetPositionResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface GetPositionUseCase {
    fun execute(): GetPositionResponse
}

@Service
@Transactional(readOnly = true)
class GetPositionUseCaseImpl(
    private val positionMapper: PositionMapper
) : GetPositionUseCase {
    override fun execute(): GetPositionResponse {
        return GetPositionResponse(
            positions = positionMapper.findAll().asSequence()
                .map { GetPositionResponse.PositionResponse.of(it) }
                .toList()
        )
    }
}