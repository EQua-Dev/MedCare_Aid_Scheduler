/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_shifts.concluded_shifts

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.components.custom_slider.CustomSlider
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.ReportLog
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_components.UpcomingShiftItemCard
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_shifts.upcoming_shifts.StaffHouseDetail
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.SHIFT_HOURLY_PAY
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isDateWithinThisWeek
import org.devstrike.app.medcareaidscheduler.utils.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffConcludedShifts() {

    val TAG = "StaffUpcomingShift"

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }
    val staffShiftsServed: MutableState<Int> = remember { mutableIntStateOf(0) }
    val staffWeekHours: MutableState<Int> = remember { mutableIntStateOf(0) }
    val staffWeekPayableAmount: MutableState<Double> = remember { mutableDoubleStateOf(0.0) }
    val staffWeekShiftTypeHours: MutableMap<String, Int> = remember {
        mutableMapOf()
    }

    val coroutineScope = rememberCoroutineScope()


    val listOfHoursPayable = mutableListOf<String>()
    for (item in staffWeekShiftTypeHours.keys) {
        listOfHoursPayable.add("$item:${staffWeekShiftTypeHours[item]},")
    }

    // Get the list of items from Firestore
//    val upcomingShiftList = remember {
//        mutableListOf<AssignedShift>()
//    }
    val concludedShiftList = remember { mutableStateOf(listOf<AssignedShift>()) }
    val concludedWeekShiftList = remember { mutableListOf<AssignedShift>() }

    var concludedShiftData = AssignedShift()
    val activeShiftData: MutableState<AssignedShift> = remember {
        mutableStateOf(AssignedShift())
    }
    var isItemClicked = false

    val staffInfo = getUser(Common.auth.uid!!, context)!!




    LaunchedEffect(Unit) {
        val concludedShiftsList = mutableListOf<AssignedShift>()

        withContext(Dispatchers.IO) {

            val querySnapshot =
                Common.assignedShiftsCollectionRef.whereEqualTo(
                    "assignedStaffID",
                    Common.auth.uid!!
                )


            querySnapshot.addSnapshotListener(MetadataChanges.INCLUDE) { snapshot, e ->
                if (e != null) {
                    Log.d(TAG, "Listen Failed: ", e)
                    return@addSnapshotListener
                }
                concludedShiftsList.clear()
                for (document in snapshot!!) {
                    val item = document.toObject(AssignedShift::class.java)
                    // check if the time of the shift is in the past and the shift is inactive (to remove the ongoing shift)
                    if (item.assignedShiftDate.toLong() < System.currentTimeMillis() && item.assignedShiftStatus != Common.SHIFT_ACTIVE)
                        concludedShiftsList.add(item)
                }
                var entriesLoopCount = 0
                concludedShiftList.value = concludedShiftsList
                for (shift in concludedShiftList.value) {
                    if (isDateWithinThisWeek(shift.assignedShiftDate.toLong()) && shift.assignedShiftClockOutTime.isNotBlank()) {
                        concludedWeekShiftList.add(shift)
                        staffShiftsServed.value++
                        val staffShiftType = getShiftType(
                            shift.assignedShiftTypeID,
                            context
                        )!!
                        staffWeekHours.value = staffWeekHours.value.toInt().plus(
                            staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                        )
//                            staffWeekShiftTypeHours[staffShiftType.shiftTypeName] =
//                                staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                        /*
                        **the key is being replaced instead of adding the hours to the value of the keys**
                         * todo: 1. loop through the keys of the map
                         * todo: 2. check if the key already exists
                         * todo: 3. if exists, then add the hours to the value of that key
                         * todo: 4. else write to the new key
                         *
                         * */
                        if (staffWeekShiftTypeHours.entries.isEmpty()) {
                            staffWeekShiftTypeHours[staffShiftType.shiftTypeName] =
                                staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                            Log.d(
                                TAG,
                                "StaffConcludedShifts: ${staffWeekShiftTypeHours.entries} is empty adding ${staffShiftType.shiftTypeName}"
                            )
                        } else {
                            Log.d(
                                TAG,
                                "StaffConcludedShifts: ${staffWeekShiftTypeHours.entries} is  not empty"
                            )
                            for (shiftHours in staffWeekShiftTypeHours.entries) {
                                Log.d(TAG, "shiftHoursCount: ${entriesLoopCount++}")
                                Log.d(TAG, "shiftHours: $shiftHours")
                                Log.d(TAG, "shiftHoursEntries: ${staffWeekShiftTypeHours.entries}")
//                            for (shiftName in shiftHours.key){
                                if (shiftHours.key == staffShiftType.shiftTypeName) {
                                    Log.d(
                                        TAG,
                                        "StaffConcludedShifts: ${shiftHours.key} is  equal to ${staffShiftType.shiftTypeName} adding ${shiftHours.value} to ${
                                            staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                                        } which is equal to ${
                                            shiftHours.value.plus(
                                                staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                                            )
                                        }"
                                    )
                                    staffWeekShiftTypeHours[shiftHours.key] = shiftHours.value.plus(
                                        staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                                    )
                                    break
                                } else {
                                    Log.d(
                                        TAG,
                                        "StaffConcludedShifts: ${shiftHours.key} is  not equal to ${staffShiftType.shiftTypeName}, writing ${shiftHours.value} to $staffWeekShiftTypeHours"
                                    )
                                    staffWeekShiftTypeHours[staffShiftType.shiftTypeName] =
                                        staffShiftType.shiftTypeNoOfHours.toDouble().toInt()
                                }
//                            }
                            }
                        }
                        Log.d(TAG, "staffShiftsServedEntries: ${staffWeekShiftTypeHours.entries}")
                    }
                }

            }

        }

    }


    Surface(modifier = Modifier.fillMaxSize()) {

        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        var isEditClicked by rememberSaveable {
            mutableStateOf(false)
        }

        Column(modifier = Modifier.padding(4.dp)) {

            Text(
                text = "End of Week Log",
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(8.dp)
                    .offset(x = 16.dp)
            )
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.staff_log_shifts_served),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )

                /*
                ~ check the list of assigned shifts for the ones with the staff ID which is "concluded shift list .value"
                ~ check the shifts that are within this week
                ~ then check for the shifts that have clock out time || check the ones that have a status of 'served'
                */

                Text(
                    text = staffShiftsServed.value.toString(),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.staff_log_hours),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )

                /*
                ~ check the list of assigned shifts for the ones with the staff ID
                ~ check the shifts that are within this week
                ~ check for the shifts that have clock out time || check the ones that have a status of 'served'
                ~ then for each of the shifts, get the shift type ID and get the number of hours in those shift types
                ~ then have a variable that will be increasing its value by adding every shift hour found
                */


                Text(
                    text = staffWeekHours.value.toString(),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.staff_log_payable_hours),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )

                /*
                ~ check the list of assigned shifts for the ones with the staff ID
                ~ check the shifts that are within this week
                ~ check for the shifts that have clock out time || check the ones that have a status of 'served'
                ~ have three variables for each of the shift types {for each distinct shift type, there will be a map containing the type name and its hours total value}
                ~ then for each of the items in the map, populate its alias name and its hours total
                */

                val hoursPayable =
                    staffWeekShiftTypeHours.entries.joinToString(", ") { (key, value) -> "$key: $value" }

                Text(
                    text =
                    hoursPayable,
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
            }
            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.staff_log_payable_amount),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )

                /*
                ~ check the map of hours payable
                ~ get all the values and add them, then multiply the cost of 1 shift by the sum

                */

                val totalHours = staffWeekShiftTypeHours.values.sum()
                Log.d(TAG, "totalHours: $totalHours")

                staffWeekPayableAmount.value =
                    totalHours.toDouble().times(SHIFT_HOURLY_PAY.toDouble())

                Text(
                    text = stringResource(
                        id = R.string.euro_currency,
                        staffWeekPayableAmount.value.toString()
                    ),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
            }

            Row(
                modifier = Modifier.padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ButtonComponent(
                    buttonText = stringResource(id = R.string.edit_text), onClick = {
                        context.toast("will edit log")
                        isEditClicked = true
                    }, modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
                ButtonComponent(
                    buttonText = stringResource(id = R.string.submit_text), onClick = {
                        context.toast("will submit log")
                    }, modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp)
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Divider(Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(12.dp))
            //search bar
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
            Text(
                text = "Shifts",
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(8.dp)
                    .offset(x = 16.dp)
            )

            //list of cards

            LazyColumn {
                val listOfUpcomingShifts = concludedShiftList.value
                Log.d(TAG, "listOfUpcomingShifts: $listOfUpcomingShifts")

                val filteredList = listOfUpcomingShifts.filter { assignedShift ->
                    val houseInfo = getHouse(assignedShift.assignedHouseID, context)!!
                    val shiftTypeInfo =
                        getShiftType(assignedShift.assignedShiftTypeID, context)!!
//                    val supervisorInfo = getUser(houseInfo.houseName, context)!!
                    houseInfo.houseName.contains(
                        searchQuery.value,
                        true
                    ) || houseInfo.houseAddress.contains(
                        searchQuery.value,
                        true
                    ) || getDate(
                        assignedShift.assignedShiftDate.toLong(),
                        "EEE, dd MMM, yyyy"
                    ).contains(searchQuery.value, true)
                            || shiftTypeInfo.shiftTypeName.contains(searchQuery.value, true)
                }

                Log.d(TAG, "filteredList: $filteredList")
                items(filteredList) { assignedShift ->
                    UpcomingShiftItemCard(assignedShift = assignedShift, onClick = {
                        concludedShiftData = assignedShift
                        isSheetOpen = true
                        isItemClicked = true
                    }, onContestClick = {

                    })
                }
//                    listOfHouses.forEach { house ->
//                        Log.d(TAG, "SupervisorHouses List: $house")
////                        HouseItemCard(house = house)
//                        HouseItemCard(house)
//                    }
            }
//                val houses =
//                    housesCollectionRef.whereEqualTo("houseAddingSupervisor", auth.uid!!).get()
            // Iterate over the list of items and display each item using the ItemComposable composable function


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
                    val houseInfo = getHouse(concludedShiftData.assignedHouseID, context)!!
                    if (isItemClicked)
                        StaffHouseDetail(
                            houseInfo, concludedShiftData, onDismissed = {
                                isItemClicked = false
                                isSheetOpen = false
                            }
                        )

                }

            }


        }
        if (isEditClicked) {

            EditWeekLogDialog(
                onDismiss = { isEditClicked = false },
                coroutineScope = coroutineScope,
                staffWeekShiftTypeHours = staffWeekShiftTypeHours,
                context = context
            )
        }

    }

}

