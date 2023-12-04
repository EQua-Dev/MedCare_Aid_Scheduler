package org.devstrike.app.medcareaidscheduler.data

data class AssignedShift(
    val assignedShiftID: String = "",
    val assignedHouseID: String = "",
    val assignedStaffID: String = "",
    val assignedSupervisorID: String = "",
    val assignedShiftStartTime: String = "",
    val assignedShiftStopTime: String = "",
    val assignedShiftTypes: String = "", //will be a text containing all the shift types that the assigned times fall into
    val assignedShiftStatus: String = "",
    val assignedShiftClockInTime: String = "",
    val assignedShiftClockOutTime: String = "",
    val assignedShiftDate: String = "",
    val assignedShiftDateAdded: String = "",
    val assignedShiftNecessaryInformation: String = "",
)
