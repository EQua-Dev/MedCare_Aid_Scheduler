/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_shifts

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.NameTag
import org.devstrike.app.medcareaidscheduler.components.PlainFloatActionButton
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.shiftCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.calculateHoursBetweenTimes
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.toast
import java.util.Calendar

@Composable
fun SupervisorAddShiftType(addedShiftTypes: List<ShiftType>, onClose: () -> Unit = {}) {

    val context = LocalContext.current
    val TAG = "SupervisorAddHouseSheet"

    val shiftTypeName: MutableState<String> = remember { mutableStateOf("") }
    val shiftTypeStartTime: MutableState<String> = remember { mutableStateOf("") }
    val shiftTypeEndTime: MutableState<String> = remember { mutableStateOf("") }
    val houseProvince: MutableState<String> = remember { mutableStateOf("") }
    val shiftTypeNoOfHours: MutableState<String> = remember { mutableStateOf("") }

    val listOfAvailableShiftTypes: MutableList<ShiftType> = mutableListOf()

    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running

    // state of the gender menu
    var shiftTypeNameExpanded by remember {
        mutableStateOf(false)
    }

    var textFilledSize by remember {
        mutableStateOf(Size.Zero)
    }
    val shiftTypeNameIcon = if (shiftTypeNameExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    // Get the list of items from Firestore
    val shiftTypesNames = remember { mutableStateOf(mutableListOf<ShiftType>()) }
    val shiftTypesNamesData: MutableState<ShiftType> = remember { mutableStateOf(ShiftType()) }
    val isPickingStartTime: MutableState<Boolean> = remember { mutableStateOf(false) }
    val isPickingEndTime: MutableState<Boolean> = remember { mutableStateOf(false) }

    val userInfo = getUser(auth.uid!!, context)!!

    val calendar = Calendar.getInstance()

    var selectedDateText by remember { mutableStateOf("") }
    var selectedTimeText by remember { mutableStateOf("") }

// Fetching current hour, and minute
    val hour = calendar[Calendar.HOUR_OF_DAY]
    val minute = calendar[Calendar.MINUTE]

    val timePicker = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            selectedTimeText = "$selectedHour:$selectedMinute"
            if (isPickingStartTime.value) {
                shiftTypeStartTime.value = selectedTimeText
                isPickingStartTime.value = false
            } else if (isPickingEndTime.value) {
                shiftTypeEndTime.value = selectedTimeText
                isPickingEndTime.value = false
            }
        }, hour, minute, true
    )

    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false

        val shiftTypesList = mutableListOf<ShiftType>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.shiftCollectionRef.whereEqualTo(
                    "shiftTypeOwnerProvinceID",
                    userInfo.userProvinceID
                )
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(ShiftType::class.java)
                shiftTypesList.add(item)
            }
        }
        shiftTypesNames.value = shiftTypesList

        for (item in addedShiftTypes) {
            for (shiftType in shiftTypesNames.value) {
                if (shiftType.shiftTypeName == item.shiftTypeName)
                    shiftTypesNames.value.remove(shiftType)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp), contentAlignment = Alignment.Center
    ) {
        if (isTaskRunning.value) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

            Text(
                text = "Add Shift Type",
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                textAlign = TextAlign.Center,
                style = Typography.titleLarge,
            )

            OutlinedTextField(value = shiftTypeName.value,
                onValueChange = { shiftTypeName.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    },
                label = { Text(text = stringResource(id = R.string.shift_type_name)) },
                trailingIcon = {
                    Icon(
                        shiftTypeNameIcon,
                        "",
                        Modifier.clickable { shiftTypeNameExpanded = !shiftTypeNameExpanded })
                }
            )

//            DropdownMenu(
//                expanded = shiftTypeNameExpanded,
//                onDismissRequest = { shiftTypeNameExpanded = false },
//                modifier = Modifier.width(with(LocalDensity.current){textFilledSize.width.toDp()})
//            ) {
//                shiftTypesNames.value.forEach { item ->
//                    DropdownMenuItem(
//                        onClick = {
//                            gender.value = item
//                            shiftTypeNameExpanded = false
//                        }
//                    ) {
//                        Text(text = item)
//                    }
//                }
//            }

            TextFieldComponent(
                value = shiftTypeStartTime.value,
                onValueChange = { shiftTypeStartTime.value = it },
                label = "Shift Start Time",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "Shift Start Time",
                modifier = Modifier
                    .onFocusChanged {
                        if (it.isFocused) {
                            // focused
                            isPickingStartTime.value = true
                            timePicker.show()

                        } else {
                            // not focused
                        }
                    }
                    .clickable {

                    }
            )
            TextFieldComponent(
                value = shiftTypeEndTime.value,
                onValueChange = { shiftTypeEndTime.value = it },
                label = "Shift End Time",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "Shift End Time",
                modifier = Modifier
                    .onFocusChanged {
                        if (it.isFocused) {
                            // focused
                            isPickingEndTime.value = true
                            timePicker.show()

                        } else {
                            // not focused
                        }
                    }
                    .clickable {

                    }
            )

            if (shiftTypeStartTime.value.isNotBlank() && shiftTypeEndTime.value.isNotBlank()) {
                shiftTypeNoOfHours.value = calculateHoursBetweenTimes(
                    lesserTime = shiftTypeStartTime.value,
                    greaterTime = shiftTypeEndTime.value
                ).toString()
                TextFieldComponent(
                    value = "${shiftTypeNoOfHours.value} total hours",
                    onValueChange = { shiftTypeNoOfHours.value = it },
                    label = "Shift Number of Hours",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = "Shift Number of Hours",
                    enabled = false
                )
            }


//
//            TextFieldComponent(
//                value = houseDistrict.value,
//                onValueChange = { houseDistrict.value = it },
//                label = "House District",
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    autoCorrect = false,
//                    keyboardType = KeyboardType.Text,
//                    imeAction = ImeAction.Next
//                ),
//                inputType = "House District",
//            )

//            TextFieldComponent(
//                value = provinceInfo.provinceName,
////                onValueChange = { houseDistrict.value = it },
//                label = "House Province",
//                enabled = false,
//                keyboardOptions = KeyboardOptions.Default.copy(
//                    autoCorrect = false,
//                    keyboardType = KeyboardType.Text,
//                    imeAction = ImeAction.Next
//                ),
//                inputType = "House Province",
//            )


            ButtonComponent(
                buttonText = "Add Shift Type",
                enabled = shiftTypeName.value.isNotBlank() && shiftTypeStartTime.value.isNotBlank() && shiftTypeEndTime.value.isNotBlank(),
                onClick = {
//
//                } else {
                    val newShiftType = ShiftType(
                        shiftTypeID = System.currentTimeMillis().toString(),
                        shiftTypeName = shiftTypeName.value,
                        shiftTypeOwnerProvinceID = userInfo.userProvinceID,
                        shiftTypeSupervisorID = userInfo.userID,
                        shiftTypeStartTime = shiftTypeStartTime.value,
                        shiftTypeEndTime = shiftTypeEndTime.value,
                        shiftTypeNoOfHours = shiftTypeNoOfHours.value,
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        isTaskRunning.value = true
                        try {
                            //val appointmentQuery = Common.facilityCollectionRef.whereEqualTo("facilityId", facility.facilityId).get().await()
                            shiftCollectionRef.document(newShiftType.shiftTypeID).set(newShiftType)
                                .addOnCompleteListener {
                                    isTaskRunning.value = false
                                    context.toast("Shift Type Added")
//                                withContext(Dispatchers.Main){
//                                    navController.popBackStack()
                                    onClose()
//                                }

                                }
                                .addOnFailureListener { e ->
                                    isTaskRunning.value = false
                                    context.toast(e.message.toString())

                                }
                        } catch (e: Exception) {
                            context.toast(e.message.toString())
                        }
                    }
                    //save house

//                }
//
                })

            Spacer(modifier = Modifier.height(64.dp))


        }
    }

//
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//
//    }

}

