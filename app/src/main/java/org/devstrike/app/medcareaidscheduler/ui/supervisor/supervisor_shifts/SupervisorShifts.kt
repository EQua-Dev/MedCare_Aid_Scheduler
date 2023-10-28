/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_shifts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.components.FloatActionButton
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorShifts(navController: NavHostController) {

    val context = LocalContext.current

    var showModal by rememberSaveable {
        mutableStateOf(false)
    }
    var isNewShiftClicked by rememberSaveable {
        mutableStateOf(false)
    }

    var isShiftDetailClicked by rememberSaveable {
        mutableStateOf(false)
    }


    // Get the list of items from Firestore
    val shiftTypes = remember { mutableStateOf(listOf<ShiftType>()) }
    val shiftTypeData: MutableState<ShiftType> = remember { mutableStateOf(ShiftType()) }
    val userProvince = getUser(userId = auth.uid!!, context = context)!!.userProvinceID


    LaunchedEffect(Unit) {
        val shiftTypeList = mutableListOf<ShiftType>()


        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.shiftCollectionRef.whereEqualTo("shiftTypeOwnerProvinceID", userProvince)
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(ShiftType::class.java)
                shiftTypeList.add(item)
            }
        }
        shiftTypes.value = shiftTypeList
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")
    }


    Scaffold(
        floatingActionButton = {
            FloatActionButton(modifier = Modifier.clickable {
//            isNewConnectClicked = true
            }, fabText = "Add Shift Type", onClick = {
                showModal = true
                isNewShiftClicked = true
            })
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.padding(it)) {
            //search bar
            //list of cards

            Text(text = "Shift Types")

            //search bar
//            TextFieldComponent(
//                value = searchQuery.value,
//                onValueChange = { searchQuery.value = it },
//                label = "Search",
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    autoCorrect = false,
//                ),
//                inputType = "Search",
//                leadingIcon = R.drawable.ic_search,
//                modifier = Modifier.padding(16.dp)
//
//            )
            //list of cards
//
            if (shiftTypes.value.isEmpty()){
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "Click the add button to add shift types for your province", fontStyle = FontStyle.Italic)
                }
            }else{
                LazyColumn {

                    val listOfShiftTypes = shiftTypes.value

                    items(listOfShiftTypes) { shiftType ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Text(text = shiftType.shiftTypeName, modifier = Modifier.fillMaxWidth(0.5F))
                            Text(text = "${shiftType.shiftTypeStartTime} - ${shiftType.shiftTypeEndTime}", modifier = Modifier.fillMaxWidth(0.4F))
                            Text(text = "edit", modifier = Modifier.fillMaxWidth(0.1F))
                        }
//                    StaffItemCard(staff = shiftType, onClick = {
//                        staffData.value = shiftType
//                        isSheetOpen = true
//                        isItemClicked = true
//                    })
                    }
//                    listOfHouses.forEach { house ->
//                        Log.d(TAG, "SupervisorHouses List: $house")
////                        HouseItemCard(house = house)
//                        HouseItemCard(house)
//                    }
                }
            }
        }
        if (showModal) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { showModal = false }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isNewShiftClicked)
                        SupervisorAddShiftType(shiftTypes.value, onClose = {
                            showModal = false
                            isNewShiftClicked = false
                        })

//                    Log.d(TAG, "MusicLandingScreen: $isItemClicked")

                }

            }


        }
    }

}