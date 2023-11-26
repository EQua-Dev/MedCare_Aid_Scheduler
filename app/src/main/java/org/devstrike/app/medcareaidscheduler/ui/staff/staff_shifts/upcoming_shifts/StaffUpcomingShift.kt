/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_shifts.upcoming_shifts

import android.util.Log
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.firebase.firestore.MetadataChanges
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_components.UpcomingShiftItemCard
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.SHIFT_ACTIVE
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Preview(showBackground = true, showSystemUi = true)
@OptIn(ExperimentalUnitApi::class, ExperimentalMaterial3Api::class)
@ExperimentalPagerApi
@Composable
fun StaffUpcomingShift() {

    val TAG = "StaffUpcomingShift"

    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }

    // Get the list of items from Firestore
//    val upcomingShiftList = remember {
//        mutableListOf<AssignedShift>()
//    }
    val upcomingShiftList = remember { mutableStateOf(listOf<AssignedShift>()) }

    var upcomingShiftData = AssignedShift()
    val activeShiftData: MutableState<AssignedShift> = remember {
        mutableStateOf(AssignedShift())
    }
    var isItemClicked = false

    val staffInfo = getUser(Common.auth.uid!!, context)!!




    LaunchedEffect(Unit) {
        val upcomingShiftsList = mutableListOf<AssignedShift>()

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
                upcomingShiftsList.clear()
                for (document in snapshot!!) {
                    val item = document.toObject(AssignedShift::class.java)
                    Log.d(TAG, "StaffUpcomingShift item: $item")
                    if (item.assignedShiftStatus == SHIFT_ACTIVE)
                        activeShiftData.value = item
                    if (item.assignedShiftDate.toLong() >= System.currentTimeMillis() && item.assignedShiftStatus != SHIFT_ACTIVE)
                        upcomingShiftsList.add(item)
                    //also check for the current shift
                }
                Log.d(TAG, "active shift: $activeShiftData")
                Log.d(TAG, "upcomingShiftsList: $upcomingShiftsList")
                upcomingShiftList.value = upcomingShiftsList
                Log.d(TAG, "StaffUpcomingShift Value: ${upcomingShiftList.value}")

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
                text = "Ongoing Shift",
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(8.dp)
                    .offset(x = 16.dp)
            )
            if (activeShiftData.value.assignedShiftClockInTime.isNotBlank()) {
                UpcomingShiftItemCard(assignedShift = activeShiftData.value, onClick = {
                    upcomingShiftData = activeShiftData.value
                    isSheetOpen = true
                    isItemClicked = true
                }, onContestClick = {
                })
            }else{

                Text(
                    text = "No active shift",
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.Bold,
                    style = Typography.titleSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .offset(x = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Divider(Modifier.padding(8.dp))
            Spacer(modifier = Modifier.height(8.dp))
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
                modifier = Modifier.padding(8.dp)

            )
            //list of cards

            LazyColumn {
                val listOfUpcomingShifts = upcomingShiftList.value
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
                        upcomingShiftData = assignedShift
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
                    val houseInfo = getHouse(upcomingShiftData.assignedHouseID, context)!!
                    if (isItemClicked)
                        StaffHouseDetail(
                            houseInfo, upcomingShiftData, onDismissed = {
                                isItemClicked = false
                                isSheetOpen = false
                            }
                        )

                }

            }


        }

    }

}

