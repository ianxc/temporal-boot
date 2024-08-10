package com.ianxc.temporalboot.config

import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.temporal.common.converter.DataConverter
import io.temporal.common.converter.DefaultDataConverter
import io.temporal.common.converter.JacksonJsonPayloadConverter
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableAutoConfiguration
class TemporalConfig {
    @Bean
    fun dataConverter(): DataConverter {
        val jacksonObjectMapper = JacksonJsonPayloadConverter.newDefaultObjectMapper()
            .registerKotlinModule()
        val jacksonConverter = JacksonJsonPayloadConverter(jacksonObjectMapper)
        val dataConverter = DefaultDataConverter.newDefaultInstance()
            .withPayloadConverterOverrides(jacksonConverter)
        return dataConverter
    }
}
