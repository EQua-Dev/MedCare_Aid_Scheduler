package org.devstrike.app.medcareaidscheduler.data

data class ShiftType(
    val shiftTypeID: String = "",
    val shiftTypeName: String = "",
    val shiftTypeOwnerProvinceID: String = "",
    val shiftTypeSupervisorID: String = "",
    val shiftTypeStartTime: String = "",
    val shiftTypeEndTime: String = "",
    val shiftTypeNoOfHours: String = "",
)
