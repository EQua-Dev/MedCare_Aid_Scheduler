/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.data

data class ConcludedShift(
    val concludedShiftID: String = "",
    val assignedShiftID: String = "",
    val shiftStaffID: String = "",
    val shiftProvinceID: String = "",
    val noOfDayHours: String = "",
    val noOfNightHours: String = "",
    val noOfSleepOverHours: String = "",
    val noOfTotalHours: String = "",
    val dateSubmitted: String = ""
)
