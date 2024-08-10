package com.ianxc.temporalboot.temporal.model

data class WorkflowData(
    val email: String
)

data class EmailDetails(
    val email: String,
    var message: String,
    var count: Int,
    var subscribed: Boolean
)

data class Message(
    val content: String,
)
