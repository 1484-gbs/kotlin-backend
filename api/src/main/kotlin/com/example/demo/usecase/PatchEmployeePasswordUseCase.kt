package com.example.demo.usecase

import com.example.demo.repository.EmployeeMapper
import com.example.demo.request.PatchEmployeePasswordRequest
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface PatchEmployeePasswordUseCase {
    fun execute(request: PatchEmployeePasswordRequest, loginId: String)
}

@Service
@Transactional
class PatchEmployeePasswordUseCaseImpl(
    private val employeeMapper: EmployeeMapper,
) : PatchEmployeePasswordUseCase {
    override fun execute(request: PatchEmployeePasswordRequest, loginId: String) {
        employeeMapper.updatePasswordByLoginId(
            loginId = loginId,
            password = BCryptPasswordEncoder().encode(request.password),
            now = LocalDateTime.now()
        )
    }
}