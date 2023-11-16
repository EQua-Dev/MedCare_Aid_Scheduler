package org.devstrike.app.medcareaidscheduler.data

data class ReportLog(
    val reportLogID: String = "",
    val reportLogDateSubmitted: String = "",
    val reportLogOwnerID: String = "",
    val reportLogOwnerProvinceID: String = "",
    val reportLogStatus: String = "", //submitted or draft
    val reportLogDuration: String = "", //number of days accounted for in the log
    val reportLogStartDate: String = "", //the date of the user's first shift of the week
    val reportLogEndDate: String = "", //the date of the user's last shift of the week
    val reportLogNoOfTotalWeekShift: String = "", //total shifts assigned
    val reportLogNoOfTotalShiftsServed: String = "", //total shifts served
    val reportLogNoOfTotalShiftsPending: String = "", //total shifts missed
    val reportLogTotalPayableDaysHours: String = "", //check every shift for the week that is day
    val reportLogTotalPayableNightsHours: String = "", //check every shift for the week that is night
    val reportLogDailyShiftDetails: List<AssignedShift> = listOf(), //mapping of the day of the week to the day's assigned shift
    val reportLogTotalPayableSleepOversHours: String = "", //check every shift for the week that is sleepover
    val reportLogTotalAmountPayable: String = "", //total amount that can be paid
    val reportLogTotalAmountToPay: String = "", //total amount that staff wants to be paid
)
