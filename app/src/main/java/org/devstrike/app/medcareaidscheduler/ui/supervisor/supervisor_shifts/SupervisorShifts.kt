/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_shifts

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.CardItem
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.ui.theme.Purple80
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.TIME_FORMAT_HM
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.categorizeDatesByWeekInMonth
import org.devstrike.app.medcareaidscheduler.utils.convertDateTimeToMillis
import org.devstrike.app.medcareaidscheduler.utils.getCurrentDate
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPagerApi::class)
@Composable
fun SupervisorShifts(navController: NavHostController) {

    val TAG = "SupervisorShifts"

    val context = LocalContext.current

    var showModal by rememberSaveable {
        mutableStateOf(false)
    }
    var isNewShiftClicked by rememberSaveable {
        mutableStateOf(false)
    }
    val houseShiftList = remember { mutableStateOf(listOf<AssignedShift>()) }


    var isShiftDetailClicked by rememberSaveable {
        mutableStateOf(false)
    }


    // Get the list of items from Firestore
    val shiftTypes = remember { mutableStateOf(listOf<ShiftType>()) }
    val shiftTypeData: MutableState<ShiftType> = remember { mutableStateOf(ShiftType()) }
    val userProvince = getUser(userId = auth.uid!!, context = context)!!.userProvinceID

    val pagerState = rememberPagerState(pageCount = 3)
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
    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false
    }

    LaunchedEffect(Unit) {
        val shiftTypeList = mutableListOf<ShiftType>()
        val assignedShiftsList = mutableListOf<AssignedShift>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.shiftCollectionRef.whereEqualTo("shiftTypeOwnerProvinceID", userProvince)
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(ShiftType::class.java)
                shiftTypeList.add(item)
            }
        }
        shiftTypes.value = shiftTypeList
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")

        withContext(Dispatchers.IO) {
            val queryAllHouses = Common.assignedShiftsCollectionRef.whereEqualTo(
                "assignedSupervisorID",
                auth.uid!!
            ).get().await()


            for (document in queryAllHouses) {
                val item = document.toObject(AssignedShift::class.java)
                Log.d(TAG, "item: $item")
                if (isTimeInCurrentMonth(item.assignedShiftDate.toLong())) {
                    assignedShiftsList.add(item)
                }
            }
            houseShiftList.value = assignedShiftsList
        }
    }


    Scaffold(
       /* floatingActionButton = {
            FloatActionButton(modifier = Modifier.clickable {
//            isNewConnectClicked = true
            }, fabText = "Add Shift Type", onClick = {
                showModal = true
                isNewShiftClicked = true
            })
        },
        floatingActionButtonPosition = FabPosition.End,*/
    ) {
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isTaskRunning.value) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        Column(modifier = Modifier.padding(it)) {
            //search bar
            //list of cards

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(100.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                ), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Shift Types", modifier = Modifier.padding(8.dp))
                    if (shiftTypes.value.size < 3){
                        Text(text = "Add Shift Type", modifier = Modifier.padding(8.dp).clickable {
                            showModal = true
                            isNewShiftClicked = true
                        })

                    }
                }


                if (shiftTypes.value.isEmpty()) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "Click the add button to add shift types for your province",
                            fontStyle = FontStyle.Italic
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(8.dp)) {

                        val listOfShiftTypes = shiftTypes.value

                        items(listOfShiftTypes) { shiftType ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(text = shiftType.shiftTypeName, modifier = Modifier.weight(0.5F))
                                Text(
                                    text = "${
                                        getDate(
                                            shiftType.shiftTypeStartTime.toLong(),
                                            TIME_FORMAT_HM
                                        )
                                    } - ${
                                        getDate(
                                            shiftType.shiftTypeEndTime.toLong(),
                                            TIME_FORMAT_HM
                                        )
                                    }",
                                    modifier = Modifier.weight(0.4F)
                                )
                                Text(text = "edit", modifier = Modifier.weight(0.1F))
                            }
                        }
                    }
                }
            }


            Divider(modifier = Modifier.padding(8.dp))
            //show thw months
            Column {
                Text(
                    text = stringResource(
                        id = R.string.staff_shift_screen_title,
                        getCurrentDate("MMMM, yyyy")
                    ), fontStyle = FontStyle.Italic, modifier = Modifier.padding(8.dp)
                )
                Column {
                    val listOfDates: MutableMap<String, List<AssignedShift>> = mutableMapOf()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
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
                        isTaskRunning.value = true
                        Log.d(
                            TAG,
                            "SupervisorHouseShiftDetail allAssignments: ${houseShiftList.value}"
                        )

                        for (shift in houseShiftList.value) {
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
                        for (shift in houseShiftList.value) {
                            groupedDates[1]?.forEach {
                                if (it.toString().isNotEmpty()) {
                                    Log.d(TAG, "SupervisorHouseShiftDetail: 1 $it")
                                    Log.d(
                                        TAG,
                                        "SupervisorHouseShiftDetail: assignedShiftDate ${shift.assignedShiftDate}"
                                    )
                                    val shiftDate = getDate(
                                        shift.assignedShiftDate.toLong(),
                                        "yyyy-MMM-dd"
                                    )
                                    val weekDate = convertDateTimeToMillis(
                                        it.toString(),
                                        "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
                                    ).toString()
                                    Log.d(TAG, "weekDate: $shiftDate")
                                    Log.d(TAG, "weekDate: $weekDate")
                                    if (shiftDate
                                        == getDate(weekDate.toLong(), "yyyy-MMM-dd")
                                    ) {
                                        weekAssignments.add(shift)
                                        Log.d(
                                            TAG,
                                            "SupervisorShifts weekAssignments: $weekAssignments"
                                        )
                                    }
                                }
                            }
                        }
                        isTaskRunning.value = false
                        houseShiftsByWeek["Week 1"] = weekAssignments
                        Log.d(TAG, "SupervisorShifts: ${houseShiftsByWeek["Week 1"]}")

                        Column(modifier = Modifier.padding(8.dp)) {
                            val week1Shifts = houseShiftsByWeek["Week 1"]
                            if (week1Shifts?.isNotEmpty() == true) {
                                LazyColumn() {
                                    items(week1Shifts) { shift ->
                                        CardItem(assignment = shift)
                                    }
                                }
                            } else {
                                Text(
                                    text = "No assigned shifts for week 1",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                            }

                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(width = 0.3.dp, MaterialTheme.colorScheme.primary),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Week 2",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.8f)
                        )
                        IconButton(
                            onClick = {
                                week2Expanded = !week2Expanded

                            },
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(4.dp)
                        ) {
                            Image(
                                painter = if (week2Expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                                    R.drawable.ic_arrow_down
                                ),
                                contentDescription = "Toggle Password Visibility",
                            )
                        }
                    }

                    if (week2Expanded) {
                        //when expanded show another expandable card
                        //card of days
                        isTaskRunning.value = true

                        Log.d(
                            TAG,
                            "SupervisorHouseShiftDetail allAssignments: ${houseShiftList.value}"
                        )
                        for (shift in houseShiftList.value) {
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
                        for (shift in houseShiftList.value) {
                            groupedDates[2]?.forEach {
                                if (it.toString().isNotEmpty()) {
                                    Log.d(TAG, "SupervisorHouseShiftDetail: 1 $it")
                                    Log.d(
                                        TAG,
                                        "SupervisorHouseShiftDetail: assignedShiftDate ${shift.assignedShiftDate}"
                                    )
                                    val shiftDate = getDate(
                                        shift.assignedShiftDate.toLong(),
                                        "yyyy-MMM-dd"
                                    )
                                    val weekDate = convertDateTimeToMillis(
                                        it.toString(),
                                        "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
                                    ).toString()
                                    Log.d(TAG, "weekDate: $shiftDate")
                                    Log.d(TAG, "weekDate: $weekDate")
                                    if (shiftDate
                                        == getDate(weekDate.toLong(), "yyyy-MMM-dd")
                                    ) {
                                        weekAssignments.add(shift)
                                        Log.d(
                                            TAG,
                                            "SupervisorShifts weekAssignments: $weekAssignments"
                                        )
                                    }
                                }
                            }
                        }
                        houseShiftsByWeek["Week 2"] = weekAssignments
                        Log.d(TAG, "SupervisorShifts: ${houseShiftsByWeek["Week 2"]}")
                        isTaskRunning.value = false
                        Column() {
                            val week1Shifts = houseShiftsByWeek["Week 2"]
                            if (week1Shifts?.isNotEmpty() == true) {
                                LazyColumn(modifier = Modifier.padding(8.dp)) {
                                    items(week1Shifts) { shift ->
                                        CardItem(assignment = shift)
                                    }
                                }
                            } else {
                                Text(
                                    text = "No assigned shifts for week 2",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                            }

                        }

                    }

                    //WEEK 2 ENDS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(width = 0.3.dp, MaterialTheme.colorScheme.primary),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Week 3",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.8f)
                        )
                        IconButton(
                            onClick = {

                                week3Expanded = !week3Expanded
                            },
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(4.dp)
                        ) {
                            Image(
                                painter = if (week3Expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                                    R.drawable.ic_arrow_down
                                ),
                                contentDescription = "Toggle Password Visibility",
                            )
                        }
                    }

                    if (week3Expanded) {
                        //when expanded show another expandable card
                        //card of days
                        Log.d(
                            TAG,
                            "SupervisorHouseShiftDetail allAssignments: ${houseShiftList.value}"
                        )
                        isTaskRunning.value = true
                        for (shift in houseShiftList.value) {
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
                        for (shift in houseShiftList.value) {
                            groupedDates[3]?.forEach {
                                if (it.toString().isNotEmpty()) {
                                    Log.d(TAG, "SupervisorHouseShiftDetail: 1 $it")
                                    Log.d(
                                        TAG,
                                        "SupervisorHouseShiftDetail: assignedShiftDate ${shift.assignedShiftDate}"
                                    )
                                    val shiftDate = getDate(
                                        shift.assignedShiftDate.toLong(),
                                        "yyyy-MMM-dd"
                                    )
                                    val weekDate = convertDateTimeToMillis(
                                        it.toString(),
                                        "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
                                    ).toString()
                                    Log.d(TAG, "weekDate: $shiftDate")
                                    Log.d(TAG, "weekDate: $weekDate")
                                    if (shiftDate
                                        == getDate(weekDate.toLong(), "yyyy-MMM-dd")
                                    ) {
                                        weekAssignments.add(shift)
                                        Log.d(
                                            TAG,
                                            "SupervisorShifts weekAssignments: $weekAssignments"
                                        )
                                    }
                                }
                            }
                        }
                        houseShiftsByWeek["Week 3"] = weekAssignments
                        Log.d(TAG, "SupervisorShifts: ${houseShiftsByWeek["Week 3"]}")

                        isTaskRunning.value = false

                        Column() {
                            val week1Shifts = houseShiftsByWeek["Week 3"]
                            if (week1Shifts?.isNotEmpty() == true) {
                                LazyColumn(modifier = Modifier.padding(8.dp)) {
                                    items(week1Shifts) { shift ->
                                        CardItem(assignment = shift)
                                    }
                                }
                            } else {
                                Text(
                                    text = "No assigned shifts for week 3",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                            }

                        }
                    }
                    //WEEK 3 ENDS

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .border(width = 0.3.dp, MaterialTheme.colorScheme.primary),
                        verticalAlignment = Alignment.CenterVertically
                    )
                    {
                        Text(
                            text = "Week 4",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(8.dp)
                                .weight(0.8f)
                        )
                        IconButton(
                            onClick = {
                                week4Expanded = !week4Expanded
                            },
                            modifier = Modifier
                                .weight(0.2f)
                                .padding(4.dp)
                        ) {
                            Image(
                                painter = if (week4Expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                                    R.drawable.ic_arrow_down
                                ),
                                contentDescription = "Toggle Password Visibility",
                            )
                        }
                    }

                    if (week4Expanded) {
                        //when expanded show another expandable card
                        //card of days
                        isTaskRunning.value = true

                        Log.d(
                            TAG,
                            "SupervisorHouseShiftDetail allAssignments: ${houseShiftList.value}"
                        )
                        for (shift in houseShiftList.value) {
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
                        for (shift in houseShiftList.value) {
                            groupedDates[4]?.forEach {
                                if (it.toString().isNotEmpty()) {
                                    Log.d(TAG, "SupervisorHouseShiftDetail: 4 $it")
                                    Log.d(
                                        TAG,
                                        "SupervisorHouseShiftDetail: assignedShiftDate ${shift.assignedShiftDate}"
                                    )
                                    val shiftDate = getDate(
                                        shift.assignedShiftDate.toLong(),
                                        "yyyy-MMM-dd"
                                    )
                                    val weekDate = convertDateTimeToMillis(
                                        it.toString(),
                                        "EEE MMM dd HH:mm:ss 'GMT'Z yyyy"
                                    ).toString()
                                    Log.d(TAG, "weekDate: $shiftDate")
                                    Log.d(TAG, "weekDate: $weekDate")
                                    if (shiftDate
                                        == getDate(weekDate.toLong(), "yyyy-MMM-dd")
                                    ) {
                                        weekAssignments.add(shift)
                                        Log.d(
                                            TAG,
                                            "SupervisorShifts weekAssignments: $weekAssignments"
                                        )
                                    }
                                }
                            }
                        }
                        houseShiftsByWeek["Week 4"] = weekAssignments
                        Log.d(TAG, "SupervisorShifts: ${houseShiftsByWeek["Week 4"]}")
                        isTaskRunning.value = false
                        Column() {
                            val week1Shifts = houseShiftsByWeek["Week 4"]
                            if (week1Shifts?.isNotEmpty() == true) {
                                LazyColumn(modifier = Modifier.padding(8.dp)) {
                                    items(week1Shifts) { shift ->
                                        CardItem(assignment = shift)
                                    }
                                }
                            } else {
                                Text(
                                    text = "No assigned shifts for week 4",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontStyle = FontStyle.Italic
                                )
                            }

                        }


                    }

                    //WEEK 4 ENDS

                }


                // on the below line we are specifying the top app bar
                // and specifying background color for it.
                // on below line we are calling tabs
//                SupervisorShiftsTabs(pagerState = pagerState)
                // on below line we are calling tabs content
                // for displaying our page for each tab layout
//                SupervisorShiftsTabsContent(pagerState = pagerState)
            }
        }
        if (showModal) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showModal = false }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isNewShiftClicked)
                        SupervisorAddShiftType(shiftTypes.value, onClose = {
                            showModal = false
                            isNewShiftClicked = false
                        })
                }
            }
        }
    }

}

