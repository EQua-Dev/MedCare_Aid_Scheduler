/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_shifts.concluded_shifts

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_components.UpcomingShiftItemCard
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_shifts.upcoming_shifts.StaffHouseDetail
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffConcludedShifts() {

    val TAG = "StaffUpcomingShift"

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }
    val staffWeekHours: MutableState<Int> = remember { mutableIntStateOf(0) }
    val staffWeekPayableAmount: MutableState<Int> = remember { mutableIntStateOf(0) }
    val staffWeekShiftTypeHours: MutableMap<String, Int> = remember {
        mutableMapOf()
     }

    val shiftPerHour = 2
    val listOfHoursPayable = mutableListOf<String>()
    for (item in staffWeekShiftTypeHours.keys){
        listOfHoursPayable.add("$item:${staffWeekShiftTypeHours[item]},")
    }

    // Get the list of items from Firestore
//    val upcomingShiftList = remember {
//        mutableListOf<AssignedShift>()
//    }
    val concludedShiftList = remember { mutableStateOf(listOf<AssignedShift>()) }

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
                    Log.d(TAG, "StaffUpcomingShift item: $item")

                    if (item.assignedShiftDate.toLong() < System.currentTimeMillis() && item.assignedShiftStatus != Common.SHIFT_ACTIVE)
                        concludedShiftsList.add(item)
                    //also check for the current shift
                }
                Log.d(TAG, "active shift: $activeShiftData")
                Log.d(TAG, "concludedShiftsList: $concludedShiftsList")
                concludedShiftList.value = concludedShiftsList
                Log.d(TAG, "StaffConcludedShift Value: ${concludedShiftList.value}")

            }

        }


    }


    Surface(modifier = Modifier.fillMaxSize()) {

        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
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
            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.staff_log_shifts_served),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))

                /*
                ~ check the list of assigned shifts for the ones with the staff ID
                ~ check the shifts that are within this week
                ~ then check for the shifts that have clock out time || check the ones that have a status of 'served'
                */
                Text(text = stringResource(id = R.string.not_available),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))
            }
            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.staff_log_hours),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))

                /*
                ~ check the list of assigned shifts for the ones with the staff ID
                ~ check the shifts that are within this week
                ~ check for the shifts that have clock out time || check the ones that have a status of 'served'
                ~ then for each of the shifts, get the shift type ID and get the number of hours in those shift types
                ~ then have a variable that will be increasing its value by adding every shift hour found
                */
                Text(text = staffWeekHours.value.toString(),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))
            }
            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.staff_log_payable_hours),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))

                /*
                ~ check the list of assigned shifts for the ones with the staff ID
                ~ check the shifts that are within this week
                ~ check for the shifts that have clock out time || check the ones that have a status of 'served'
                ~ have three variables for each of the shift types {for each distinct shift type, there will be a map containing the type name and its hours total value}
                ~ then for each of the items in the map, populate its alias name and its hours total
                */

                Text(text = listOfHoursPayable.toString(),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))
            }
            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = stringResource(id = R.string.staff_log_payable_amount),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))

                /*
                ~ check the map of hours payable
                ~ get all the values and add them, then multiply the cost of 1 shift by the sum

                */

                val totalHours = staffWeekShiftTypeHours.values.sum()

                staffWeekPayableAmount.value = totalHours.toLong().times(shiftPerHour.toLong()).toInt()

                Text(text = staffWeekPayableAmount.value.toString(),
                    modifier = Modifier
                        .weight(0.5F)
                        .padding(4.dp))
            }

            Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                ButtonComponent(buttonText = stringResource(id = R.string.edit_text), onClick = {
                    context.toast("will edit log")
                }, modifier = Modifier.weight(0.5F).padding(4.dp))
                ButtonComponent(buttonText = stringResource(id = R.string.submit_text), onClick = {
                    context.toast("will submit log")
                }, modifier = Modifier.weight(0.5F).padding(4.dp))
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
                    val shiftTypeInfo = getShiftType(assignedShift.assignedShiftTypeID, context)!!
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

    }

}