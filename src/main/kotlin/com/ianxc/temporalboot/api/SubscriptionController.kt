package com.ianxc.temporalboot.api

import com.ianxc.temporalboot.temporal.model.Constants
import com.ianxc.temporalboot.temporal.model.EmailDetails
import com.ianxc.temporalboot.temporal.model.Message
import com.ianxc.temporalboot.temporal.model.WorkflowData
import com.ianxc.temporalboot.temporal.workflows.SendEmailWorkflow
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowOptions
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/subscription")
class SubscriptionController {

    @Autowired
    private lateinit var client: WorkflowClient

    @PostMapping("/create", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun subscribe(@RequestBody data: WorkflowData): Message {
        val options = WorkflowOptions.newBuilder()
            .setWorkflowId(data.email)
            .setTaskQueue(Constants.TASK_QUEUE_NAME)
            .build()

        val workflow = client.newWorkflowStub(SendEmailWorkflow::class.java, options)
        WorkflowClient.start(workflow::run, data)

        return Message("Subscribe request received")
    }

    @GetMapping("/details", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getDetails(@RequestParam email: String): EmailDetails {
        val workflow = client.newWorkflowStub(SendEmailWorkflow::class.java, email)
        return workflow.details()
    }

    @PostMapping("/delete", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun unsubscribe(@RequestBody data: WorkflowData): Message {
        val workflow = client.newUntypedWorkflowStub(data.email)
        workflow.cancel()
        return Message("Unsubscribe request received")
    }
}
