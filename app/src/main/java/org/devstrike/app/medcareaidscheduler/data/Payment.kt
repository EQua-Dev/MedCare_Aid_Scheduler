package org.devstrike.app.medcareaidscheduler.data

data class Payment(
    val paymentID: String = "",
    val paymentOwnerID: String = "",
    val paymentDate: String = "",
    val paymentAmount: String = "",
    val paymentNoOfHoursPaid: String = "",
    val paymentStatus: String = "",
    val paymentApprovingSupervisorID: String = "",
    val paymentDateApproved: String = ""

)
