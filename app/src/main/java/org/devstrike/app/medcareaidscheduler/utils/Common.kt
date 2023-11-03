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

    const val NOTIFICATION_TYPE_SHIFT_ASSIGNMENT = "Shift Assignment"

    private const val USER_REF = "UserData"
    private const val PROVINCE_REF = "Province Data"
    private const val STAFF_REF = "Staff Data"
    private const val SHIFT_REF = "Shift Data"
    private const val HOUSES_REF = "Houses Data"
    private const val PAYMENTS_REF = "Payments Data"
    private const val NOTIFICATION_REF = "Notifications Data"
    private const val ASSIGNED_SHIFT_REF = "Assigned Shifts Data"


    val userCollectionRef = Firebase.firestore.collection(USER_REF)
    val provinceCollectionRef = Firebase.firestore.collection(PROVINCE_REF)
    val staffCollectionRef = Firebase.firestore.collection(STAFF_REF)
    val shiftCollectionRef = Firebase.firestore.collection(SHIFT_REF)
    val housesCollectionRef = Firebase.firestore.collection(HOUSES_REF)
    val paymentsCollectionRef = Firebase.firestore.collection(PAYMENTS_REF)
    val notificationsCollectionRef = Firebase.firestore.collection(NOTIFICATION_REF)
    val assignedShiftsCollectionRef = Firebase.firestore.collection(ASSIGNED_SHIFT_REF)
}