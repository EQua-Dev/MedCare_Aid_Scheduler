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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.ConcludedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.SHIFT_ACTIVE
import org.devstrike.app.medcareaidscheduler.utils.Common.TIME_FORMAT_HM
import org.devstrike.app.medcareaidscheduler.utils.Common.shiftCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.openDial
import org.devstrike.app.medcareaidscheduler.utils.toast
import org.devstrike.libs.android.timetravel.TimeTraveller
import java.util.UUID

@Composable
fun StaffHouseDetail(house: House, upcomingShiftData: AssignedShift, onDismissed: () -> Unit = {}) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val openDialog = remember { mutableStateOf(false) }
    val showClockOutDialog = remember { mutableStateOf(false) }
    val currentHourMinute = remember { mutableStateOf("") }
    val provinceShiftTypeDetails = remember { mutableStateOf(ShiftType()) }
    val provinceShiftTypeDetailsList = remember { mutableListOf(ShiftType()) }
    val provinceDayStartTime = remember { mutableStateOf("") }
    val provinceDayStartTimeStringLong = remember { mutableStateOf("") }
    val provinceDayStopTime = remember { mutableStateOf("") }
    val provinceDayStopTimeStringLong = remember { mutableStateOf("") }
    val provinceNightStartTime = remember { mutableStateOf("") }
    val provinceNightStartTimeStringLong = remember { mutableStateOf("") }
    val provinceNightStopTime = remember { mutableStateOf("") }
    val provinceNightStopTimeStringLong = remember { mutableStateOf("") }
    val provinceSleepOverStartTime = remember { mutableStateOf("") }
    val provinceSleepOverStartTimeStringLong = remember { mutableStateOf("") }
    val provinceSleepOverStopTime = remember { mutableStateOf("") }
    val provinceSleepOverStopTimeStringLong = remember { mutableStateOf("") }

    val totalDayHoursWorked = remember {
        mutableStateOf("0")
    }
    val totalNightHoursWorked = remember {
        mutableStateOf("0")
    }
    val totalSleepOverHoursWorked = remember {
        mutableStateOf("0")
    }

    var assignmentClockOutNecessaryInfo by remember {
        mutableStateOf("")
    }

    val isTaskRunning = remember { mutableStateOf(false) }

    val staffInfo = getUser(upcomingShiftData.assignedStaffID, context)!!

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
                    if (upcomingShiftData.assignedShiftStartTime.toLong() - System.currentTimeMillis() > 300000) {
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
                                    } else {
                                        context.toast(
                                            it.exception?.localizedMessage?.toString()
                                                ?: "some error occurred"
                                        )
                                    }
                                }.addOnFailureListener {
                                    context.toast(
                                        it.localizedMessage?.toString() ?: "some error occurred"
                                    )
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
                        currentHourMinute.value =
                            TimeTraveller.getDate(System.currentTimeMillis(), TIME_FORMAT_HM)
                        val currentMillisTime = TimeTraveller.convertDateTimeToMillis(
                            currentHourMinute.value,
                            TIME_FORMAT_HM
                        )
                        val shiftEndTime = upcomingShiftData.assignedShiftStopTime.toLong()

                        Log.d(TAG, "currentHourMinute: $currentHourMinute")
                        Log.d(TAG, "currentMillisTime: $currentMillisTime")
                        Log.d(TAG, "shiftEndTime: $shiftEndTime")

                        if (System.currentTimeMillis() < shiftEndTime) {
                            context.toast("wait till the end of the shift")
                            Log.d(TAG, "StaffHouseDetail: Too early")
                        } else {
                            //display dialog to enter necessary information
                            showClockOutDialog.value = true
                        }
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
                    }, modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
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

    if (showClockOutDialog.value) {
        LaunchedEffect(Unit) {
            coroutineScope.launch {
                shiftCollectionRef.whereEqualTo(
                    "shiftTypeOwnerProvinceID",
                    staffInfo.userProvinceID
                ).get().addOnCompleteListener {
                    for (doc in it.result.documents) {
                        val shiftTypeItem = doc.toObject(ShiftType::class.java)!!
                        provinceShiftTypeDetailsList.add(shiftTypeItem)
                    }
                }
            }
        }
        Dialog(onDismissRequest = { showClockOutDialog.value = false }) {
            Card(
                //shape = MaterialTheme.shapes.medium,
                shape = RoundedCornerShape(10.dp),
                // modifier = modifier.size(280.dp, 240.dp)
                modifier = Modifier.padding(8.dp),
            ) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    if (isTaskRunning.value) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }

                    LazyColumn {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(Color.Transparent) //will later be converted to background color

                            ) {

                                Text(
                                    text = stringResource(id = R.string.staff_clockout_dialog_title),
                                    modifier = Modifier.padding(8.dp),
                                    fontSize = 20.sp
                                )
                                val shiftDate = TimeTraveller.getDate(
                                    upcomingShiftData.assignedShiftDate.toLong(),
                                    "EEE, dd MMM, yyyy"
                                )
                                val clockInTime = TimeTraveller.convertMillisToHourAndMinute(
                                    upcomingShiftData.assignedShiftClockInTime.toLong()
                                )
                                val totalHours = TimeTraveller.calculateHoursBetweenTimes(
                                    lesserTime = upcomingShiftData.assignedShiftClockInTime,
                                    greaterTime = upcomingShiftData.assignedShiftClockOutTime
                                ) ?: "0"

                                for (shiftType in provinceShiftTypeDetailsList) {
                                    /* LOGIC TO DETERMINE THE NUMBER OF HOURS IN EACH SHIFT TYPE THE ASSIGNED SHIFT CONTAINS
                                    * 1. Get the actual assigned shift start and stop time
                                    * 2. Get the start and stop time of each of the shift types (D, N and S/O)
                                    * 3. Iterate over all the types in the shift type for the staff's province
                                    * 4. Within each shift type:
                                    *    a. Check if the assigned shift stop time is before the stop time of the shift type;
                                    *        then
                                    *        i. if the assigned start time is before the shift type start time, count how many hours are between assigned stop time and shift type start time
                                    *        else,
                                    *        ii. count the number of hours between the assigned stop time and the assigned start time
                                    *    b. Check if the the assigned stop time is after the shift type stop time;
                                    *        then
                                    *        i. if the assigned start time is after the shift type start, count how many hours are between the shift type stop time and the assigned start time
                                    *        else
                                    *        ii. count the number of hours between the assigned stop time and the assigned start time.
                                    *
                                    * CODE LOGIC
                                    *
                                    * ASSP => Assigned Stop TIme
                                    * DSSP => Shift Type Stop TIme
                                    * ASST => Assigned Start Time
                                    * DSST => Shift Type Start Time
                                    *
                                    * if (ASSP < DSSP){
                                    *    if(ASST < DSST) {
                                    *        hours = ASSP - DSST
                                    *        }
                                    *    else {
                                    *        hours = ASSP - ASST
                                    *        }
                                    *   }
                                    * else if(ASSP > DSSP){
                                    *    if(ASST > DSST){
                                    *        hours = DSSP - ASST
                                    *        }
                                    *    else{
                                    *        hours = DSSP - DSST
                                    *        }
                                    *   }
                                    * */
                                    when (shiftType.shiftTypeAliasName) {
                                        "D" -> {
                                            provinceDayStartTimeStringLong.value =
                                                shiftType.shiftTypeStartTime
                                            provinceDayStartTime.value =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    provinceDayStartTimeStringLong.value.toLong()
                                                )
                                            provinceDayStopTimeStringLong.value =
                                                shiftType.shiftTypeEndTime
                                            provinceDayStopTime.value =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    provinceDayStopTimeStringLong.value.toLong()
                                                )

                                            val assignedShiftStartTimeLong =
                                                upcomingShiftData.assignedShiftStartTime
                                            val assignedShiftStartTime =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    assignedShiftStartTimeLong.toLong()
                                                )
                                            val assignedShiftStopTimeLong =
                                                upcomingShiftData.assignedShiftStopTime
                                            val assignedShiftStopTime =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    assignedShiftStopTimeLong.toLong()
                                                )

                                            var hoursWorked = 0.0
                                            if (assignedShiftStopTimeLong.toLong() < provinceDayStopTimeStringLong.value.toLong()) {
                                                hoursWorked =
                                                    if (assignedShiftStartTimeLong.toLong() < provinceDayStartTimeStringLong.value.toLong()) {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            lesserTime = provinceDayStartTime.value,
                                                            greaterTime = assignedShiftStopTime
                                                        )!!

                                                    } else {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            lesserTime = assignedShiftStartTime,
                                                            greaterTime = assignedShiftStopTime
                                                        )!!

                                                    }
                                            } else if (assignedShiftStopTimeLong.toLong() > provinceDayStopTimeStringLong.value.toLong()) {
                                                hoursWorked =
                                                    if (assignedShiftStartTimeLong.toLong() > provinceDayStopTimeStringLong.value.toLong()) {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            assignedShiftStartTime,
                                                            provinceDayStopTime.value
                                                        )!!

                                                    } else {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            lesserTime = provinceDayStartTime.value,
                                                            greaterTime = provinceDayStopTime.value
                                                        )!!
                                                    }
                                            }
                                            totalDayHoursWorked.value =
                                                hoursWorked.toInt().toString()
                                        }

                                        "N" -> {

                                            provinceNightStartTimeStringLong.value =
                                                shiftType.shiftTypeStartTime
                                            provinceNightStartTime.value =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    provinceNightStartTimeStringLong.value.toLong()
                                                )
                                            provinceNightStopTimeStringLong.value =
                                                shiftType.shiftTypeEndTime
                                            provinceNightStopTime.value =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    provinceNightStopTimeStringLong.value.toLong()
                                                )

                                            val assignedShiftStartTimeLong =
                                                upcomingShiftData.assignedShiftStartTime
                                            val assignedShiftStartTime =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    assignedShiftStartTimeLong.toLong()
                                                )
                                            val assignedShiftStopTimeLong =
                                                upcomingShiftData.assignedShiftStopTime
                                            val assignedShiftStopTime =
                                                TimeTraveller.convertMillisToHourAndMinute(
                                                    assignedShiftStopTimeLong.toLong()
                                                )

                                            var hoursWorked = 0.0
                                            if (assignedShiftStopTimeLong.toLong() < provinceNightStopTimeStringLong.value.toLong()) {
                                                hoursWorked =
                                                    if (assignedShiftStartTimeLong.toLong() < provinceDayStartTimeStringLong.value.toLong()) {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            lesserTime = provinceDayStartTime.value,
                                                            greaterTime = assignedShiftStopTime
                                                        )!!

                                                    } else {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            lesserTime = assignedShiftStartTime,
                                                            greaterTime = assignedShiftStopTime
                                                        )!!

                                                    }
                                            } else if (assignedShiftStopTimeLong.toLong() > provinceNightStopTimeStringLong.value.toLong()) {
                                                hoursWorked =
                                                    if (assignedShiftStartTimeLong.toLong() > provinceNightStopTimeStringLong.value.toLong()) {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            assignedShiftStartTime,
                                                            provinceNightStopTime.value
                                                        )!!

                                                    } else {
                                                        TimeTraveller.calculateHoursBetweenTimes(
                                                            lesserTime = provinceNightStartTime.value,
                                                            greaterTime = provinceNightStopTime.value
                                                        )!!
                                                    }
                                            }
                                            totalNightHoursWorked.value =
                                                hoursWorked.toInt().toString()

                                        }

                                        "S/O" -> {
                                            if (upcomingShiftData.assignedShiftStartTime.toLong() > shiftType.shiftTypeStartTime.toLong()
                                                || upcomingShiftData.assignedShiftStopTime.toLong() > shiftType.shiftTypeStartTime.toLong()
                                            )
                                                totalSleepOverHoursWorked.value = "8"
                                        }
                                    }
                                }



                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = house.houseName, modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.6f)
                                    )
                                    Text(
                                        text = shiftDate, modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f), style = Typography
                                            .labelMedium, fontStyle = FontStyle.Italic
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.clock_in_time_title),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f)
                                    )
                                    Text(
                                        text = clockInTime, modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.6f)
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.clock_out_time_title),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f),
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = currentHourMinute.value, modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.6f), fontWeight = FontWeight.Bold
                                    )
                                }

                                Divider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp)
                                )
                                Text(text = stringResource(id = R.string.hours_text))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.total_hours_title),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f),
                                        fontWeight = FontWeight.Bold, textAlign = TextAlign.End
                                    )
                                    Text(
                                        text = "$totalHours Hours",
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.6f),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Start
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.day_title),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f), textAlign = TextAlign.End
                                    )
                                    Text(
                                        text = "${totalDayHoursWorked.value} Hours",
                                        modifier = Modifier
                                            .padding(4.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.night_title),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f), textAlign = TextAlign.End
                                    )
                                    Text(
                                        text = "${totalNightHoursWorked.value} Hours",
                                        modifier = Modifier
                                            .padding(4.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(id = R.string.sleep_over_title),
                                        modifier = Modifier
                                            .padding(4.dp)
                                            .weight(0.4f), textAlign = TextAlign.End
                                    )
                                    Text(
                                        text = "${totalSleepOverHoursWorked.value} Hours",
                                        modifier = Modifier
                                            .padding(4.dp),
                                        textAlign = TextAlign.Start
                                    )
                                }

                                //any necessary information
                                val heightTextFields by remember { mutableStateOf(128.dp) }

                                OutlinedTextField(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(heightTextFields.times(2))
                                        .padding(4.dp),
                                    placeholder = { Text(text = stringResource(id = R.string.necessary_info_title)) },
                                    value = assignmentClockOutNecessaryInfo,
                                    onValueChange = {
                                        assignmentClockOutNecessaryInfo = it
                                    },
                                    textStyle = TextStyle(
                                        color = Color.Black,
                                        fontSize = 16.sp
                                    ),
                                )


                                ButtonComponent(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    buttonText = stringResource(id = R.string.confirm_clock_out_btn),
                                    onClick = {
                                        isTaskRunning.value = false
                                        coroutineScope.launch {
                                            val concludedShiftID = UUID.randomUUID().toString()
                                            val concludedShift = ConcludedShift(
                                                concludedShiftID = concludedShiftID,
                                                assignedShiftID = upcomingShiftData.assignedShiftID,
                                                shiftStaffID = upcomingShiftData.assignedStaffID,
                                                shiftProvinceID = staffInfo.userProvinceID,
                                                noOfDayHours = totalDayHoursWorked.value,
                                                noOfNightHours = totalNightHoursWorked.value,
                                                noOfSleepOverHours = totalSleepOverHoursWorked.value,
                                                noOfTotalHours = totalHours.toString(),
                                                dateSubmitted = System.currentTimeMillis()
                                                    .toString(),
                                            )

                                            Common.concludedShiftsCollectionRef.document(
                                                concludedShiftID
                                            )
                                                .set(concludedShift).addOnCompleteListener {
                                                    if (it.isSuccessful) {
                                                        val updates = hashMapOf<String, Any>(
                                                            "assignedShiftClockOutTime" to System.currentTimeMillis()
                                                                .toString(),
                                                            "assignedShiftStatus" to Common.SHIFT_INACTIVE
                                                        )
                                                        Common.assignedShiftsCollectionRef.document(
                                                            upcomingShiftData.assignedShiftID
                                                        )
                                                            .update(updates)
                                                            .addOnCompleteListener { task ->

                                                                if (task.isSuccessful) {
                                                                    context.toast("Clock Out Success!")
                                                                    showClockOutDialog.value = false
                                                                } else {
                                                                    isTaskRunning.value = false
                                                                    context.toast(
                                                                        task.exception?.localizedMessage
                                                                            ?: "Some error occurred"
                                                                    )
                                                                }
                                                            }.addOnFailureListener { e ->
                                                                isTaskRunning.value = false
                                                                context.toast(
                                                                    e.localizedMessage
                                                                        ?: "Some error occurred"
                                                                )
                                                            }
                                                    } else {
                                                        isTaskRunning.value = false
                                                        context.toast(
                                                            it.exception?.localizedMessage
                                                                ?: "Some error occurred"
                                                        )
                                                    }
                                                }.addOnFailureListener { e ->
                                                    isTaskRunning.value = false
                                                    context.toast(
                                                        e.localizedMessage ?: "Some error occurred"
                                                    )

                                                }


                                        }
                                    })
                            }
                        }
                    }

                }
            }
        }
    }
}