package com.ianxc.temporalboot.temporal.manager

import com.ianxc.temporalboot.config.HelloScheduleSpec
import com.ianxc.temporalboot.temporal.model.Constants
import com.ianxc.temporalboot.temporal.model.HelloData
import com.ianxc.temporalboot.temporal.workflows.HelloWorkflow
import io.temporal.api.enums.v1.ScheduleOverlapPolicy
import io.temporal.client.WorkflowOptions
import io.temporal.client.schedules.Schedule
import io.temporal.client.schedules.ScheduleActionStartWorkflow
import io.temporal.client.schedules.ScheduleAlreadyRunningException
import io.temporal.client.schedules.ScheduleClient
import io.temporal.client.schedules.ScheduleOptions
import io.temporal.client.schedules.SchedulePolicy
import io.temporal.client.schedules.ScheduleSpec
import io.temporal.client.schedules.ScheduleUpdate
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ScheduleManager {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Autowired private lateinit var scheduleClient: ScheduleClient

    fun scheduleHello(helloScheduleSpec: HelloScheduleSpec) {
        val schedule =
            Schedule.newBuilder()
                .setAction(
                    ScheduleActionStartWorkflow.newBuilder()
                        .setWorkflowType(HelloWorkflow::class.java)
                        .setArguments(HelloData(helloScheduleSpec.name))
                        .setOptions(
                            WorkflowOptions.newBuilder()
                                .setWorkflowId("hello-workflow-${helloScheduleSpec.name}")
                                .setTaskQueue(Constants.HELLO_TASK_QUEUE_NAME)
                                .build())
                        .build())
                .setSpec(
                    ScheduleSpec.newBuilder()
                        .setCronExpressions(listOf(helloScheduleSpec.cron))
                        .setJitter(30.seconds.toJavaDuration())
                        .build())
                .setPolicy(
                    SchedulePolicy.newBuilder()
                        .setOverlap(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_BUFFER_ONE)
                        .build())
                .build()

        val scheduleId = "hello-schedule-${helloScheduleSpec.name}"
        try {
            scheduleClient.createSchedule(
                scheduleId,
                schedule,
                ScheduleOptions.newBuilder().setTriggerImmediately(true).build())
            logger.info("created new schedule $scheduleId")
        } catch (e: ScheduleAlreadyRunningException) {
            val scheduleHandle = scheduleClient.getHandle(scheduleId)
            // Don't bother with input as we want to replace the existing schedule.
            scheduleHandle.update { ScheduleUpdate(schedule) }
            logger.info("updated existing schedule $scheduleId")
        }
    }
}
