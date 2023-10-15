package org.devstrike.app.medcareaidscheduler.data

data class AssignedShift(
    val assignedShiftID: String = "",
    val assignedHouseID: String = "",
    val assignedStaffID: String = "",
    val assignedSupervisorID: String = "",
    val assignedShiftTypeID: String = "",
    val assignedShiftStatus: String = "",
    val assignedShiftClockInTime: String = "",
    val assignedShiftClockOutTime: String = "",
    val assignedShiftDate: String = "",
    val assignedShiftDateAdded: String = "",
    val assignedShiftNecessaryInformation: String = "",
)
