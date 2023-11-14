/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_shifts

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.CardItem
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.categorizeDatesByWeekInMonth
import org.devstrike.app.medcareaidscheduler.utils.convertDateTimeToMillis
import org.devstrike.app.medcareaidscheduler.utils.getCurrentDate
import org.devstrike.app.medcareaidscheduler.utils.getDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SupervisorHouseShiftDetail(
    houseDetail: House,
    onDismiss: () -> Unit,
    allAssignments: List<AssignedShift>
) {

    val TAG = "SupervisorHouseShiftDetail"
    Log.d(TAG, "SupervisorHouseShiftDetail: $TAG")

    val context = LocalContext.current
    var week1Expanded by remember { mutableStateOf(false) }
    var week2Expanded by remember { mutableStateOf(false) }
    var week3Expanded by remember { mutableStateOf(false) }
    var week4Expanded by remember { mutableStateOf(false) }

    val weekNames = listOf("Week 1", "Week 2", "Week 3", "Week 4")
    val houseShiftsByWeek = mutableMapOf<String, List<AssignedShift>>(
        "Week 1" to listOf(AssignedShift()),
        "Week 2" to listOf(AssignedShift()),
        "Week 3" to listOf(AssignedShift()),
        "Week 4" to listOf(AssignedShift()),
        "Week 5" to listOf(AssignedShift()),
    )

    val houseShiftsByWeekDays = mutableMapOf<String, List<AssignedShift>>(
        "Monday" to listOf(AssignedShift()),
        "Tuesday" to listOf(AssignedShift()),
        "Wednesday" to listOf(AssignedShift()),
        "Thursday" to listOf(AssignedShift()),
        "Friday" to listOf(AssignedShift()),
        "Saturday" to listOf(AssignedShift()),
        "Sunday" to listOf(AssignedShift())
    )

    Column {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Surface(
                shape = CircleShape,
                modifier = Modifier
                    .size(64.dp)
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.secondary
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = houseDetail.houseName.substring(0, 1).toString(),
                        fontWeight = FontWeight.Bold,
                        style = Typography.displaySmall
                    )
                }
            }

        }
        Text(
            text = houseDetail.houseName,
            style = Typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )


        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(
                id = R.string.staff_shift_screen_title,
                getCurrentDate("MMMM, yyyy")
            ), fontStyle = FontStyle.Italic
        )
        Spacer(modifier = Modifier.height(24.dp))

        Column {
            val listOfDates: MutableMap<String, List<AssignedShift>> = mutableMapOf()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.3.dp, MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Week 1",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.8f)
                )
                IconButton(
                    onClick = {
                        week1Expanded = !week1Expanded
                        /*when(week){
                            "Week 1" -> {

                            }
                            "Week 2" -> {
                                week2Expanded = !week2Expanded
                            }
                            "Week 3" -> {
                                week3Expanded = !week3Expanded
                            }
                            "Week 4" -> {
                                week4Expanded = !week4Expanded
                            }
                        }
*/
                    },
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                ) {
                    Image(
                        painter = if (week1Expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                            R.drawable.ic_arrow_down
                        ),
                        contentDescription = "Toggle Password Visibility",
                    )
                }
            }

            if (week1Expanded) {
                //when expanded show another expandable card
                //card of days
                Log.d(TAG, "SupervisorHouseShiftDetail allAssignments: $allAssignments")
                for (shift in allAssignments) {
                    /*
                    * Get all the shifts in the same week and add to a map with the key as the week number
                    * Display the list of keys in a lazy column
                    * When each key card is clicked, all days of the week from Monday to Sunday is displayed
                    * When each day is clicked, the Day, Night and SleepOver is displayed in a list and the assigned staff beside them
                    * If the assigned staff is empty or null, it shows indication to assign
                    *
                    * */

                    val mapKey =
                        getDate(shift.assignedShiftDate.toLong(), "yyyy-MMM-dd")
                    val list = listOfDates.getOrDefault(mapKey, mutableListOf())
                        .toMutableList()

                    list.add(shift) // Add the new value to the list

                    listOfDates[mapKey] = list


                }
                val dateFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH)

                val dates = listOfDates.map { dateFormat.parse(it.key) ?: Date() }

                val groupedDates = categorizeDatesByWeekInMonth(dates)
                Log.d(TAG, "Grouped Dates: $groupedDates")

                val weekAssignments: MutableList<AssignedShift> =
                    mutableListOf()
                for (shift in allAssignments) {
                    groupedDates[1]?.forEach {
                        if (it.toString().isNotEmpty()) {
                            Log.d(TAG, "SupervisorHouseShiftDetail: 1 $it")
                            if (getDate(
                                    shift.assignedShiftDate.toLong(),
                                    "yyyy-MMM-dd"
                                ).toLong() == convertDateTimeToMillis(
                                    it.toString(),
                                    "yyyy-MMM-dd"
                                )
                            ) {
                                weekAssignments.add(shift)
                            }
                        }
                    }
                }
                houseShiftsByWeek["Week 1"] = weekAssignments

            /*    Column {
                    CardItem(text = "Week 1")
                    Text(
                        text = getDate(
                            houseShiftsByWeek["Week 1"]?.get(0)?.assignedShiftDate?.toLong(),
                            "EEE"
                        ) ?: "Not Assigned"
                    )
                }*/

                /*                                    2 -> {
                                                        val weekAssignments: MutableList<AssignedShift> =
                                                            mutableListOf()
                                                        for (shift in allAssignments) {
                                                            groupedDates[2]?.forEach {
                                                                if (it.toString().isNotEmpty()){
                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 2 $it")

                                                                    if (getDate(
                                                                            shift.assignedShiftDate.toLong(),
                                                                            "yyyy-MMM-dd"
                                                                        ).toLong() == convertDateTimeToMillis(
                                                                            it.toString(),
                                                                            "yyyy-MMM-dd"
                                                                        )
                                                                    ) {
                                                                        weekAssignments.add(shift)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        houseShiftsByWeek["Week 2"] = weekAssignments
                                                        if (week == "Week 2"){
                                                            Column {
                                                                CardItem(text = "Week 2")
                                                                Text(
                                                                    text = getDate(
                                                                        houseShiftsByWeek["Week 2"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                        "EEE"
                                                                    ) ?: "Not Assigned"
                                                                )
                                                            }
                                                        }
                                                    }

                                                    3 -> {
                                                        val weekAssignments: MutableList<AssignedShift> =
                                                            mutableListOf()
                                                        for (shift in allAssignments) {
                                                            groupedDates[3]?.forEach {
                                                                if (it.toString().isNotEmpty()) {
                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 3 $it")

                                                                    if (getDate(
                                                                            shift.assignedShiftDate.toLong(),
                                                                            "yyyy-MMM-dd"
                                                                        ).toLong() == convertDateTimeToMillis(
                                                                            it.toString(),
                                                                            "yyyy-MMM-dd"
                                                                        )
                                                                    ) {
                                                                        weekAssignments.add(shift)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        houseShiftsByWeek["Week 3"] = weekAssignments
                                                        if (week == "Week 3") {
                                                            Column {
                                                                CardItem(text = "Week 3")
                                                                Text(
                                                                    text = getDate(
                                                                        houseShiftsByWeek["Week 3"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                        "EEE"
                                                                    ) ?: "Not Assigned"
                                                                )
                                                            }
                                                        }
                                                    }

                                                    4 -> {
                                                        val weekAssignments: MutableList<AssignedShift> =
                                                            mutableListOf()
                                                        for (shift in allAssignments) {
                                                            groupedDates[4]?.forEach {
                                                                if (it.toString().isNotEmpty()) {
                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 4 $it")

                                                                    if (getDate(
                                                                            shift.assignedShiftDate.toLong(),
                                                                            "yyyy-MMM-dd"
                                                                        ).toLong() == convertDateTimeToMillis(
                                                                            it.toString(),
                                                                            "yyyy-MMM-dd"
                                                                        )
                                                                    ) {
                                                                        weekAssignments.add(shift)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        houseShiftsByWeek["Week 4"] = weekAssignments
                                                        if (week == "Week 4") {
                                                            Column {
                                                                CardItem(text = "Week 4")
                                                                Text(
                                                                    text = getDate(
                                                                        houseShiftsByWeek["Week 4"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                        "EEE"
                                                                    ) ?: "Not Assigned"
                                                                )
                                                            }
                                                        }
                                                    }

                                                    5 -> {
                                                        val weekAssignments: MutableList<AssignedShift> =
                                                            mutableListOf()
                                                        for (shift in allAssignments) {
                                                            groupedDates[1]?.forEach {
                                                                if (it.toString().isNotEmpty()) {
                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 5 $it")

                                                                    if (getDate(
                                                                            shift.assignedShiftDate.toLong(),
                                                                            "yyyy-MMM-dd"
                                                                        ).toLong() == convertDateTimeToMillis(
                                                                            it.toString(),
                                                                            "yyyy-MMM-dd"
                                                                        )
                                                                    ) {
                                                                        weekAssignments.add(shift)
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        houseShiftsByWeek["Week 5"] = weekAssignments
                                                        if (week == "Week 5") {
                                                            Column {
                                                                CardItem(text = "Week 5")
                                                                Text(
                                                                    text = getDate(
                                                                        houseShiftsByWeek["Week 5"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                        "EEE"
                                                                    ) ?: "Not Assigned"
                                                                )
                                                            }
                                                        }
                                                    }

                                                    6 -> {

                                                        val weekAssignments: MutableList<AssignedShift> =
                                                            mutableListOf()
                                                        for (shift in allAssignments) {
                                                            groupedDates[6]?.forEach {
                                                                if (it.toString().isNotEmpty()) {
                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 6 $it")

                                                                    if (getDate(
                                                                            shift.assignedShiftDate.toLong(),
                                                                            "yyyy-MMM-dd"
                                                                        ).toLong() == convertDateTimeToMillis(
                                                                            it.toString(),
                                                                            "yyyy-MMM-dd"
                                                                        )
                                                                    ) {
                                                                        weekAssignments.add(shift)
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        houseShiftsByWeek["Week 6"] = weekAssignments
                                                        if (week == "Week 6") {
                                                            Column {
                                                                CardItem(text = "Week 6")
                                                                Text(
                                                                    text = getDate(
                                                                        houseShiftsByWeek["Week 6"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                        "EEE"
                                                                    ) ?: "Not Assigned"
                                                                )
                                                            }
                                                        }
                                                    }

                                                    7 -> {
                                                        val weekAssignments: MutableList<AssignedShift> =
                                                            mutableListOf()
                                                        for (shift in allAssignments) {
                                                            groupedDates[7]?.forEach {
                                                                if (it.toString().isNotEmpty()) {
                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 7 $it")

                                                                    if (getDate(
                                                                            shift.assignedShiftDate.toLong(),
                                                                            "yyyy-MMM-dd"
                                                                        ).toLong() == convertDateTimeToMillis(
                                                                            it.toString(),
                                                                            "yyyy-MMM-dd"
                                                                        )
                                                                    ) {
                                                                        weekAssignments.add(shift)
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        houseShiftsByWeek["Week 7"] = weekAssignments
                                                        if (week == "Week 7") {
                                                            Column {
                                                                CardItem(text = "Week 7")
                                                                Text(
                                                                    text = getDate(
                                                                        houseShiftsByWeek["Week 7"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                        "EEE"
                                                                    ) ?: "Not Assigned"
                                                                )
                                                            }
                                                        }
                                                    }*/

                //}


                Text(
                    text = "Content Sample for Display on Expansion of Card",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }


        LazyColumn() {
            items(weekNames) { week ->
                Column {
                    val listOfDates: MutableMap<String, List<AssignedShift>> = mutableMapOf()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(width = 0.3.dp, MaterialTheme.colorScheme.primary),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = week,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.8f)
                        )
                        IconButton(
                            onClick = {
                                when (week) {
                                    "Week 1" -> {
                                        week1Expanded = !week1Expanded
                                    }

                                    "Week 2" -> {
                                        week2Expanded = !week2Expanded
                                    }

                                    "Week 3" -> {
                                        week3Expanded = !week3Expanded
                                    }

                                    "Week 4" -> {
                                        week4Expanded = !week4Expanded
                                    }
                                }

                            },
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(4.dp)
                        ) {
                            Image(
                                painter = if (week1Expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                                    R.drawable.ic_arrow_down
                                ),
                                contentDescription = "Toggle Password Visibility",
                            )
                        }
                    }

                   /* if (week1Expanded) {
                        //when expanded show another expandable card
                        //card of days
                        Log.d(TAG, "SupervisorHouseShiftDetail allAssignments: $allAssignments")
                        for (shift in allAssignments) {
                            *//*
                            * Get all the shifts in the same week and add to a map with the key as the week number
                            * Display the list of keys in a lazy column
                            * When each key card is clicked, all days of the week from Monday to Sunday is displayed
                            * When each day is clicked, the Day, Night and SleepOver is displayed in a list and the assigned staff beside them
                            * If the assigned staff is empty or null, it shows indication to assign
                            *
                            * *//*

                            val mapKey =
                                getDate(shift.assignedShiftDate.toLong(), "yyyy-MMM-dd")
                            val list = listOfDates.getOrDefault(mapKey, mutableListOf())
                                .toMutableList()

                            list.add(shift) // Add the new value to the list

                            listOfDates[mapKey] = list


                        }
                        val dateFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH)

                        val dates = listOfDates.map { dateFormat.parse(it.key) ?: Date() }

                        val groupedDates = categorizeDatesByWeekInMonth(dates)
                        Log.d(TAG, "Grouped Dates: $groupedDates")
                        for (weekName in groupedDates.keys) {
                            when (weekName) {
                                1 -> {
                                    val weekAssignments: MutableList<AssignedShift> =
                                        mutableListOf()
                                    for (shift in allAssignments) {
                                        groupedDates[1]?.forEach {
                                            if (it.toString().isNotEmpty()) {
                                                Log.d(TAG, "SupervisorHouseShiftDetail: 1 $it")
                                                if (getDate(
                                                        shift.assignedShiftDate.toLong(),
                                                        "yyyy-MMM-dd"
                                                    ).toLong() == convertDateTimeToMillis(
                                                        it.toString(),
                                                        "yyyy-MMM-dd"
                                                    )
                                                ) {
                                                    weekAssignments.add(shift)
                                                }
                                            }
                                        }
                                    }
                                    houseShiftsByWeek["Week 1"] = weekAssignments
                                    if (week == "Week 1") {

                                        Column {
                                            CardItem(text = "Week 1")
                                            Text(
                                                text = getDate(
                                                    houseShiftsByWeek["Week 1"]?.get(0)?.assignedShiftDate?.toLong(),
                                                    "EEE"
                                                ) ?: "Not Assigned"
                                            )
                                        }
                                    }

                                }

                                *//*                                    2 -> {
                                                                        val weekAssignments: MutableList<AssignedShift> =
                                                                            mutableListOf()
                                                                        for (shift in allAssignments) {
                                                                            groupedDates[2]?.forEach {
                                                                                if (it.toString().isNotEmpty()){
                                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 2 $it")

                                                                                    if (getDate(
                                                                                            shift.assignedShiftDate.toLong(),
                                                                                            "yyyy-MMM-dd"
                                                                                        ).toLong() == convertDateTimeToMillis(
                                                                                            it.toString(),
                                                                                            "yyyy-MMM-dd"
                                                                                        )
                                                                                    ) {
                                                                                        weekAssignments.add(shift)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        houseShiftsByWeek["Week 2"] = weekAssignments
                                                                        if (week == "Week 2"){
                                                                            Column {
                                                                                CardItem(text = "Week 2")
                                                                                Text(
                                                                                    text = getDate(
                                                                                        houseShiftsByWeek["Week 2"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                                        "EEE"
                                                                                    ) ?: "Not Assigned"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    3 -> {
                                                                        val weekAssignments: MutableList<AssignedShift> =
                                                                            mutableListOf()
                                                                        for (shift in allAssignments) {
                                                                            groupedDates[3]?.forEach {
                                                                                if (it.toString().isNotEmpty()) {
                                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 3 $it")

                                                                                    if (getDate(
                                                                                            shift.assignedShiftDate.toLong(),
                                                                                            "yyyy-MMM-dd"
                                                                                        ).toLong() == convertDateTimeToMillis(
                                                                                            it.toString(),
                                                                                            "yyyy-MMM-dd"
                                                                                        )
                                                                                    ) {
                                                                                        weekAssignments.add(shift)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        houseShiftsByWeek["Week 3"] = weekAssignments
                                                                        if (week == "Week 3") {
                                                                            Column {
                                                                                CardItem(text = "Week 3")
                                                                                Text(
                                                                                    text = getDate(
                                                                                        houseShiftsByWeek["Week 3"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                                        "EEE"
                                                                                    ) ?: "Not Assigned"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    4 -> {
                                                                        val weekAssignments: MutableList<AssignedShift> =
                                                                            mutableListOf()
                                                                        for (shift in allAssignments) {
                                                                            groupedDates[4]?.forEach {
                                                                                if (it.toString().isNotEmpty()) {
                                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 4 $it")

                                                                                    if (getDate(
                                                                                            shift.assignedShiftDate.toLong(),
                                                                                            "yyyy-MMM-dd"
                                                                                        ).toLong() == convertDateTimeToMillis(
                                                                                            it.toString(),
                                                                                            "yyyy-MMM-dd"
                                                                                        )
                                                                                    ) {
                                                                                        weekAssignments.add(shift)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        houseShiftsByWeek["Week 4"] = weekAssignments
                                                                        if (week == "Week 4") {
                                                                            Column {
                                                                                CardItem(text = "Week 4")
                                                                                Text(
                                                                                    text = getDate(
                                                                                        houseShiftsByWeek["Week 4"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                                        "EEE"
                                                                                    ) ?: "Not Assigned"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    5 -> {
                                                                        val weekAssignments: MutableList<AssignedShift> =
                                                                            mutableListOf()
                                                                        for (shift in allAssignments) {
                                                                            groupedDates[1]?.forEach {
                                                                                if (it.toString().isNotEmpty()) {
                                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 5 $it")

                                                                                    if (getDate(
                                                                                            shift.assignedShiftDate.toLong(),
                                                                                            "yyyy-MMM-dd"
                                                                                        ).toLong() == convertDateTimeToMillis(
                                                                                            it.toString(),
                                                                                            "yyyy-MMM-dd"
                                                                                        )
                                                                                    ) {
                                                                                        weekAssignments.add(shift)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                        houseShiftsByWeek["Week 5"] = weekAssignments
                                                                        if (week == "Week 5") {
                                                                            Column {
                                                                                CardItem(text = "Week 5")
                                                                                Text(
                                                                                    text = getDate(
                                                                                        houseShiftsByWeek["Week 5"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                                        "EEE"
                                                                                    ) ?: "Not Assigned"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    6 -> {

                                                                        val weekAssignments: MutableList<AssignedShift> =
                                                                            mutableListOf()
                                                                        for (shift in allAssignments) {
                                                                            groupedDates[6]?.forEach {
                                                                                if (it.toString().isNotEmpty()) {
                                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 6 $it")

                                                                                    if (getDate(
                                                                                            shift.assignedShiftDate.toLong(),
                                                                                            "yyyy-MMM-dd"
                                                                                        ).toLong() == convertDateTimeToMillis(
                                                                                            it.toString(),
                                                                                            "yyyy-MMM-dd"
                                                                                        )
                                                                                    ) {
                                                                                        weekAssignments.add(shift)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }

                                                                        houseShiftsByWeek["Week 6"] = weekAssignments
                                                                        if (week == "Week 6") {
                                                                            Column {
                                                                                CardItem(text = "Week 6")
                                                                                Text(
                                                                                    text = getDate(
                                                                                        houseShiftsByWeek["Week 6"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                                        "EEE"
                                                                                    ) ?: "Not Assigned"
                                                                                )
                                                                            }
                                                                        }
                                                                    }

                                                                    7 -> {
                                                                        val weekAssignments: MutableList<AssignedShift> =
                                                                            mutableListOf()
                                                                        for (shift in allAssignments) {
                                                                            groupedDates[7]?.forEach {
                                                                                if (it.toString().isNotEmpty()) {
                                                                                    Log.d(TAG, "SupervisorHouseShiftDetail: 7 $it")

                                                                                    if (getDate(
                                                                                            shift.assignedShiftDate.toLong(),
                                                                                            "yyyy-MMM-dd"
                                                                                        ).toLong() == convertDateTimeToMillis(
                                                                                            it.toString(),
                                                                                            "yyyy-MMM-dd"
                                                                                        )
                                                                                    ) {
                                                                                        weekAssignments.add(shift)
                                                                                    }
                                                                                }
                                                                            }
                                                                        }

                                                                        houseShiftsByWeek["Week 7"] = weekAssignments
                                                                        if (week == "Week 7") {
                                                                            Column {
                                                                                CardItem(text = "Week 7")
                                                                                Text(
                                                                                    text = getDate(
                                                                                        houseShiftsByWeek["Week 7"]?.get(0)?.assignedShiftDate?.toLong(),
                                                                                        "EEE"
                                                                                    ) ?: "Not Assigned"
                                                                                )
                                                                            }
                                                                        }
                                                                    }*//*
                            }
                        }


                        Text(
                            text = "Content Sample for Display on Expansion of Card",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }*/
                }

            }
        }


        ButtonComponent(
            buttonText = stringResource(
                id = R.string.okay_button,

                ), onClick = {
                onDismiss()
            }, modifier = Modifier.padding(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))


    }

}