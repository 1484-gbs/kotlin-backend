package com.example.demo.usecase

import com.example.demo.client.DynamoDBClient
import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.InvalidRequestException
import com.example.demo.exception.NotFoundException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.request.PatchEmployeePasswordRequest
import com.example.demo.type.RoleType
import com.example.demo.type.dynamodb.JwtType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.LocalDateTime

interface PatchEmployeePasswordUseCase {
    fun execute(id: Long, request: PatchEmployeePasswordRequest, user: UserDetailImpl.UserDetail)
}

@Service
@Transactional
class PatchEmployeePasswordUseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val dynamoDBClient: DynamoDBClient,
) : PatchEmployeePasswordUseCase {
    override fun execute(id: Long, request: PatchEmployeePasswordRequest, user: UserDetailImpl.UserDetail) {
        val employee = employeeMapper.findById(id)
            ?: throw NotFoundException("employee not exists. id: $id")

        user.takeIf {
            it.role == RoleType.ADMIN || it.loginId == employee.loginId
        } ?: throw InvalidRequestException("can't update other people's information.")

        employeeMapper.updatePasswordById(
            employeeId = id,
            updatedBy = user.loginId,
            password = BCryptPasswordEncoder().encode(request.password),
            now = LocalDateTime.now()
        )

        dynamoDBClient.deleteItem(
            JwtType.TABLE_NAME.value,
            mapOf(
                JwtType.TOKEN_ID.value
                to AttributeValue.builder().s(employee.tokenId).build()
            )
        )
    }
}