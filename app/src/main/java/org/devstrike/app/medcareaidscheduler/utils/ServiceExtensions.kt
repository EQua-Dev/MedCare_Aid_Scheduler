/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.Province
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.data.UserData

 fun getProvince(provinceId: String, context: Context): Province? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.provinceCollectionRef.document(provinceId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(Province::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}

 fun getUser(userId: String, context: Context): UserData? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.userCollectionRef.document(userId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(UserData::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}
 fun getHouse(houseId: String, context: Context): House? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.housesCollectionRef.document(houseId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(House::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}
 fun getShiftType(shiftTypeId: String, context: Context): ShiftType? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.shiftCollectionRef.document(shiftTypeId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(ShiftType::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}

fun getShift(shiftId: String, context: Context): AssignedShift? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.assignedShiftsCollectionRef.document(shiftId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(AssignedShift::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}


fun openDial(phoneNumber: String, context: Context){
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = Uri.parse("tel:$phoneNumber")
    context.startActivity(intent)
}
