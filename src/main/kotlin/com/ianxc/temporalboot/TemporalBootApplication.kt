package com.ianxc.temporalboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TemporalBootApplication

fun main(args: Array<String>) {
    runApplication<TemporalBootApplication>(*args)
}
