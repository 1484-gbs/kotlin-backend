package com.example.demo.usecase

import com.example.demo.entity.Employee
import com.example.demo.exception.InvalidRequestException
import com.example.demo.repository.EmployeeMapper
import com.example.demo.type.GenderType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.time.LocalDate
import java.time.LocalDateTime

class GetEmployeeRandomUseCaseTest {

    @Test
    fun execute_count_equal_entity_size_success() {
        val expected = 2

        val employeeMapper = mock<EmployeeMapper> {
            on { findAll() } doReturn getEmployee()
        }
        val usecase =
            GetEmployeeRandomUseCaseCaseImpl(employeeMapper)
        val actual = usecase.execute(count = expected)

        assertEquals(expected, actual.employees.size)
    }

    @Test
    fun execute_count_less_than_entity_size_success() {
        val expected = 1

        val employeeMapper = mock<EmployeeMapper> {
            on { findAll() } doReturn getEmployee()
        }
        val usecase =
            GetEmployeeRandomUseCaseCaseImpl(employeeMapper)
        val actual = usecase.execute(count = expected)

        assertEquals(expected, actual.employees.size)
    }

    @Test
    fun execute_count_greater_than_entity_size_throw_exception() {
        val employeeMapper = mock<EmployeeMapper> {
            on { findAll() } doReturn getEmployee()
        }
        val usecase =
            GetEmployeeRandomUseCaseCaseImpl(employeeMapper)
        assertThrows<InvalidRequestException> {
            usecase.execute(count = getEmployee().size + 1)
        }
    }

    @Test
    fun execute_count_invalid_throw_exception() {
        val usecase =
            GetEmployeeRandomUseCaseCaseImpl(mock())
        assertThrows<InvalidRequestException> {
            usecase.execute(count = 0)
        }
    }
    private fun getEmployee() : List<Employee> {
        return listOf (
            Employee(
                1,
                "firstname",
                "lastname",
                "firstnamekana",
                "lastnamekana",
                LocalDate.now(),
                GenderType.FEMALE,
                "09000000000",
                1,
                350000,
                "hoge",
                "hoge",
                "hoge",
                "hoge",
                LocalDateTime.now(),
                null,
                null
            ),
            Employee(
                2,
                "firstname",
                "lastname",
                "firstnamekana",
                "lastnamekana",
                LocalDate.now(),
                GenderType.FEMALE,
                "09000000000",
                1,
                350000,
                "hoge",
                "hoge",
                "hoge",
                "hoge",
                LocalDateTime.now(),
                null,
                null
            )
        )
    }

}