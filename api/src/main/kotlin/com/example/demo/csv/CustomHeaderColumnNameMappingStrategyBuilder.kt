package com.example.demo.csv

import com.opencsv.bean.CsvBindByName
import com.opencsv.bean.CsvBindByPosition
import com.opencsv.bean.HeaderColumnNameMappingStrategy
import org.apache.commons.lang3.reflect.FieldUtils

class CustomHeaderColumnNameMappingStrategyBuilder<T> {
    inline fun <reified T> build(): HeaderColumnNameMappingStrategy<T> {
        return HeaderColumnNameMappingStrategy<T>()
            .apply {
                type = T::class.java
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
            }
    }
}