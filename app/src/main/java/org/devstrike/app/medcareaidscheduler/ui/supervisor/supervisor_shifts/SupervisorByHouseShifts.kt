/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_shifts

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.components.CardItem
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorByHouseShifts() {

    val TAG = "SupervisorByHouseShifts"
    val context = LocalContext.current
    val houseShiftList = remember { mutableStateOf(listOf<AssignedShift>()) }

    Log.d(TAG, "SupervisorByHouseShifts: By Houses")

    val houseShifts = remember {
        mutableStateOf(mutableMapOf<String, MutableList<AssignedShift>>())

    }
    var isCardExpanded by remember { mutableStateOf(false) }
    var selectedHouse by remember {
        mutableStateOf(House())
    }
    var selectedHouseAssignments = remember {
        listOf<AssignedShift>()
    }
    val sheetState = rememberModalBottomSheetState()
    var isSheetOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isItemClicked = false

    /*
    * 1. Get the list of all shifts that are within the current month
    * 2. Put all the houses in a map of the house and a list of all its shifts e.g: map of "houseName": ListOf<Shifts>
    * 3. In each of the houses shifts, get all the shifts and categorize them into the 4 weeks of the month
    * 4. In each of the weeks, get the shifts for a day of the week
    * */

    LaunchedEffect(Unit) {
        val assignedShiftsList = mutableListOf<AssignedShift>()
        val fetchedHouseShift = mutableMapOf<String, MutableList<AssignedShift>>()
        //val fetchedHouseAssignedShift = mutableListOf<AssignedShift>()

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
            for (shift in houseShiftList.value) {

                val shiftHouse = getHouse(shift.assignedHouseID, context)!!
                if (fetchedHouseShift.entries.isEmpty()) {
                    val fetchedHouseAssignedShift = mutableListOf<AssignedShift>()
                    fetchedHouseAssignedShift.add(shift)
                    fetchedHouseShift[shiftHouse.houseName] = fetchedHouseAssignedShift

                } else {
                    for (houseShift in houseShifts.value.entries) {

                        if (houseShift.key == shiftHouse.houseName) {
                            val fetchedHouseAssignedShift = fetchedHouseShift[houseShift.key]
                            fetchedHouseAssignedShift!!.add(shift)
                            fetchedHouseShift[houseShift.key] = fetchedHouseAssignedShift
                            break
                        } else {
                            val fetchedHouseAssignedShift = fetchedHouseShift[houseShift.key]
                            fetchedHouseAssignedShift!!.add(shift)
                            fetchedHouseShift[shiftHouse.houseName] = fetchedHouseAssignedShift
                        }
                    }
                }
            }
            Log.d(TAG, "houseShiftList: ${houseShiftList.value}")
            Log.d(TAG, "fetchedHouseShift: $fetchedHouseShift")
            houseShifts.value = fetchedHouseShift


        }
    }
    Column {
        LazyColumn {
            var houseNames = listOf<AssignedShift>()
            val innerHouse = mutableListOf<AssignedShift>()
            for (house in houseShiftList.value) {
                innerHouse.add(house)
                houseNames = innerHouse.distinctBy { house.assignedHouseID }
            }
//            items(houseNames) { house ->
//                CardItem(text = getHouse(house.assignedHouseID, context)!!.houseName,
//                    onClick = {
//                        //pull up bottom sheet with all shifts of just the selected house
//                        val houseShiftsToSubmit = mutableListOf<AssignedShift>()
//                        for (assignment in houseShiftList.value){
//                            if (assignment.assignedHouseID == house.assignedHouseID)
//                                selectedHouse = getHouse(house.assignedHouseID, context)!!
//                            houseShiftsToSubmit.add(assignment)
//                        }
//                        selectedHouseAssignments = houseShiftsToSubmit
//                        isSheetOpen = true
//                        isItemClicked = true
//
//                    })
//
////                ExpandableCard(
////                    title = houseName.key,
////                    content = houseName.value.toList()
////                )
//            }


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
                Log.d(TAG, "SupervisorByHouseShifts: $selectedHouse")
                Log.d(TAG, "SupervisorByHouseShifts: $selectedHouseAssignments")
                //if (isItemClicked)
                    SupervisorHouseShiftDetail(houseDetail = selectedHouse, onDismiss = {
                        isSheetOpen = false
                        isItemClicked = false
                    }, allAssignments = selectedHouseAssignments)
            }

        }


    }


}