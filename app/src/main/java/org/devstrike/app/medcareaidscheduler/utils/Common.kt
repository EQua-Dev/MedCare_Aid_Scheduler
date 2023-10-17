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

    private const val USER_REF = "UserData"


    val userCollectionRef = Firebase.firestore.collection(USER_REF)
}