/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_shifts.upcoming_shifts

import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.SHIFT_ACTIVE
import org.devstrike.app.medcareaidscheduler.utils.Common.TIME_FORMAT_HM
import org.devstrike.app.medcareaidscheduler.utils.convertDateTimeToMillis
import org.devstrike.app.medcareaidscheduler.utils.getCurrentDate
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.openDial
import org.devstrike.app.medcareaidscheduler.utils.toast

@Composable
fun StaffHouseDetail(house: House, upcomingShiftData: AssignedShift, onDismissed: () -> Unit = {}) {

    val context = LocalContext.current
    val openDialog = remember { mutableStateOf(false) }

    val TAG = "StaffHouseDetail"

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
//                    Text(
//                        text = house.houseName.first().toString(),
//                        fontWeight = FontWeight.Bold,
//                        style = Typography.displaySmall
//                    )
                    Text(
                        text = house.houseName.substring(0, 1).toString(),
                        fontWeight = FontWeight.Bold,
                        style = Typography.displaySmall
                    )
                }
            }

        }

        Text(
            text = house.houseName,
            style = Typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text =
                if (upcomingShiftData.assignedShiftClockInTime.isNotBlank()) {
                    "clock in: ${
                        getDate(
                            upcomingShiftData.assignedShiftClockInTime.toLong(),
                            "hh:mm a"
                        )
                    }"
                } else {
                    "clock in: N/A"

                }, Modifier.weight(0.6F), textAlign = TextAlign.Center
            )

            Box(contentAlignment = Alignment.CenterEnd) {
                ButtonComponent(buttonText = "clock in", onClick = {
                    /*
                    * for clock in to occur:
                    * a. the current time must not be more than 5 minutes before the actual time (shift time - current time !> 300,000 milliseconds)
                    * */
                    if (upcomingShiftData.assignedShiftDate.toLong() - System.currentTimeMillis() > 300000) {
                        Log.d(
                            TAG,
                            "shift time: ${
                                getDate(
                                    upcomingShiftData.assignedShiftDate.toLong(),
                                    "dd-MMM-yyyy hh:mm"
                                )
                            }"
                        )
                        Log.d(
                            TAG,
                            "current time: ${
                                convertDateTimeToMillis(getCurrentDate("dd-MMM-yyyy hh:mm"), "dd-MMM-yyyy hh:mm")
                            }"
                        )
                        Log.d(
                            TAG,
                            "time difference: ${upcomingShiftData.assignedShiftDate} - ${System.currentTimeMillis()} \n${
                                upcomingShiftData.assignedShiftDate.toLong() - convertDateTimeToMillis(getCurrentDate("dd-MMM-yyyy hh:mm"), "dd-MMM-yyyy hh:mm")
                            }"
                        )
                        openDialog.value = true
                    } else {
                        //allow clock in
                        CoroutineScope(Dispatchers.IO).launch {
                            val shiftClockIn = hashMapOf(
                                "assignedShiftClockInTime" to System.currentTimeMillis().toString(),
                                "assignedShiftStatus" to SHIFT_ACTIVE
                            )

                            Common.assignedShiftsCollectionRef.document(upcomingShiftData.assignedShiftID)
                                .update(shiftClockIn as Map<String, Any>).addOnCompleteListener {
                                if (it.isSuccessful) {
                                    context.toast("clocked in")
                                    onDismissed()
                                }else{
                                    context.toast(it.exception?.localizedMessage?.toString() ?: "some error occurred")
                                }
                            }.addOnFailureListener {
                                    context.toast(it.localizedMessage?.toString() ?: "some error occurred")
                                }
                        }
                    }
                }, enabled = upcomingShiftData.assignedShiftClockInTime.isBlank())
            }

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Text(
                text = if (upcomingShiftData.assignedShiftClockOutTime.isNotBlank()) {
                    "clock out: ${
                        getDate(
                            upcomingShiftData.assignedShiftClockOutTime.toLong(),
                            "hh:mm a"
                        )
                    }"
                } else {
                    "clock out: N/A"
                }, Modifier.weight(0.6F), textAlign = TextAlign.Center
            )
            Box(contentAlignment = Alignment.CenterEnd) {
                ButtonComponent(
                    buttonText = "clock out",
                    onClick = {
                        /*
                        * if the current time is earlier than the current shift end time, it will not agree
                        * */
                        val currentHourMinute = getDate(System.currentTimeMillis(), TIME_FORMAT_HM)
                        val currentMillisTime = convertDateTimeToMillis(currentHourMinute, TIME_FORMAT_HM)
                        val shiftEndTime = getShiftType(upcomingShiftData.assignedShiftTypeID, context)!!.shiftTypeEndTime.toLong()
                        val shiftHourMinute = getDate(shiftEndTime, TIME_FORMAT_HM)

                        Log.d(TAG, "currentHourMinute: $currentHourMinute")
                        Log.d(TAG, "currentMillisTime: $currentMillisTime")
                        Log.d(TAG, "shiftEndTime: $shiftEndTime")
                        Log.d(TAG, "shiftHourMinute: $shiftHourMinute")

                        if (currentMillisTime < shiftEndTime)
                            Log.d(TAG, "StaffHouseDetail: Too early")


                        context.toast("will clock out")
                    },
                    enabled = upcomingShiftData.assignedShiftClockInTime.isNotBlank() && upcomingShiftData.assignedShiftClockOutTime.isBlank()
                )
            }


        }

        Spacer(modifier = Modifier.height(24.dp))


        Column(
            modifier = Modifier.verticalScroll(
                rememberScrollState()
            )
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.contact_title),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.9F)
                )
                Icon(imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1F)
                        .clickable {
                            openDial(house.houseContactNumber, context)
                        })
            }

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )

            ) {


                Text(
                    text = stringResource(id = R.string.contact_address_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = house.houseAddress,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )

                Text(
                    text = stringResource(id = R.string.district_province_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )
                val houseProvince =
                    getProvince(provinceId = house.houseProvince, context = context)!!
                val houseDistrictAndProvince =
                    "${house.houseDistrict}, ${houseProvince.provinceName}"

                Text(
                    text = houseDistrictAndProvince,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )

                Text(
                    text = stringResource(id = R.string.contact_person_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = house.houseContactPerson,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )

                Text(
                    text = stringResource(id = R.string.phone_number_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = house.houseContactNumber,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(id = R.string.service_title),
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.9F)
            )

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )

            ) {


                Text(
                    text = stringResource(id = R.string.no_of_patients_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = house.houseNoOfClients,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )

                Text(
                    text = stringResource(id = R.string.necessary_info_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )
                Text(
                    text = house.houseNecessaryInformation.ifBlank { "N/A" },
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )

            }

            Spacer(modifier = Modifier.height(32.dp))

            //if the user is clocked in, then display an input field to enter info for next shift
            Box(contentAlignment = Alignment.CenterEnd) {
                ButtonComponent(
                    buttonText = stringResource(
                        id = R.string.staff_contest_shift
                    ), onClick = {
                        context.toast("Editing ${house.houseName}")
                    }, modifier = Modifier.padding(8.dp).fillMaxWidth()
                )
            }




            Spacer(modifier = Modifier.height(24.dp))


        }


    }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openDialog.value = false
            },
            title = {
                Text(text = "⚠️ Shift Inactive")
            },
            text = {
                Text(text = "Clock in can occur only 5 minutes to shift time")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        openDialog.value = false
                    }
                ) {
                    Text("Okay")
                }
            },

            )
    }
}