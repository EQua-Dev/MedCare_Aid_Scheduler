package org.devstrike.app.medcareaidscheduler.data

data class ShiftType(
    val shiftTypeID: String = "",
    val shiftTypeName: String = "",
    val shiftTypeOwnerDistrict: String = "",
    val shiftTypeSupervisor: String = "",
    val shiftTypeStartTime: String = "",
    val shiftTypeEndTime: String = "",
    val shiftTypeNoOfHours: String = "",
)
