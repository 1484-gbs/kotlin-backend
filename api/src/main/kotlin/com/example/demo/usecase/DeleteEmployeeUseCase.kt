package com.example.demo.usecase

import com.example.demo.repository.EmployeeMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface DeleteEmployeeUseCase {
    fun execute(id: Long)
}

@Service
@Transactional
class DeleteEmployeeUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper
) : DeleteEmployeeUseCase {
    override fun execute(id: Long) {
        employeeMapper.delete(id)
    }
}