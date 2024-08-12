package com.ianxc.temporalboot.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan

@ConfigurationProperties(prefix = "app.schedules")
@ConfigurationPropertiesScan
class ScheduleConfig {
    lateinit var hello: List<HelloScheduleSpec>
}

data class HelloScheduleSpec(
    private val name: String,
    private val cron: String
)
