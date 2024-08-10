package com.ianxc.temporalboot.temporal.activities

import com.ianxc.temporalboot.temporal.model.Constants
import com.ianxc.temporalboot.temporal.model.EmailDetails
import io.temporal.spring.boot.ActivityImpl
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
@ActivityImpl(workers = [Constants.WORKER_NAME])
class SendEmailActivitiesImpl : SendEmailActivities {
    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun sendEmail(emailDetails: EmailDetails) {
        logger.atInfo()
            .addKeyValue("email", emailDetails.email)
            .addKeyValue("message", emailDetails.message)
            .log("sending email")
    }
}
