package com.example.demo.usecase

import com.example.demo.dto.EmployeeListCSV
import com.example.demo.repository.EmployeeMapper
import com.example.demo.type.GenderType
import com.opencsv.CSVWriter
import com.opencsv.bean.*
import org.apache.commons.lang3.reflect.FieldUtils
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
                                HeaderColumnNameMappingStrategy<EmployeeListCSV>()
                                    .apply {
                                        type = EmployeeListCSV::class.java
                                        val headers = FieldUtils.getAllFields(type)
                                            // FieldをCsvBindByPosition.position順で並び替え
                                            .sortedWith(
                                                compareBy {
                                                    it.getAnnotation(CsvBindByPosition::class.java)?.position
                                                }
                                            )
                                            .mapNotNull {
                                                it.getAnnotation(CsvBindByName::class.java)?.column
                                            }
                                        setColumnOrderOnWrite(compareBy { headers.indexOf(it) })
                                    })
                            .build();
                    beanToCsv.write(employees);
                }
            }
            return baos.toByteArray()
        }
    }
}