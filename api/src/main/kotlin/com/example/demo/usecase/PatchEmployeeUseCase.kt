package com.example.demo.usecase

import com.example.demo.entity.Employee
import com.example.demo.entity.EmployeeSkill
import com.example.demo.exception.NotFoundException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.repository.EmployeeSkillMapper
import com.example.demo.repository.PositionMapper
import com.example.demo.repository.SkillMapper
import com.example.demo.request.PatchEmployeeRequest
import com.example.demo.type.GenderType
import com.example.demo.usecase.common.AbstractEmployeeUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

interface PatchEmployeeUseCase {
    fun execute(id: Long, request: PatchEmployeeRequest)
}

@Service
@Transactional
class PatchEmployeeUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper,
    val skillMapper: SkillMapper,
    val positionMapper: PositionMapper,
    private val employeeSkillMapper: EmployeeSkillMapper
) : PatchEmployeeUseCase, AbstractEmployeeUseCase(skillMapper, positionMapper) {
    override fun execute(id: Long, request: PatchEmployeeRequest) {
        employeeMapper.findById(id) ?: throw NotFoundException("employee not exists. id: $id.")
        super.validatePosition(request.positionId)
        super.validateSkill(request.skills)

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
            )
        )

        employeeSkillMapper.deleteByEmployeeId(id)
        request.skills.takeIf {
            it.isNotEmpty()
        }?.let {
            employeeSkillMapper.bulkInsert(
                request.skills.asSequence().map {
                    EmployeeSkill(
                        employeeSkillId = 0, // auto_increment
                        employeeId = id,
                        skillId = it
                    )
                }.toList()
            )
        }

    }
}