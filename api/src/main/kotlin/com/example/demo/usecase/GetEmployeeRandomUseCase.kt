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

        val employee = employeeMapper.findAll()
        employee.takeIf { e ->
            e.size < count
        }?.let { throw InvalidRequestException("count:$count is invalid. target employee count: ${employee.size}") }

       return GetEmployeeRandomResponse(
           employees = mutableListOf<GetEmployeeRandomResponse.GetEmployeeRandom>().apply {
               do {
                   val coordinator = employee[(employee.indices).random()]
                   this.firstOrNull { c -> c.employeeId == coordinator.employeeId }
                       ?: this.add(GetEmployeeRandomResponse.GetEmployeeRandom.of(coordinator))
               } while (this.size != count)
           }
       )
    }
}