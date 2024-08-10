package com.ianxc.temporalboot.temporal.workflows

import com.ianxc.temporalboot.temporal.activities.SendEmailActivities
import com.ianxc.temporalboot.temporal.model.Constants
import com.ianxc.temporalboot.temporal.model.EmailDetails
import com.ianxc.temporalboot.temporal.model.WorkflowData
import io.temporal.activity.ActivityOptions
import io.temporal.failure.CanceledFailure
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Workflow
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

@WorkflowImpl(workers = [Constants.WORKER_NAME])
class SendEmailWorkflowImpl : SendEmailWorkflow {
    private lateinit var emailDetails: EmailDetails

    private val activities = Workflow.newActivityStub(
        SendEmailActivities::class.java,
        ActivityOptions.newBuilder()
            .setStartToCloseTimeout(10.seconds.toJavaDuration())
            .build()
    )

    override fun run(data: WorkflowData) {
        emailDetails = EmailDetails(
            email = data.email,
            message = "Welcome to our subscription workflow",
            subscribed = true,
            count = 0
        )

        while (emailDetails.subscribed) {
            emailDetails.count++
            if (emailDetails.count > 1) {
                emailDetails.message = "Thank you for your continued subscription!"
            }

            try {
                activities.sendEmail(emailDetails)
                Workflow.sleep(12.seconds.toJavaDuration())
            } catch (e: CanceledFailure) {
                emailDetails.subscribed = false
                emailDetails.message = "Sorry to see you go"
                val sendGoodbye = Workflow.newDetachedCancellationScope {
                    activities.sendEmail(emailDetails)
                }
                sendGoodbye.run()
                throw e
            }
        }
    }

    override fun details(): EmailDetails {
        return emailDetails
    }
}
