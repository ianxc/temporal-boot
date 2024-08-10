package com.ianxc.temporalboot.temporal.integration

import com.ianxc.temporalboot.temporal.activities.SendEmailActivitiesImpl
import com.ianxc.temporalboot.temporal.model.WorkflowData
import com.ianxc.temporalboot.temporal.workflows.SendEmailWorkflow
import com.ianxc.temporalboot.temporal.workflows.SendEmailWorkflowImpl
import io.temporal.api.enums.v1.WorkflowExecutionStatus
import io.temporal.api.workflowservice.v1.DescribeWorkflowExecutionRequest
import io.temporal.client.WorkflowClient
import io.temporal.testing.TestWorkflowEnvironment
import io.temporal.testing.TestWorkflowExtension
import io.temporal.worker.Worker
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.springframework.boot.test.context.SpringBootTest
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBootTest
class SendEmailTest {
    @RegisterExtension
    val testWorkflowExtension = TestWorkflowExtension.newBuilder()
        .registerWorkflowImplementationTypes(SendEmailWorkflowImpl::class.java)
        .setDoNotStart(true)
        .build()


    @Test
    fun `test create email`(testEnv: TestWorkflowEnvironment, worker: Worker, workflow: SendEmailWorkflow) {

        // Arrange
        val client = testEnv.workflowClient
        worker.registerActivitiesImplementations(SendEmailActivitiesImpl())
        testEnv.start()

        // Act
        val data = WorkflowData("test@example.com")
        val execution = WorkflowClient.start(workflow::run, data)

        val response = client.workflowServiceStubs
            .blockingStub()
            .describeWorkflowExecution(
                DescribeWorkflowExecutionRequest.newBuilder()
                    .setNamespace(testEnv.namespace)
                    .setExecution(execution)
                    .build()
            )

        val status = response.workflowExecutionInfo.status

        // Assert
        expectThat(status).isEqualTo(WorkflowExecutionStatus.WORKFLOW_EXECUTION_STATUS_RUNNING)
    }
}
