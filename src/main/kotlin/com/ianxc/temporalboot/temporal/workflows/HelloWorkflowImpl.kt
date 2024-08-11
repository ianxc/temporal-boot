package com.ianxc.temporalboot.temporal.workflows

import com.ianxc.temporalboot.temporal.model.Constants
import com.ianxc.temporalboot.temporal.model.HelloData
import io.temporal.spring.boot.WorkflowImpl
import io.temporal.workflow.Workflow

@WorkflowImpl(workers = [Constants.HELLO_WORKER_NAME])
class HelloWorkflowImpl: HelloWorkflow {
    private val logger = Workflow.getLogger(this::class.java)

    override fun sayHello(data: HelloData) {
        logger.info("Hello, {}!", data.name)
    }
}
