/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.FloatActionButton
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.HouseItemCard
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.StaffItemCard
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses.SupervisorAddHouseSheet
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.userCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.getUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorStaff(navController: NavHostController) {
    var isItemClicked = false
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }
    val staffData: MutableState<UserData> = remember { mutableStateOf(UserData()) }


    val context = LocalContext.current

    val TAG = "SupervisorHouses"


    // Get the list of items from Firestore
    val staff = remember { mutableStateOf(listOf<UserData>()) }


    var isNewStaffClicked by rememberSaveable {
        mutableStateOf(false)
    }


    var showModal by rememberSaveable {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val supervisor = getUser(
        userId = auth.uid!!,
        context = context
    )!!

    LaunchedEffect(Unit) {
        val staffList = mutableListOf<UserData>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                userCollectionRef.whereEqualTo("userRole", "staff").whereEqualTo(
                    "userProvinceID", supervisor.userProvinceID
                )
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(UserData::class.java)
                staffList.add(item)
            }
        }
        staff.value = staffList
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")
    }

    Scaffold(
        floatingActionButton = {
            FloatActionButton(modifier = Modifier.clickable {
//            isNewConnectClicked = true
            }, fabText = "Message Staff", onClick = {
                showModal = true
                isNewStaffClicked = true
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
            //list of cards

            LazyColumn {
                val listOfStaff = staff.value

                item {
                    Text(text = "Total Staff in Province: ${listOfStaff.size}", modifier = Modifier.padding(8.dp))
                }

                val filteredList = listOfStaff.filter { staffItem ->
                    staffItem.userFirstName.contains(
                        searchQuery.value,
                        true
                    ) || staffItem.userLastName.contains(
                        searchQuery.value,
                        true
                    )
                }
                items(filteredList) { staff ->
                    StaffItemCard(staff = staff, onClick = {
                        staffData.value = staff
                        isSheetOpen = true
                        isItemClicked = true
                    })
                }
//                    listOfHouses.forEach { house ->
//                        Log.d(TAG, "SupervisorHouses List: $house")
////                        HouseItemCard(house = house)
//                        HouseItemCard(house)
//                    }
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
                        SupervisorStaffDetail(staffData.value)

                    Log.d(TAG, "MusicLandingScreen: $isItemClicked")

                }

            }


        }
    }
}


