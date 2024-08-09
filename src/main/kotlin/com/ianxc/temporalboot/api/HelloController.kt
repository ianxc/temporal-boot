package com.ianxc.temporalboot.api

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    private val logger = LoggerFactory.getLogger(HelloController::class.java)

    @GetMapping("/hello/{name}")
    fun hello(@PathVariable name: String): String {
        logger.atInfo().addKeyValue("name", name).log("hello")
        return "Hello, $name"
    }
}
