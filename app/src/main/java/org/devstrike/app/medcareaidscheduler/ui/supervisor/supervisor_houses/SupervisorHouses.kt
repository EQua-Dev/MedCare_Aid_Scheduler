/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.housesCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorHouses(navController: NavHostController) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }

    var isItemClicked = false


    // Get the list of items from Firestore
    val houses = remember { mutableStateOf(listOf<House>()) }
    val houseData: MutableState<House> = remember { mutableStateOf(House()) }


    var isNewHouseClicked by rememberSaveable {
        mutableStateOf(false)
    }

    var isNewStaffClicked by rememberSaveable {
        mutableStateOf(false)
    }


    var showModal by rememberSaveable {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()
    val TAG = "SupervisorHouses"

    LaunchedEffect(Unit) {
        val houseList = mutableListOf<House>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                housesCollectionRef.whereEqualTo("houseAddingSupervisor", auth.uid!!)
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(House::class.java)
                houseList.add(item)
            }
        }
        houses.value = houseList
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")
    }

    Scaffold(
        floatingActionButton = {
            FloatActionButton(
                modifier = Modifier.clickable {
//                isNewHouseClicked = true
                    context.toast("Add House Clicked!")
                },
                fabText = "Add House", onClick = {
                    navController.navigate(Screen.AddNewHouse.route)
//                    showModal = true
//                    isNewHouseClicked = true
                }
            )
            /*addScreen = SupervisorAddHouseSheet(), showSheet = isNewHouseClicked*/
            //)
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { innerPadding ->
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.padding(innerPadding)) {
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

                val listOfHouses = houses.value
                val filteredList = listOfHouses.filter { houseItem ->
                    houseItem.houseName.contains(
                        searchQuery.value,
                        true
                    ) || houseItem.houseContactPerson.contains(
                        searchQuery.value,
                        true
                    ) || houseItem.houseAddress.contains(searchQuery.value, true)
                            || houseItem.houseDistrict.contains(searchQuery.value, true)
                }
                items(filteredList) { house ->
                    HouseItemCard(house = house, onClick = {
                        houseData.value = house
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
                    if (isItemClicked)
                        SupervisorHouseDetail(houseData.value)

                    Log.d(TAG, "MusicLandingScreen: $isItemClicked")

                }

            }


        }
    }

}


