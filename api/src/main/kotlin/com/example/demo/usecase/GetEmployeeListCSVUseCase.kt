package com.example.demo.usecase

import com.example.demo.csv.CustomHeaderColumnNameMappingStrategyBuilder
import com.example.demo.dto.EmployeeListCSV
import com.example.demo.repository.EmployeeMapper
import com.example.demo.type.GenderType
import com.opencsv.CSVWriter
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.io.OutputStreamWriter

interface GetEmployeeListCSVUseCase {
    fun execute(name: String?, kana: String?, gender: GenderType?, positionId: Long?): ByteArray
}

@Service
@Transactional(readOnly = true)
class GetEmployeeListCSVUseCaseCaseImpl(
    private val employeeMapper: EmployeeMapper
) : GetEmployeeListCSVUseCase {
    override fun execute(
        name: String?,
        kana: String?,
        gender: GenderType?,
        positionId: Long?
    ): ByteArray {
        val employees = employeeMapper.findList(name, kana, gender, positionId).map {
            EmployeeListCSV.of(it)
        }.toList()

        ByteArrayOutputStream().use { baos ->
            OutputStreamWriter(baos).use { osw ->
                CSVWriter(osw).use { cw ->
                    val beanToCsv: StatefulBeanToCsv<EmployeeListCSV> =
                        StatefulBeanToCsvBuilder<EmployeeListCSV>(cw)
                            .withMappingStrategy(
                                CustomHeaderColumnNameMappingStrategyBuilder<EmployeeListCSV>().build()
                            )
                            .build()
                    beanToCsv.write(employees)
                }
            }
            return baos.toByteArray()
        }
    }
}