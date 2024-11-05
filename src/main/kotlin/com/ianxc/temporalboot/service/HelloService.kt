package com.ianxc.temporalboot.service

import net.logstash.logback.argument.StructuredArguments.kv
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

interface HelloService {
    suspend fun makeHelloMessage(name: String): String
}

@Service
class HelloServiceImpl : HelloService {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun makeHelloMessage(name: String): String {
        logger.info("hello!", kv("name", name))
        return "Hello, $name"
    }
}
