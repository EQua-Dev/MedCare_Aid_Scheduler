/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.devstrike.app.medcareaidscheduler.data.Province
import org.devstrike.app.medcareaidscheduler.data.UserData

@Composable
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

@Composable
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