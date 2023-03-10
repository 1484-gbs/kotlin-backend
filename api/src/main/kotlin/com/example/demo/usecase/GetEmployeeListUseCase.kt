package com.example.demo.usecase

import com.example.demo.repository.EmployeeMapper
import com.example.demo.response.GetEmployeeListResponse
import com.example.demo.type.GenderType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface GetEmployeeListUseCase {
    fun execute(name: String?, kana: String?, gender: GenderType?, positionId: Long?): GetEmployeeListResponse
}

@Service
@Transactional(readOnly = true)
class GetEmployeeListUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper
) : GetEmployeeListUseCase {
    override fun execute(
        name: String?,
        kana: String?,
        gender: GenderType?,
        positionId: Long?
    ): GetEmployeeListResponse {
        return GetEmployeeListResponse(
            employees = employeeMapper.findList(name, kana, gender, positionId)
                .asSequence().map {
                    GetEmployeeListResponse.GetEmployeeList.of(it)
                }.toList()
        )
    }
}