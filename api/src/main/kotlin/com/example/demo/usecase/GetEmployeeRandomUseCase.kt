package com.example.demo.usecase

import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.response.GetEmployeeRandomResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface GetEmployeeRandomUseCase {
    fun execute(count: Int): GetEmployeeRandomResponse
}

@Service
@Transactional(readOnly = true)
class GetEmployeeRandomUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper
) : GetEmployeeRandomUseCase {
    override fun execute(count: Int): GetEmployeeRandomResponse {
        count.takeIf { it > 0 }
            ?: throw InvalidRequestException("count:$count is invalid.")

        val employee = employeeMapper.findAll().also {
            if (it.size < count)
                throw InvalidRequestException("count:$count is invalid. target employee count: ${it.size}")
        }

        return GetEmployeeRandomResponse(
            employees = mutableListOf<GetEmployeeRandomResponse.GetEmployeeRandom>().apply {
                do {
                    val randomEmployee = employee[(employee.indices).random()]
                    this.firstOrNull { e -> e.employeeId == randomEmployee.employeeId }
                        ?: this.add(GetEmployeeRandomResponse.GetEmployeeRandom.of(randomEmployee))
                } while (this.size != count)
            }
        )
    }
}