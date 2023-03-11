package com.example.demo.usecase

import com.example.demo.S3Client
import com.example.demo.exception.NotFoundException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.response.GetEmployeeResponse
import com.example.demo.type.S3FileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface GetEmployeeUseCase {
    fun execute(employeeId: Long): GetEmployeeResponse
}

@Service
@Transactional(readOnly = true)
class GetEmployeeUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val s3Client: S3Client,
) : GetEmployeeUseCase {
    override fun execute(employeeId: Long): GetEmployeeResponse {
        val employee = employeeMapper.findEmployeeAndSkillById(employeeId)
            ?: throw NotFoundException("employee not exists. id: $employeeId")
        val url = s3Client.getPresignedUrl(employee.employeeId.toString(), S3FileType.PHOTO.value)
        return GetEmployeeResponse.of(employee, url)
    }
}