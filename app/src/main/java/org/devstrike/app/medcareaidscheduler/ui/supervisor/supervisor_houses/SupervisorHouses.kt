/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.devstrike.app.medcareaidscheduler.components.FloatActionButton
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.utils.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorHouses(navController: NavHostController) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()


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
    ) {
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.padding(it)) {
            //search bar
            //list of cards
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Red), contentAlignment = Alignment.Center
            ) {

                Text(text = "Supervisor Houses")
            }
        }

//
//        if (showModal) {
//            ModalBottomSheet(
//                sheetState = sheetState,
//                onDismissRequest = { showModal = false }) {
//                Column(
//                    Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    if (isNewHouseClicked)
//                        SupervisorAddHouseSheet(onClose = {
//                            showModal = false
//                        })
//                    else if (isNewStaffClicked)
//                        context.toast("Add New Staff!")
//                }
//            }
//        }
    }
}

