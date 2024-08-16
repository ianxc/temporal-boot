package com.ianxc.temporalboot.api

import com.ianxc.temporalboot.temporal.model.Constants
import com.ianxc.temporalboot.temporal.model.HelloData
import com.ianxc.temporalboot.temporal.workflows.HelloWorkflow
import io.temporal.api.enums.v1.ScheduleOverlapPolicy
import io.temporal.client.WorkflowOptions
import io.temporal.client.schedules.Schedule
import io.temporal.client.schedules.ScheduleActionStartWorkflow
import io.temporal.client.schedules.ScheduleAlreadyRunningException
import io.temporal.client.schedules.ScheduleClient
import io.temporal.client.schedules.ScheduleException
import io.temporal.client.schedules.ScheduleIntervalSpec
import io.temporal.client.schedules.ScheduleOptions
import io.temporal.client.schedules.SchedulePolicy
import io.temporal.client.schedules.ScheduleSpec
import io.temporal.client.schedules.ScheduleUpdate
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@RestController
@RequestMapping("/hello")
class HelloController(private val scheduleClient: ScheduleClient) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/{name}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun hello(@PathVariable name: String): String {
        logger.atInfo().addKeyValue("name", name).log("hello")
        return "Hello, $name"
    }

    @GetMapping("/schedule/{name}", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun getHello(@PathVariable name: String): String {
        logger.info("getting schedule $name")
        val scheduleHandle = scheduleClient.getHandle("hello-schedule-$name")
        return try {
            scheduleHandle.describe().id
        } catch (e: ScheduleException) {
            "no"
        }
    }

    @PostMapping("/schedule/create", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun scheduleHello(@RequestParam name: String, @RequestParam seconds: Int): String {
        val helloData = HelloData(name)
        val schedule =
            Schedule.newBuilder()
                .setAction(
                    ScheduleActionStartWorkflow.newBuilder()
                        .setWorkflowType(HelloWorkflow::class.java)
                        .setArguments(helloData)
                        .setOptions(
                            WorkflowOptions.newBuilder()
                                .setWorkflowId("hello-workflow-$name")
                                .setTaskQueue(Constants.HELLO_TASK_QUEUE_NAME)
                                .build())
                        .build())
                .setSpec(
                    ScheduleSpec.newBuilder()
                        .setIntervals(
                            listOf(ScheduleIntervalSpec(seconds.seconds.toJavaDuration())))
                        .setJitter(3.seconds.toJavaDuration())
                        .build())
                .setPolicy(
                    SchedulePolicy.newBuilder()
                        .setOverlap(ScheduleOverlapPolicy.SCHEDULE_OVERLAP_POLICY_BUFFER_ONE)
                        .build())
                .build()

        return try {
            scheduleClient.createSchedule(
                "hello-schedule-$name",
                schedule,
                ScheduleOptions.newBuilder().setTriggerImmediately(true).build())
            "Scheduled hello for $name every $seconds seconds"
        } catch (e: ScheduleAlreadyRunningException) {
            val scheduleHandle = scheduleClient.getHandle("hello-schedule-$name")
            // Don't bother with input as we want to replace the existing schedule.
            scheduleHandle.update { scheduleUpdateInput ->
                check(scheduleUpdateInput.description.id == "hello-schedule-$name") {
                    "schedule ids should match"
                }
                ScheduleUpdate(schedule)
            }
            "Schedule for $name already exists - updated"
        }
    }

    @PostMapping("/schedule/delete", produces = [MediaType.TEXT_PLAIN_VALUE])
    fun deleteScheduledHello(@RequestParam name: String): String {
        val scheduleHandle = scheduleClient.getHandle("hello-schedule-$name")
        scheduleHandle.delete()
        return "Deleted schedule for $name"
    }
}
