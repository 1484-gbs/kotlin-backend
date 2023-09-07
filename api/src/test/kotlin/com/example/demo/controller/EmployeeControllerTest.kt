package com.example.demo.controller

import com.example.demo.dto.UserDetailImpl
import com.example.demo.exception.InvalidRequestException
import com.example.demo.request.CreateEmployeeRequest
import com.example.demo.response.CreateEmployeeResponse
import com.example.demo.type.GenderType
import com.example.demo.type.RoleType
import com.example.demo.usecase.CreateEmployeeUseCase
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock

class EmployeeControllerTest {

    @Test
    fun create_success() {
        val expected = 1L
        val createEmployeeUseCase = mock<CreateEmployeeUseCase> {
            on { execute(any(), any()) } doReturn CreateEmployeeResponse(
                employeeId = expected
            )
        }
        val controller =
            EmployeeController(createEmployeeUseCase, mock(), mock(), mock(), mock(), mock(), mock(), mock(), mock())
        val actual = controller.create(
            request = getEmployeeCreateRequest(),
            user = UserDetailImpl.UserDetail(
                loginId = "admin",
                token = "token",
                role = RoleType.ADMIN
            )
        )
        assertEquals(expected, actual.employeeId)
    }

    @Test
    fun create_validation_error() {
        val createEmployeeUseCase = mock<CreateEmployeeUseCase> {
            on { execute(any(), any()) } doThrow (
                InvalidRequestException("test")
            )
        }
        val controller =
            EmployeeController(createEmployeeUseCase, mock(), mock(), mock(), mock(), mock(), mock(), mock(), mock())
        assertThrows<InvalidRequestException> {
            controller.create(
                request = getEmployeeCreateRequest(),
                user = UserDetailImpl.UserDetail(
                    loginId = "admin",
                    token = "token",
                    role = RoleType.ADMIN
                )
            )
        }
    }

    private fun getEmployeeCreateRequest(): CreateEmployeeRequest {
        return CreateEmployeeRequest(
            firstName = "firstName",
            lastName = "lastName",
            firstNameKana = "ファーストネームカナ",
            lastNameKana = "ラストネームカナ",
            birthDay = CreateEmployeeRequest.Birthday(
                year = 2000,
                month = 12,
                day = 1
            ),
            gender = GenderType.FEMALE,
            tel = "09000000000",
            positionId = 1,
            skills = listOf(1),
            photo = null,
            salaryOfMonth = 350000,
            loginId = "test",
            password = "hoge"
        )

    }
}