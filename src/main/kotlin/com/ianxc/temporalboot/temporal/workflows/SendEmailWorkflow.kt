package com.ianxc.temporalboot.temporal.workflows

import com.ianxc.temporalboot.temporal.model.EmailDetails
import com.ianxc.temporalboot.temporal.model.WorkflowData
import io.temporal.workflow.QueryMethod
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface SendEmailWorkflow {
    @WorkflowMethod
    fun run(data: WorkflowData)

    @QueryMethod
    fun details(): EmailDetails
}


