/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.libs.android.timetravel

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeTraveller {


    //function to change milliseconds to date format
    fun getDate(milliSeconds: Long?, dateFormat: String?): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

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
        val dateFormatType = SimpleDateFormat(dateFormat, Locale.getDefault())
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

    fun Context.toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()



    fun pickATime(context: Context, initialHour: Int, initialMinute: Int, onTimeSelected: (Int, Int) -> Unit) {
        val timePickerDialog = TimePickerDialog(
            context,
            { _: TimePicker, hourOfDay: Int, minute: Int ->
                onTimeSelected(hourOfDay, minute)
            },
            initialHour,
            initialMinute,
            true
        )

        timePickerDialog.show()
    }

    fun formatTime(hour: Int, minute: Int): String {
        return "${String.format("%02d", hour)}:${String.format("%02d", minute)}"
    }

    fun calculateHourTimeInMillis(hour: Int, minute: Int): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)
        return calendar.timeInMillis
    }
    fun isDateSelectable(
        selectedDate: Calendar,
        currentMonth: Calendar,
        futureDate: Calendar
    ): Boolean {
        return selectedDate in currentMonth..futureDate
    }
    fun Calendar.format24Hours(): String {
        return android.text.format.DateFormat.format("HH:mm", this).toString()
    }
    var selectedAssignmentDayToSave = ""
    var selectedAssignmentDay = ""

    fun pickADate(context: Context) {
        val calendar = Calendar.getInstance()

        // Fetching current year, month and day
        val year = calendar[Calendar.YEAR]
        val month = calendar[Calendar.MONTH]
        val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

        val currentMonth = Calendar.getInstance()
        currentMonth.set(Calendar.DAY_OF_MONTH, 1)

// Set the futureDate to the last day of the current month
        val futureDate = Calendar.getInstance()
        futureDate.time = currentMonth.time
        futureDate.set(Calendar.DAY_OF_MONTH, 30)

       val datePicker = DatePickerDialog(
            context,
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

                if (isDateSelectable(selectedDate, currentMonth, futureDate)) {
                    // The selected date is within the allowed range
                    val selectedMillis = selectedDate.timeInMillis
                    // Now, 'selectedMillis' contains the time in milliseconds
                    selectedAssignmentDayToSave = selectedMillis.toString()
                    selectedAssignmentDay = getDate(selectedMillis.toString().toLong(), "EEE, dd MMM, yyyy")
                } else {
                    // The selected date is not within the allowed range
                    context.toast("Invalid date selection")
                }
            },
            year,
            month,
            dayOfMonth
        )


// Show the DatePickerDialog



        /*
        when supervisor selects the date, only this month's dates will be clickable, future ones
        when supervisor selects a date, the system checks if the staff already has a shift that day
         */

    }

    fun isTimeInRange(startTime: String, endTime: String, givenStartTime: String, givenStopTime: String): Boolean {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        val startDateTime = dateFormat.parse(startTime)
        val endDateTime = dateFormat.parse(endTime)
        val givenStartDateTime = dateFormat.parse(givenStartTime)
        val givenStopDateTime = dateFormat.parse(givenStopTime)

        if (startDateTime != null && endDateTime != null && givenStartDateTime != null && givenStopDateTime != null) {
            val startMillis = startDateTime.time
            val endMillis = endDateTime.time
            val givenStartMillis = givenStartDateTime.time
            val givenStopMillis = givenStopDateTime.time

            return givenStartMillis in startMillis..endMillis || givenStopMillis in startMillis .. endMillis
        }

        return false
    }

    fun convertMillisToHourAndMinute(milliSeconds: Long): String{
// Convert milliseconds to minutes
        val totalMinutes = milliSeconds / (1000 * 60)

// Calculate hours and minutes
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60

        return "$hours:$minutes"
    }
}