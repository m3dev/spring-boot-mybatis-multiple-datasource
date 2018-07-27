package com.example.springboot.multidatasource

import com.example.springboot.multidatasource.datasourceA.DataSourceAConfig
import com.example.springboot.multidatasource.datasourceB.DataSourceBConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import

@SpringBootApplication
@Import(DataSourceAConfig::class, DataSourceBConfig::class)
class MultidatasourceSampleApplication

fun main(args: Array<String>) {
    runApplication<MultidatasourceSampleApplication>(*args)
}
