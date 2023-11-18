/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_weekly_logs

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import org.devstrike.app.medcareaidscheduler.data.ReportLog
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.HouseItemCard
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.ReportItemCard
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses.SupervisorHouseDetail
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorWeekLogs(navController: NavHostController) {

    val context = LocalContext.current

    val TAG = "SupervisorWeekLogs"

    // Get the list of items from Firestore
    val weeklyLogs = remember { mutableStateOf(listOf<ReportLog>()) }
    val weeklyLogsData: MutableState<ReportLog> = remember { mutableStateOf(ReportLog()) }
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }


    val isTaskRunning = remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isItemClicked = false


    LaunchedEffect(Unit) {

        val weeklyLogsList = mutableListOf<ReportLog>()
        val userDetails = getUser(auth.uid!!, context)!!

        isTaskRunning.value = true

        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.weeklyShiftsReportLogCollectionRef.whereEqualTo(
                    "reportLogOwnerProvinceID",
                    userDetails.userProvinceID
                )
                    .get().await()

            Log.d(TAG, "SupervisorWeekLogs: $querySnapshot")

            isTaskRunning.value = false

            for (document in querySnapshot) {
                val item = document.toObject(ReportLog::class.java)
                weeklyLogsList.add(item)
                Log.d(TAG, "SupervisorWeekLogs: $item")
            }
        }
        weeklyLogs.value = weeklyLogsList

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

            val listOfWeeklyLogs = weeklyLogs.value
            val filteredList = listOfWeeklyLogs.filter { reportLog ->
                val staffDetails = getUser(reportLog.reportLogOwnerID, context)!!
                staffDetails.userFirstName.contains(
                    searchQuery.value,
                    true
                ) || staffDetails.userLastName.contains(
                    searchQuery.value,
                    true
                )
            }
            items(filteredList) { report ->
                ReportItemCard(report = report, onClick = {
                    weeklyLogsData.value = report
                    isSheetOpen = true
                    isItemClicked = true
                })
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
                        SupervisorLogDetail(weeklyLogsData.value)

                }

            }


        }

    }



}