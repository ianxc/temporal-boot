package com.ianxc.temporalboot.temporal.workflows

import com.ianxc.temporalboot.temporal.model.HelloData
import io.temporal.workflow.WorkflowInterface
import io.temporal.workflow.WorkflowMethod

@WorkflowInterface
interface HelloWorkflow {
    @WorkflowMethod fun sayHello(data: HelloData)
}
