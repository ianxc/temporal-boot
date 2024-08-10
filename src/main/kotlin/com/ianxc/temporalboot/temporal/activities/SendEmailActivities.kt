package com.ianxc.temporalboot.temporal.activities

import com.ianxc.temporalboot.temporal.model.EmailDetails
import io.temporal.activity.ActivityInterface

@ActivityInterface
interface SendEmailActivities {
    fun sendEmail(emailDetails: EmailDetails)
}
