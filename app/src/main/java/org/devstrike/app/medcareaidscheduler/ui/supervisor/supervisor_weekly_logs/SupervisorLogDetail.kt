/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_weekly_logs

import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.AlertDialogComponent
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.ConcludedShift
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShift
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.toast
import org.devstrike.libs.android.timetravel.TimeTraveller

@Composable
fun SupervisorLogDetail(
    selectedStaffID: String,
    selectedStaffWeekLog: List<ConcludedShift>,
    totalHours: Int
) {

    val TAG = "SupervisorHouseDetail"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var showLogShiftDialog by rememberSaveable {
        mutableStateOf(false)
    }
    val shiftToShow: MutableState<AssignedShift> = remember { mutableStateOf(AssignedShift()) }
    val reportLogRejectionText: MutableState<String> = remember { mutableStateOf("") }

    val isTaskRunning = remember { mutableStateOf(false) }


    var approveLog by rememberSaveable {
        mutableStateOf(false)
    }
    var rejectLog by rememberSaveable {
        mutableStateOf(false)
    }
    val staffDetail = getUser(selectedStaffID, context)!!


    Surface {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isTaskRunning.value) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        LazyColumn() {
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

            }
            items(selectedStaffWeekLog){shift ->
                val shiftDate = TimeTraveller.getDate(shift.dateSubmitted.toLong(), Common.TIME_FORMAT_EDMY)
                val assignedShift = getShift(shift.assignedShiftID, context)!!
                val shiftHouse = getHouse(assignedShift.assignedHouseID, context)!!
                Column(Modifier.padding(4.dp).fillMaxWidth()) {
                    Text(text ="Date: $shiftDate", modifier = Modifier.padding(4.dp))
                    Text(text ="Client: ${shiftHouse.houseName}", modifier = Modifier.padding(4.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(text ="D: ${shift.noOfDayHours} Hours", modifier = Modifier
                            .padding(4.dp)
                            .weight(1f))
                        Text(text ="N: ${shift.noOfNightHours} Hours", modifier = Modifier
                            .padding(4.dp)
                            .weight(1f))
                        Text(text ="S/O: ${shift.noOfSleepOverHours} Hours", modifier = Modifier
                            .padding(4.dp)
                            .weight(1f))
                    }
                    Divider(Modifier.padding(2.dp))

                }

            }

            item {

                 Row(
                     modifier = Modifier.padding(4.dp),
                     verticalAlignment = Alignment.CenterVertically
                 ) {
                     ButtonComponent(
                         buttonText = stringResource(id = R.string.reject_log_text), onClick = {
                             rejectLog = true
                             //isEditClicked = true
                         }, modifier = Modifier
                             .weight(0.5F)
                             .padding(4.dp)
                     )
                     ButtonComponent(
                         buttonText = stringResource(id = R.string.approve_log_text), onClick = {
                             approveLog = true
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

        }
        if (showLogShiftDialog) {
            LogShiftDialog(
                logShift = shiftToShow.value,
                context = context
            ) {
                showLogShiftDialog = false
            }
        }

        if (approveLog) {
            AlertDialogComponent(
                title = "Approve Log Report",
                message = "This will approve payment for ${staffDetail.userFirstName}, ${staffDetail.userLastName}.\nContinue?",
                onConfirm = {
                            context.toast("Flow will be implemented when approved")
                   /* CoroutineScope(Dispatchers.IO).launch {
                        isTaskRunning.value = true
                        Common.weeklyShiftsReportLogCollectionRef.document(report.reportLogID)
                            .update("reportLogStatus", LOG_REPORT_APPROVED_STATUS)
                            .addOnCompleteListener {
                                //write new payment data

                                val paymentDataID = UUID.randomUUID().toString()
                                val paymentData = Payment(
                                    paymentID = paymentDataID,
                                    paymentOwnerID = staffDetail.userID,
                                    paymentAmount = report.reportLogTotalAmountToPay,
                                    paymentNoOfHoursPaid = totalNoOfHoursToPay.toString(),
                                    paymentStatus = LOG_REPORT_APPROVED_STATUS,
                                    paymentApprovingSupervisorID = auth.uid!!,
                                    paymentDateApproved = System.currentTimeMillis().toString(),
                                )
                                paymentsCollectionRef.document(paymentDataID).set(paymentData)
                                    .addOnCompleteListener {
                                        val notificationDataID = UUID.randomUUID().toString()
                                        //create notification to send
                                        val approvalNotification = Notification(
                                            notificationID = notificationDataID,
                                            notificationType = NOTIFICATION_TYPE_PAYMENT_APPROVAL,
                                            notificationSenderID = auth.uid!!,
                                            notificationReceiverID = staffDetail.userID,
                                            notificationTitle = "Payment Approved",
                                            notificationMessage = "Your week log report has been reviewed and your payment has been approved.",
                                            notificationSentDate = System.currentTimeMillis()
                                                .toString(),
                                            notificationProvinceID = getUser(
                                                auth.uid!!,
                                                context
                                            )!!.userProvinceID
                                        )
                                        notificationsCollectionRef.document(notificationDataID)
                                            .set(approvalNotification).addOnCompleteListener {
                                            approveLog = false
                                            isTaskRunning.value = false
                                            context.toast("Week Log Report Approved")
                                        }.addOnFailureListener { e ->
                                            isTaskRunning.value = false
                                            context.toast(
                                                e.localizedMessage?.toString()
                                                    ?: "Some error occurred"
                                            )
                                        }
                                    }.addOnFailureListener { e ->
                                    isTaskRunning.value = false
                                    context.toast(
                                        e.localizedMessage?.toString() ?: "Some error occurred"
                                    )
                                }
                            }.addOnFailureListener { e ->
                                isTaskRunning.value = false
                                context.toast(
                                    e.localizedMessage?.toString() ?: "Some error occurred"
                                )
                            }
                    }*/
                },//change status of paylog to approve and write a new table to db for payment,
                onCancel = {
                    approveLog = false
                }
            )
        }
        if (rejectLog) {
            Dialog(onDismissRequest = { rejectLog = false }) {
                Card(modifier = Modifier.padding(8.dp)) {
                    Text(
                        text = stringResource(id = R.string.reject_shift_report_title),
                        style = Typography.titleMedium,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                    )

                    TextFieldComponent(
                        value = reportLogRejectionText.value,
                        onValueChange = { reportLogRejectionText.value = it },
                        label = "Rejection Reason",
                        keyboardOptions = KeyboardOptions.Default.copy(
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.None
                        ),
                        inputType = "Log Report Rejection",
                    )

                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        ButtonComponent(
                            buttonText = stringResource(id = R.string.submit_text),
                            onClick = {
                                      context.toast("Flow will be implemented when approved")
                                /*CoroutineScope(Dispatchers.IO).launch {
                                    isTaskRunning.value = true
                                    Common.weeklyShiftsReportLogCollectionRef.document(report.reportLogID)
                                        .update("reportLogStatus", LOG_REPORT_REJECTED_STATUS)
                                        .addOnCompleteListener {
                                            val notificationDataID = UUID.randomUUID().toString()
                                            //create notification to send
                                            val approvalNotification = Notification(
                                                notificationID = notificationDataID,
                                                notificationType = NOTIFICATION_TYPE_LOG_REPORT_REJECTION,
                                                notificationSenderID = auth.uid!!,
                                                notificationReceiverID = staffDetail.userID,
                                                notificationTitle = "Log Report Rejected",
                                                notificationMessage = "Your week log report has been reviewed and rejected.\n\nReason: ${reportLogRejectionText.value}",
                                                notificationSentDate = System.currentTimeMillis()
                                                    .toString(),
                                                notificationProvinceID = getUser(
                                                    auth.uid!!,
                                                    context
                                                )!!.userProvinceID
                                            )
                                            notificationsCollectionRef.document(notificationDataID)
                                                .set(approvalNotification).addOnCompleteListener {
                                                approveLog = false
                                                isTaskRunning.value = false
                                                context.toast("Week Log Report Rejected")
                                            }.addOnFailureListener { e ->
                                                isTaskRunning.value = false
                                                context.toast(
                                                    e.localizedMessage?.toString()
                                                        ?: "Some error occurred"
                                                )
                                            }
                                        }.addOnFailureListener { e ->
                                            isTaskRunning.value = false
                                            context.toast(
                                                e.localizedMessage?.toString()
                                                    ?: "Some error occurred"
                                            )
                                        }
                                }*/
                            },  modifier = Modifier.fillMaxWidth())
                }
            }

        }

    }
}

}

@Composable
fun LogShiftDialog(
    logShift: AssignedShift,
    context: Context,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        val houseDetails = getHouse(logShift.assignedHouseID, context)!!
//        val shiftTypeDetails = getShiftType(logShift.assignedShiftTypeID, context)!!
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
//            Text(text = shiftTypeDetails.shiftTypeName, Modifier.padding(4.dp))
            /*Text(
                text = "Hours: ${shiftTypeDetails.shiftTypeNoOfHours.toDouble().toInt()}",
                Modifier.padding(4.dp)
            )*/

            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                ButtonComponent(buttonText = stringResource(id = R.string.okay_button), onClick = {
                    onDismiss()
                }, modifier = Modifier.fillMaxWidth())
            }
        }

    }


}