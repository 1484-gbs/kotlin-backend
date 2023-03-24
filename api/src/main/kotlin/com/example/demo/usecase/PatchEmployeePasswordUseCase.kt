package com.example.demo.usecase

import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.InvalidRequestException
import com.example.demo.exception.NotFoundException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.request.PatchEmployeePasswordRequest
import com.example.demo.type.RoleType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

interface PatchEmployeePasswordUseCase {
    fun execute(id: Long, request: PatchEmployeePasswordRequest, user: UserDetailImpl.UserDetail)
}

@Service
@Transactional
class PatchEmployeePasswordUseCaseImpl(
    private val employeeMapper: EmployeeMapper,
) : PatchEmployeePasswordUseCase {
    override fun execute(id: Long, request: PatchEmployeePasswordRequest, user: UserDetailImpl.UserDetail) {
        val employee = employeeMapper.findById(id)
            ?: throw NotFoundException("employee not exists. id: $id")

        user.takeIf {
            it.role == RoleType.ADMIN || it.loginId == employee.loginId
        } ?: throw InvalidRequestException("can't update other people's information.")

        employeeMapper.updatePasswordAndTokenNullById(
            employeeId = id,
            updatedBy = user.loginId,
            password = BCryptPasswordEncoder().encode(request.password),
            now = LocalDateTime.now()
        )
    }
}