// on below line we are
// creating a function for tabs
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalPagerApi
@Composable
fun SupervisorShiftsTabs(pagerState: PagerState) {
    // in this function we are creating a list
    // in this list we are specifying data as
    // name of the tab and the icon for it.
    val list = listOf(
        "By Week",
        "By House",
        "By Staff",

        )
    // on below line we are creating
    // a variable for the scope.
    val scope = rememberCoroutineScope()
    // on below line we are creating a
    // individual row for our tab layout.
    TabRow(
        modifier = Modifier
            .padding(8.dp)
            .clip(
                RoundedCornerShape(8.dp)
            ),

        // on below line we are specifying
        // the selected index.
        selectedTabIndex = pagerState.currentPage,

        // on below line we are
        // specifying background color.
//        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,

        // on below line we are specifying content color.
//        contentColor = MarketPlaceDayColor,

        // on below line we are specifying
        // the indicator for the tab
//        indicator = { tabPositions ->
//            // on below line we are specifying the styling
//            // for tab indicator by specifying height
//            // and color for the tab indicator.
//            TabRowDefaults.Indicator(
//                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
//                height = 2.dp,
////                color = MarketPlaceDayColor
//            )
//        }
    ) {
        // on below line we are specifying icon
        // and text for the individual tab item
        list.forEachIndexed { index, _ ->
            // on below line we are creating a tab.
            Tab(
                // on below line we are specifying icon
                // for each tab item and we are calling
                // image from the list which we have created.
//                icon = {
//                    Icon(imageVector = list[index].second, contentDescription = null)
//                },
                // on below line we are specifying the text for
                // the each tab item and we are calling data
                // from the list which we have created.
                text = {
                    Text(
                        list[index],
                        // on below line we are specifying the text color
                        // for the text in that tab
                        color = if (pagerState.currentPage == index) Purple80 else Color.LightGray
                    )
                },
                // on below line we are specifying
                // the tab which is selected.
                selected = pagerState.currentPage == index,
                // on below line we are specifying the
                // on click for the tab which is selected.
                onClick = {
                    // on below line we are specifying the scope.
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}


// on below line we are creating a tab content method
// in which we will be displaying the individual page of our tab .
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalPagerApi
@Composable
fun SupervisorShiftsTabsContent(pagerState: PagerState) {
    // on below line we are creating
    // horizontal pager for our tab layout.
    HorizontalPager(state = pagerState) {
        // on below line we are specifying
        // the different pages.
            page ->
        when (page) {
            // on below line we are calling tab content screen
            // and specifying data as Home Screen.
            0 -> SupervisorByWeekShifts()
            // on below line we are calling tab content screen
            // and specifying data as Shopping Screen.
            1 -> SupervisorByHouseShifts()
            2 -> SupervisorByStaffShifts()
        }
    }
}
