/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_weekly_logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.ConcludedShift
import org.devstrike.app.medcareaidscheduler.data.ReportLog
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.ReportItemCard
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.libs.android.timetravel.TimeTraveller

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorWeekLogs(navController: NavHostController) {

    val context = LocalContext.current

    val TAG = "SupervisorWeekLogs"

    // Get the list of items from Firestore
    val weeklyLogs = remember { mutableStateOf(listOf<ReportLog>()) }
    val weeklyLogsData: MutableState<ReportLog> = remember { mutableStateOf(ReportLog()) }
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }
    var staffWeekLogs = remember { mutableStateOf(mapOf<String, List<ConcludedShift>>()) }
    var selectedStaffID = remember { mutableStateOf("") }
    var selectedStaffWeekLog = remember { mutableStateOf(listOf(ConcludedShift())) }
    var selectedStaffTotalHours = remember { mutableIntStateOf(0) }

   /* val staffWeekLogs by remember{
        MutableMap<String, MutableList<ConcludedShift>>()
     }*/


    val isTaskRunning = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isItemClicked = false


    LaunchedEffect(Unit) {

        val weeklyLogsList = mutableListOf<ConcludedShift>()
        val thisWeekLogs = mutableListOf<ConcludedShift>()
        val weekLogs = mutableMapOf<String, MutableList<ConcludedShift>>()
        val userDetails = getUser(auth.uid!!, context)!!

        isTaskRunning.value = true

        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.concludedShiftsCollectionRef.whereEqualTo(
                    "shiftProvinceID",
                    userDetails.userProvinceID
                )
                    .get().await()


            isTaskRunning.value = false

            for (document in querySnapshot) {
                val item = document.toObject(ConcludedShift::class.java)
                weeklyLogsList.add(item)
            }

            /*
            * 1. get all the concluded lists
            * 2. sort the ones that are this week
            * 3. group the logs into a map of staff and his logs
            * 4. add all the total hours in each staff's logs
            * 5. then display the list of all staff and their total week hours
            * 6. on click of each of the staff log, it pulls up the list of the week's logs
            * */
            //weeklyLogs.value = weeklyLogsList
            weeklyLogsList.forEach { log ->
                if (TimeTraveller.isDateWithinThisWeek(log.dateSubmitted.toLong())) {
                    thisWeekLogs.add(log)
                }
            }
            for (staffLog in weeklyLogsList) {
                if (weekLogs.isEmpty()) {
                    weekLogs[staffLog.shiftStaffID] = mutableListOf(staffLog)

                } else {
                    for (key in weekLogs.keys) {
                        if (key == staffLog.shiftStaffID) {
                            val staffLogs = weekLogs[key]
                            staffLogs?.add(staffLog)
                            weekLogs[key] = staffLogs!!

                            break
                        } else {
                            weekLogs[staffLog.shiftStaffID] = mutableListOf(staffLog)

                        }
                    }
                }
            }
        }
        staffWeekLogs.value = weekLogs

    }

    Surface {
        LazyColumn {

            item {
                TextFieldComponent(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = "Search",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                    ),
                    inputType = "Search",
                    leadingIcon = R.drawable.ic_search,
                    modifier = Modifier.padding(16.dp)

                )

            }
            if (staffWeekLogs.value.isNotEmpty()){

                items(staffWeekLogs.value.entries.toList()){concludedShift ->
                    var totalHours = 0
                    concludedShift.value.forEach {
                        totalHours += it.noOfTotalHours.toInt()
                    }
                    ReportItemCard(concludedShift.key, totalHours.toString()){
                        isSheetOpen = true
                        isItemClicked = true
                        selectedStaffID.value = concludedShift.key
                        selectedStaffWeekLog.value = concludedShift.value
                        selectedStaffTotalHours.intValue = totalHours

                    }
                }
            }

        }

        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isItemClicked)
                        SupervisorLogDetail(selectedStaffID.value, selectedStaffWeekLog.value, selectedStaffTotalHours.intValue)

                }

            }


        }

    }


}