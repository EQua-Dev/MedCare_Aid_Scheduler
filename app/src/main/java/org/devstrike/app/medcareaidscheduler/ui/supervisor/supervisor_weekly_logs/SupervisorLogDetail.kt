/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_weekly_logs

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.ReportLog
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.ReportItemCard
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth
import org.devstrike.app.medcareaidscheduler.utils.toast

@Composable
fun SupervisorLogDetail(report: ReportLog) {

    val TAG = "SupervisorHouseDetail"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showLogShiftDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val shiftToShow: MutableState<AssignedShift> = remember { mutableStateOf(AssignedShift()) }

    /*
        val houseAssignedShifts = remember {
            mutableStateOf(listOf<AssignedShift>())

        }*/


    /* LaunchedEffect(key1 = Unit) {
         val houseAssignedShiftsList = mutableListOf<AssignedShift>()

         coroutineScope.launch {
             val querySnapshot =
                 Common.assignedShiftsCollectionRef
                     .whereEqualTo("assignedHouseID", report.houseID)
 //                                            .whereEqualTo(
 //                                                "userProvinceID", supervisor.userProvinceID
 //                                            )
                     .get()
                     .await()

             for (document in querySnapshot) {
                 val item = document.toObject(AssignedShift::class.java)
                 Log.d(
                     TAG,
                     "SupervisorStaffDetail Is current week?: ${
                         isTimeInCurrentMonth(item.assignedShiftDate.toLong())
                     }"
                 )

                 if (isTimeInCurrentMonth(item.assignedShiftDate.toLong())) {
                     //time is in current week
                     houseAssignedShiftsList.add(item)
                     houseAssignedShifts.value = houseAssignedShiftsList
                     Log.d(TAG, "SupervisorStaffDetail Item: $item")
                     Log.d(
                         TAG,
                         "SupervisorStaffDetail Is current week?: ${
                             isTimeInCurrentMonth(item.assignedShiftDate.toLong())
                         }"
                     )
                 }
             }

             //staff.value = staffList
         }

     }*/




    LazyColumn() {
        val staffDetail = getUser(report.reportLogOwnerID, context)!!
        item {
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
                            text = staffDetail.userFirstName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                        Text(
                            text = staffDetail.userLastName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                    }
                }

            }

            Text(
                text = "${staffDetail.userFirstName}, ${staffDetail.userLastName}",
                style = Typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
            val totalHours = report.reportLogTotalPayableDaysHours.toInt()
                .plus(report.reportLogTotalPayableNightsHours.toInt()).plus(
                    report
                        .reportLogTotalPayableSleepOversHours.toInt()
                )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Hours Worked:",
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "$totalHours Hours",
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Text(
                text = "D: ${report.reportLogTotalPayableDaysHours} N: ${report.reportLogTotalPayableNightsHours} S/O: ${report.reportLogTotalPayableSleepOversHours}",
                Modifier
                    .padding(4.dp)
                    .fillMaxWidth(), textAlign = TextAlign.Center
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Total Amount to Pay:",
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = "€${report.reportLogTotalAmountToPay}",
                    style = Typography.bodyMedium,
                    modifier = Modifier.padding(4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonComponent(
                    buttonText = stringResource(id = R.string.reject_log_text), onClick = {
                        context.toast("will reject log")
                        //isEditClicked = true
                    }, modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
                ButtonComponent(
                    buttonText = stringResource(id = R.string.approve_log_text), onClick = {
                        context.toast("will approve log")
                    }, modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
            }
            //total hours
            //total amount to pay
            //approve or reject pay buttons
            Divider(Modifier.padding(8.dp))

        }

        items(report.reportLogDailyShiftDetails.sortedBy { assignedShift -> assignedShift.assignedShiftDate }) { shift ->
            ReportItemCard(shift, onClick = {
                showLogShiftDialog = true
                shiftToShow.value = shift
            })


        }

    }
    if (showLogShiftDialog) {
        LogShiftDialog(
            logShift = shiftToShow.value,
            staffReportLog = report,
            context = context,
            onDismiss = {
                showLogShiftDialog = false
            })
    }
}

@Composable
fun LogShiftDialog(
    logShift: AssignedShift,
    staffReportLog: ReportLog,
    context: Context,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        val houseDetails = getHouse(logShift.assignedHouseID, context)!!
        val shiftTypeDetails = getShiftType(logShift.assignedShiftTypeID, context)!!
        Card(modifier = Modifier.padding(8.dp)) {
            Text(
                text = stringResource(id = R.string.log_shift_report),
                style = Typography.titleMedium,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            )
            Text(
                text = getDate(logShift.assignedShiftDate.toLong(), Common.TIME_FORMAT_EDMY),
                Modifier.padding(4.dp)
            )
            Text(text = houseDetails.houseName, Modifier.padding(4.dp))
            Text(text = shiftTypeDetails.shiftTypeName, Modifier.padding(4.dp))
            Text(
                text = "Hours: ${shiftTypeDetails.shiftTypeNoOfHours.toDouble().toInt()}",
                Modifier.padding(4.dp)
            )

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ButtonComponent(buttonText = stringResource(id = R.string.okay_button), onClick = {
                    onDismiss()
                })
            }
        }

    }


}