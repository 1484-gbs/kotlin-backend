package com.example.demo.usecase

import com.example.demo.client.S3Client
import com.example.demo.dto.UserDetailImpl
import com.example.demo.entity.Employee
import com.example.demo.entity.EmployeeSkill
import com.example.demo.exception.InvalidRequestException
import com.example.demo.exception.NotFoundException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.EmployeeSkillMapper
import com.example.demo.repository.PositionMapper
import com.example.demo.repository.SkillMapper
import com.example.demo.request.PatchEmployeeRequest
import com.example.demo.type.GenderType
import com.example.demo.type.RoleType
import com.example.demo.type.S3FileType
import com.example.demo.usecase.common.AbstractEmployeeUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

interface PatchEmployeeUseCase {
    fun execute(id: Long, request: PatchEmployeeRequest, user: UserDetailImpl.UserDetail)
}

@Service
@Transactional
class PatchEmployeeUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    val skillMapper: SkillMapper,
    val positionMapper: PositionMapper,
    private val employeeSkillMapper: EmployeeSkillMapper,
    private val s3Client: S3Client
) : PatchEmployeeUseCase, AbstractEmployeeUseCase(skillMapper, positionMapper) {
    override fun execute(id: Long, request: PatchEmployeeRequest, user: UserDetailImpl.UserDetail) {
        val employee = employeeMapper.findEmployeeAndSkillById(id)
            ?: throw NotFoundException("employee not exists. id: $id.")

        // ADMIN can update all user. other user can update just me.
        user.takeIf {
            it.role == RoleType.ADMIN || it.loginId == employee.loginId
        } ?: throw InvalidRequestException("can't update other people's information.")

        super.validatePosition(request.positionId)
        super.validateSkill(request.skills)
        val now = LocalDateTime.now()

        employeeMapper.update(
            Employee(
                employeeId = id,
                firstName = request.firstName,
                lastName = request.lastName,
                firstNameKana = request.firstNameKana,
                lastNameKana = request.lastNameKana,
                birthday = LocalDate.MIN, // Mapperでupdateしない
                gender = GenderType.FEMALE, // Mapperでupdateしない
                tel = request.tel,
                positionId = request.positionId,
                salaryOfMonth = if (
                    user.role == RoleType.ADMIN
                    && request.salaryOfMonth != null
                ) request.salaryOfMonth
                else 0, // only ADMIN can update salary.
                loginId = "", // Mapperでupdateしない
                password = "", // Mapperでupdateしない
                tokenId = "", // Mapperでupdateしない
                createdBy = user.loginId,
                createdAt = now,
                updatedBy = user.loginId,
                updatedAt = now,
            )
        )

        val insertSkill = request.skills.filter { s -> !employee.skills.contains(s) }
        val deleteSkill = employee.skills.filter { s -> !request.skills.contains(s) }

        deleteSkill.takeIf {
            it.isNotEmpty()
        }?.let {
            employeeSkillMapper.deleteByEmployeeIdAndSkillIds(
                employeeId = id,
                skillIds = it
            )
        }

        insertSkill.takeIf {
            it.isNotEmpty()
        }?.let {
            employeeSkillMapper.bulkInsert(
                it.asSequence().map { skillId ->
                    EmployeeSkill(
                        employeeSkillId = 0, // auto_increment
                        employeeId = id,
                        skillId = skillId,
                        lastModifiedBy = user.loginId,
                        lastModifiedAt = now,
                    )
                }.toList()
            )
        }

        request.photo?.let {
            s3Client.upload(it, id.toString(), S3FileType.PHOTO.value)
        } ?: run {
            request.isDeletePhoto.takeIf {
                it == true
            }?.let {
                s3Client.delete(id.toString(), S3FileType.PHOTO.value)
            }
        }
    }
}