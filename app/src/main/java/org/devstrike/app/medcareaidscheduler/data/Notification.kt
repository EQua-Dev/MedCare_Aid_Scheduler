package org.devstrike.app.medcareaidscheduler.data

data class Notification(
    val notificationID: String = "",
    val notificationType: String = "",
    val notificationSenderID: String = "",
    val notificationReceiverID: String = "",
    val notificationTitle: String = "",
    val notificationMessage: String = "",
    val notificationSentDate: String = "",
    val notificationProvinceID: String = ""
)
