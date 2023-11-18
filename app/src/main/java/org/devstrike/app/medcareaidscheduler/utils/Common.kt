/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Common {

    val auth = FirebaseAuth.getInstance()

    const val PASSWORD_INPUT_TYPE = "Password"
    const val IS_FIRST_TIME_KEY = "is_first_time"

    const val STAFF_ROLE = "staff"
    const val SUPERVISOR_ROLE = "supervisor"

    const val PERSONAL_NOTIFICATION_TAG = "personal"
    const val GENERAL_NOTIFICATION_TAG = "general"
    const val NOTIFICATION_TYPE_SHIFT_ASSIGNMENT = "Shift Assignment"

    const val SHIFT_ACTIVE = "active"
    const val SHIFT_INACTIVE = "inactive"
    const val LOG_REPORT_SUBMITTED_STATUS = "submitted"
    const val LOG_REPORT_APPROVED_STATUS = "approved"
    const val LOG_REPORT_REJECTED_STATUS = "rejected"

    const val TIME_FORMAT_EDMYHM = "EEE, dd MMM, yyyy hh:mm"
    const val TIME_FORMAT_EDMY = "EEE, dd MMM, yyyy"
    const val TIME_FORMAT_DMYHM = "dd-MMM-yyyy hh:mm"
    const val TIME_FORMAT_HM = "hh:mm"

    const val SHIFT_HOURLY_PAY = 2.00

    private const val USER_REF = "UserData"
    private const val PROVINCE_REF = "Province Data"
    private const val STAFF_REF = "Staff Data"
    private const val SHIFT_REF = "Shift Data"
    private const val HOUSES_REF = "Houses Data"
    private const val PAYMENTS_REF = "Payments Data"
    private const val NOTIFICATION_REF = "Notifications Data"
    private const val ASSIGNED_SHIFT_REF = "Assigned Shifts Data"
    private const val SHIFT_REPORT_LOG_REF = "Organisation Of Working Time Sheet"


    val userCollectionRef = Firebase.firestore.collection(USER_REF)
    val provinceCollectionRef = Firebase.firestore.collection(PROVINCE_REF)
    val staffCollectionRef = Firebase.firestore.collection(STAFF_REF)
    val shiftCollectionRef = Firebase.firestore.collection(SHIFT_REF)
    val housesCollectionRef = Firebase.firestore.collection(HOUSES_REF)
    val paymentsCollectionRef = Firebase.firestore.collection(PAYMENTS_REF)
    val notificationsCollectionRef = Firebase.firestore.collection(NOTIFICATION_REF)
    val assignedShiftsCollectionRef = Firebase.firestore.collection(ASSIGNED_SHIFT_REF)
    val weeklyShiftsReportLogCollectionRef = Firebase.firestore.collection(SHIFT_REPORT_LOG_REF)
}