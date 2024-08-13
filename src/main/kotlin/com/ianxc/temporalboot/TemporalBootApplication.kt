package com.ianxc.temporalboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication @ConfigurationPropertiesScan class TemporalBootApplication

fun main(args: Array<String>) {
    runApplication<TemporalBootApplication>(*args)
}
