/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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

fun isDateWithinThisWeek(millisTime: Long): Boolean {
    // Convert milliseconds to a Date object.
    val date = Date(millisTime)

    val calendar = Calendar.getInstance()

    // Get the week number of the current date.
    val currentWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    // Get the week number of the date you want to check.
    val checkedDateWeek = calendar.get(Calendar.WEEK_OF_YEAR)

    return currentWeek == checkedDateWeek
}

fun isTimeInCurrentWeek(timeInMillis: Long): Boolean {
    // Get the current time
    val currentTime = Calendar.getInstance()

    // Create a Calendar instance for the given time
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis

    //Log.d(TAG, "isTimeInCurrentWeek: ")

    // Get the week of the year for the current time
    val currentWeek = currentTime.get(Calendar.WEEK_OF_YEAR)

    val givenDateWeek = calendar.get(Calendar.WEEK_OF_YEAR)
    Log.d("WeekTime", "isTimeInCurrentWeek: $currentWeek \ngivenWeek: $givenDateWeek")

    return currentWeek == givenDateWeek

}


fun isTimeInCurrentMonth(timeInMillis: Long): Boolean {
    // Get the current time
    val currentTime = Calendar.getInstance()

    // Create a Calendar instance for the given time
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis

    //Log.d(TAG, "isTimeInCurrentWeek: ")

    // Get the week of the year for the current time
    val currentMonth = currentTime.get(Calendar.MONTH)

    val givenDateMonth = calendar.get(Calendar.MONTH)
    Log.d("MonthTime", "isTimeInCurrentMonth: $currentMonth \ngivenMonth: $givenDateMonth")

    return currentMonth == givenDateMonth

}
fun convertDateTimeToMillis(dateTime: String, dateFormat: String): Long {
    val dateFormatType = SimpleDateFormat(dateFormat, Locale.ENGLISH)
    val date: Date? = try {
        dateFormatType.parse(dateTime)
    } catch (e: ParseException) {
        e.printStackTrace()
        null
    }

    return date?.time ?: 0L
}

fun getCurrentDate(dateFormat: String): String{
    val simpleDateFormat = SimpleDateFormat(dateFormat, Locale.ENGLISH)

    // Get the current date.
    val date = Date()

    // Format the current date as "October, 2023".
    return simpleDateFormat.format(date)

}

fun categorizeDatesByWeekInMonth(dates: List<Date>): Map<Int, List<Date>> {
    val calendar = Calendar.getInstance()

    val groupedDates = mutableMapOf<Int, MutableList<Date>>()

    for (date in dates) {
        calendar.time = date
        val weekNumber = calendar.get(Calendar.WEEK_OF_MONTH)

        if (groupedDates.containsKey(weekNumber)) {
            groupedDates[weekNumber]?.add(date)
        } else {
            groupedDates[weekNumber] = mutableListOf(date)
        }
    }

    return groupedDates.toMap()
}

