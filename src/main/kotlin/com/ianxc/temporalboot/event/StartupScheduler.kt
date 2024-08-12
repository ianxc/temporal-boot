package com.ianxc.temporalboot.event

import com.ianxc.temporalboot.config.ScheduleConfig
import com.ianxc.temporalboot.temporal.manager.ScheduleManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class StartupScheduler {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired
    private lateinit var scheduleConfig: ScheduleConfig

    @Autowired
    private lateinit var scheduleManager: ScheduleManager

    @EventListener
    fun onApplicationEvent(event: ApplicationReadyEvent) {
        logger.error("took ${event.timeTaken} to start. scheduleConfig=${scheduleConfig.hello}")
        scheduleConfig.hello.forEach { spec ->
            scheduleManager.scheduleHello(spec)
        }
    }
}
