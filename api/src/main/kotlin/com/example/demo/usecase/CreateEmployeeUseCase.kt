package com.example.demo.usecase

import com.example.demo.client.S3Client
import com.example.demo.entity.Employee
import com.example.demo.entity.EmployeeSkill
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.EmployeeSkillMapper
import com.example.demo.repository.PositionMapper
import com.example.demo.repository.SkillMapper
import com.example.demo.request.CreateEmployeeRequest
import com.example.demo.response.CreateEmployeeResponse
import com.example.demo.type.S3FileType
import com.example.demo.usecase.common.AbstractEmployeeUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

interface CreateEmployeeUseCase {
    fun execute(request: CreateEmployeeRequest): CreateEmployeeResponse
}

@Service
@Transactional
class CreateEmployeeUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    private val employeeSkillMapper: EmployeeSkillMapper,
    val skillMapper: SkillMapper,
    val positionMapper: PositionMapper,
    private val s3Client: S3Client
) : CreateEmployeeUseCase, AbstractEmployeeUseCase(
    skillMapper,
    positionMapper
) {
    override fun execute(request: CreateEmployeeRequest): CreateEmployeeResponse {
        super.validatePosition(request.positionId)
        super.validateSkill(request.skills)

        val employee = Employee(
            employeeId = 0, // auto_increment
            firstName = request.firstName,
            lastName = request.lastName,
            firstNameKana = request.firstNameKana,
            lastNameKana = request.lastNameKana,
            birthday = CreateEmployeeRequest.Birthday.getLocalDate(request.birthDay),
            gender = request.gender,
            tel = request.tel,
            positionId = request.positionId,
            salaryOfMonth =  request.salaryOfMonth
        )

        employeeMapper.create(employee)

        request.skills.takeIf {
            it.isNotEmpty()
        }?.let {
            employeeSkillMapper.bulkInsert(
                request.skills.asSequence().map {
                    EmployeeSkill(
                        employeeSkillId = 0, // auto_increment
                        employeeId = employee.employeeId,
                        skillId = it
                    )
                }.toList()
            )
        }

        request.photo?.let {
            s3Client.upload(request.photo, employee.employeeId.toString(), S3FileType.PHOTO.value)
        }

        return CreateEmployeeResponse(employee.employeeId)
    }
}