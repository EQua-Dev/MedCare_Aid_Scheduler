/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.utils

import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar

//toast function

fun Context.toast(message: String) =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

//function to change milliseconds to date format
fun getDate(milliSeconds: Long?, dateFormat: String?): String {
    // Create a DateFormatter object for displaying date in specified format.
    val formatter = SimpleDateFormat(dateFormat)

    // Create a calendar object that will convert the date and time value in milliseconds to date.
    val calendar: Calendar? = Calendar.getInstance()
    calendar?.timeInMillis = milliSeconds!!
    return formatter.format(calendar?.time!!)
}
fun calculateHoursBetweenTimes(lesserTime: String, greaterTime: String): Double? {
    try {
        // Parse the time strings into hours and minutes
        val regex = """(\d+):(\d+)""".toRegex()
        val match1 = regex.find(lesserTime)
        val match2 = regex.find(greaterTime)

        if (match1 != null && match2 != null) {
            val (hours1, minutes1) = match1.destructured
            val (hours2, minutes2) = match2.destructured

            // Convert the times to minutes since midnight
            val totalMinutes1 = hours1.toInt() * 60 + minutes1.toInt()
            val totalMinutes2 = hours2.toInt() * 60 + minutes2.toInt()

            // Calculate the time difference in minutes
            val timeDifference = totalMinutes2 - totalMinutes1

            // Convert the time difference back to hours
            val hours = timeDifference / 60.0

            return hours
        }
    } catch (e: Exception) {
        // Handle parsing or calculation errors
    }

    return null
}
