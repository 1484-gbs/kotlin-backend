package com.example.demo.usecase

import com.example.demo.client.S3Client
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.EmployeeSkillMapper
import com.example.demo.type.S3FileType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface DeleteEmployeeUseCase {
    fun execute(id: Long)
}

@Service
@Transactional
class DeleteEmployeeUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val employeeSkillMapper: EmployeeSkillMapper,
    private val s3Client: S3Client
) : DeleteEmployeeUseCase {
    override fun execute(id: Long) {
        employeeMapper.delete(id)
        employeeSkillMapper.deleteByEmployeeId(id)
        s3Client.delete(id.toString(), S3FileType.PHOTO.value)
    }
}