package com.ianxc.temporalboot.temporal.integration

import com.ianxc.temporalboot.config.TemporalConfig
import com.ianxc.temporalboot.temporal.activities.SendEmailActivitiesImpl
import com.ianxc.temporalboot.temporal.model.WorkflowData
import com.ianxc.temporalboot.temporal.workflows.SendEmailWorkflow
import com.ianxc.temporalboot.temporal.workflows.SendEmailWorkflowImpl
import io.temporal.api.enums.v1.WorkflowExecutionStatus
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest
import io.temporal.client.WorkflowClient
import io.temporal.client.WorkflowClientOptions
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.testing.TestWorkflowExtension
import io.temporal.worker.Worker
import java.util.concurrent.TimeUnit
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
import org.junit.jupiter.api.extension.RegisterExtension
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

class SendEmailTest {
    @Test
    fun `test create subscription`(
        testEnv: TestWorkflowEnvironment,
        worker: Worker,
        workflow: SendEmailWorkflow
    ) {
        // Arrange
        worker.registerActivitiesImplementations(SendEmailActivitiesImpl())
        testEnv.start()
        val data = WorkflowData("test@example.com")
        val execution = WorkflowClient.start(workflow::run, data)
        val client = testEnv.workflowClient

        // Act
        val response =
            client.workflowServiceStubs
                .blockingStub()
                .describeWorkflowExecution(
                    DescribeWorkflowExecutionRequest.newBuilder()
                        .setNamespace(testEnv.namespace)
                        .setExecution(execution)
                        .build())

        val status = response.workflowExecutionInfo.status

        // Assert
        expectThat(status).isEqualTo(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING)
    }

    /** This test also tests whether the QueryMethod result can be deserialized */
    @Test
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    fun `test get subscription details`(
        testEnv: TestWorkflowEnvironment,
        worker: Worker,
        workflow: SendEmailWorkflow
    ) {
        // Arrange
        worker.registerActivitiesImplementations(SendEmailActivitiesImpl())
        testEnv.start()
        val data = WorkflowData("test@example.com")
        WorkflowClient.start(workflow::run, data)

        // Act
        // N.B. this will block.
        val details = workflow.details()

        // Assert
        expectThat(details) {
            get { email }.isEqualTo("test@example.com")
            get { subscribed }.isTrue()
        }
    }

    companion object {
        @RegisterExtension
        @Suppress("unused")
        val testWorkflowExtension: TestWorkflowExtension =
            TestWorkflowExtension.newBuilder()
                .registerWorkflowImplementationTypes(SendEmailWorkflowImpl::class.java)
                .setDoNotStart(true)
                .setWorkflowClientOptions(
                    WorkflowClientOptions.newBuilder()
                        .setDataConverter(TemporalConfig().dataConverter())
                        .build())
                .build()
    }
}