@Composable
fun EditWeekLogDialog(
    onDismiss: () -> Unit,
    coroutineScope: CoroutineScope,
    staffWeekShiftTypeHours: MutableMap<String, Int>,
    context: Context
) {

    Log.d("EditWeekLogDialog", "SleepOver: ${staffWeekShiftTypeHours["SleepOver"]}")
    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false
    }

    var morningSliderPosition by remember { mutableStateOf(staffWeekShiftTypeHours["Morning"]) }
    val highestMorningHours = staffWeekShiftTypeHours["Morning"]?.toFloat()

    var nightSliderPosition by remember { mutableStateOf(staffWeekShiftTypeHours["Night"]) }
    val highestNightHours = staffWeekShiftTypeHours["Night"]?.toFloat()

    var sleepOverSliderPosition by remember { mutableStateOf(staffWeekShiftTypeHours["SleepOver"]) }
    val highestSleepOverHours = staffWeekShiftTypeHours["SleepOver"]?.toFloat()

    var totalMorningPay by remember { mutableDoubleStateOf(morningSliderPosition?.toDouble()?.times(SHIFT_HOURLY_PAY) ?: 0.0) }
    var totalNightPay by remember { mutableDoubleStateOf(nightSliderPosition?.toDouble()?.times(SHIFT_HOURLY_PAY) ?: 0.0) }
    var totalSleepOverPay by remember { mutableDoubleStateOf(sleepOverSliderPosition?.toDouble()?.times(SHIFT_HOURLY_PAY) ?: 0.0) }
    var totalPay by remember { mutableDoubleStateOf(totalMorningPay.plus(totalNightPay).plus(totalSleepOverPay)) }


    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(8.dp),
        ) {
            Box(modifier = Modifier.padding(4.dp), contentAlignment = Alignment.Center) {
                if (isTaskRunning.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.Transparent) //will later be converted to background color
                ) {

                    Text(
                        text = stringResource(id = R.string.staff_adjust_hours_dialog_title),
                        modifier = Modifier.padding(8.dp),
                        fontSize = 20.sp
                    )
                    totalMorningPay = highestMorningHours?.times(SHIFT_HOURLY_PAY)!!
                    totalNightPay = highestNightHours?.times(SHIFT_HOURLY_PAY)!!
//                    totalSleepOverPay = highestSleepOverHours?.times(SHIFT_HOURLY_PAY)!!

                    if (morningSliderPosition != null) {
                        Text(
                            text = stringResource(id = R.string.morning_text),
                            style = Typography.bodyLarge,
                            modifier = Modifier
                                .offset(x = 8.dp)
                                .padding(4.dp)
                        )
                        CustomSlider(
                            value = morningSliderPosition!!.toFloat(),
                            onValueChange = {
                                /*
                                * if the previous value is greater than the current value, add to total, else, subtract
                                *
                                * steps:
                                * 1. save the previous morning pay
                                * 2. create a new morning pay
                                * 3. subtract the previous morning pay from the total pay and add the new morning payu
                                * */
                                val previousValue = morningSliderPosition
                                morningSliderPosition = it.toInt()
                                val previousTotalMorningPay = previousValue!!.toDouble().times(
                                    SHIFT_HOURLY_PAY)
                                totalMorningPay =
                                    morningSliderPosition!!.toDouble().times(SHIFT_HOURLY_PAY)

                                totalPay = totalPay.minus(previousTotalMorningPay).plus(totalMorningPay)
                            },
                            valueRange = 0f..highestMorningHours,
                            showLabel = true,
                            showIndicator = true
                        ) {
                            context.toast(it.toString())
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                    if (nightSliderPosition != null) {
                        Text(
                            text = stringResource(id = R.string.night_text),
                            style = Typography.bodyLarge,
                            modifier = Modifier
                                .offset(x = 8.dp)
                                .padding(4.dp)
                        )
                        CustomSlider(
                            value = nightSliderPosition!!.toFloat(),
                            onValueChange = {
                                val previousValue = nightSliderPosition
                                nightSliderPosition = it.toInt()
                                val previousTotalNightPay = previousValue!!.toDouble().times(
                                    SHIFT_HOURLY_PAY)
                                totalNightPay =
                                    nightSliderPosition!!.toDouble().times(SHIFT_HOURLY_PAY)
                                totalPay = totalPay.minus(previousTotalNightPay).plus(totalNightPay)


                            },
                            valueRange = 0f..highestNightHours!!,
                            showLabel = true,
                            showIndicator = true
                        ) {
                            context.toast(it.toString())
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }

                    if (sleepOverSliderPosition != null) {
                        Text(
                            text = stringResource(id = R.string.sleep_over_text),
                            style = Typography.bodyLarge,
                            modifier = Modifier
                                .offset(x = 8.dp)
                                .padding(4.dp)
                        )
                        CustomSlider(
                            value = sleepOverSliderPosition!!.toFloat(),
                            onValueChange = {
                                val previousValue = sleepOverSliderPosition
                                sleepOverSliderPosition = it.toInt()
                                val previousTotalSleepOverPay = previousValue!!.toDouble().times(
                                    SHIFT_HOURLY_PAY)
                                totalSleepOverPay = sleepOverSliderPosition!!.toDouble()
                                    .times(SHIFT_HOURLY_PAY)
                                totalPay = totalPay.minus(previousTotalSleepOverPay).plus(totalSleepOverPay)

                            },
                            valueRange = 0f..highestSleepOverHours!!,
                            showLabel = true,
                            showIndicator = true
                        ) {
                            context.toast(it.toString())
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }

//                    Slider(value = sliderPosition, onValueChange = {sliderPosition = it}, valueRange = 0f..noOfMorningHours, steps = noOfMorningHours.toInt())
//                    totalPay = totalMorningPay + totalNightPay + totalSleepOverPay
//                    if (totalPay > newTotalPay)
//                        totalPay = totalPay.minus(newTotalPay)


                    Text(
                        text = stringResource(id = R.string.total_pay_text, totalPay),
                        fontWeight = FontWeight.Bold,
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(4.dp)
                    )


                    ButtonComponent(
                        buttonText = stringResource(id = R.string.submit_text),
//                        enabled = selectedHouse.isNotBlank() && selectedAssignmentDay.isNotBlank() && selectedAssignmentType.isNotBlank(),
                        onClick = {
                            Log.d(
                                "EditWeekLogDialog",
                                "morning: ${morningSliderPosition?.toInt()} \nnight: ${nightSliderPosition?.toInt()}\nsleepover: ${sleepOverSliderPosition?.toInt()}"
                            )
                            coroutineScope.launch {
                                val reportLog = ReportLog(

                                )

                            }

                            /*ensure that the staff has not already been assigned a shift on that day ☑️
                            * ensure that no other staff has been assigned that particular day and time, to the same house ☑️*/
                        }
                    )


                }
            }

        }
    }
}













