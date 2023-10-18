/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.FloatActionButton
import org.devstrike.app.medcareaidscheduler.components.NameTag
import org.devstrike.app.medcareaidscheduler.components.PlainFloatActionButton
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.Province
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.toast

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SupervisorAddHouseSheet(/*house: House*/) {
    val context = LocalContext.current
    val houseName: MutableState<String> = remember { mutableStateOf("") }
    val houseAddress: MutableState<String> = remember { mutableStateOf("") }
    val houseDistrict: MutableState<String> = remember { mutableStateOf("") }
    val houseProvince: MutableState<String> = remember { mutableStateOf("") }
    val houseNoOfClients: MutableState<Int> = remember { mutableStateOf(1) }
    val houseID: MutableState<String> = remember { mutableStateOf("") }
    val houseIsSpecialCare: MutableState<Boolean> = remember { mutableStateOf(false) }
    val houseSpecialCareType: MutableState<String> = remember { mutableStateOf("") }
    val houseContactPerson: MutableState<String> = remember { mutableStateOf("") }
    val houseContactNumber: MutableState<String> = remember { mutableStateOf("") }
    val houseIs3rdParty: MutableState<Boolean> = remember { mutableStateOf(false) }
    val houseNameOfPatients: MutableList<String> = remember { mutableListOf() }
    val houseNecessaryInformation: MutableState<String> = remember { mutableStateOf("") }
    val houseDateAdded: MutableState<String> = remember { mutableStateOf("") }
    val houseAddingSupervisor: MutableState<String> = remember { mutableStateOf("") }
    val currentPatientName: MutableState<String> = remember { mutableStateOf("") }

    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false
    }
    val userInfo = getUser(auth.uid!!, context)!!
    val provinceInfo = getProvince(userInfo.userProvinceID, context)!!


    Box(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            TextFieldComponent(
                value = houseName.value,
                onValueChange = { houseName.value = it },
                label = "House Name",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "House Name",
            )
            TextFieldComponent(
                value = houseAddress.value,
                onValueChange = { houseAddress.value = it },
                label = "House Address",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "House Address",
            )

            TextFieldComponent(
                value = houseDistrict.value,
                onValueChange = { houseDistrict.value = it },
                label = "House District",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "House District",
            )

            TextFieldComponent(
                value = provinceInfo.provinceName,
//                onValueChange = { houseDistrict.value = it },
                label = "House Province",
                enabled = false,
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "House Province",
            )

            //Row of button
            Text(
                text = stringResource(id = R.string.no_of_patients_text),
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.padding(4.dp)) {
                PlainFloatActionButton(
                    modifier = Modifier
                        .size(12.dp)
                        .clickable {
                            if (houseNoOfClients.value > 1)
                                houseNoOfClients.value = houseNoOfClients.value - 1
                            else
                                houseNoOfClients.value = houseNoOfClients.value
                        }, fabText = "-"
                )
                Text(text = houseNoOfClients.value.toString())
                PlainFloatActionButton(
                    modifier = Modifier
                        .size(48.dp)
                        .clickable {
                            houseNoOfClients.value = houseNoOfClients.value + 1
                        }, fabText = "+"
                )
            }

            Row(modifier = Modifier.padding(4.dp)) {
                Checkbox(
                    checked = houseIsSpecialCare.value,
                    onCheckedChange = { isChecked -> houseIsSpecialCare.value = isChecked },
                )
                Text(text = "Is Special Care")
            }

            TextFieldComponent(
                value = houseSpecialCareType.value,
                onValueChange = { houseSpecialCareType.value = it },
                label = "House Special Care Type",
                enabled = houseIsSpecialCare.value,
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "House Special Care Type",
            )

            TextFieldComponent(
                value = houseContactPerson.value,
                onValueChange = { houseContactPerson.value = it },
                label = "House Contact Person Name",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                inputType = "House Contact Person Name",
            )

            TextFieldComponent(
                value = houseContactPerson.value,
                onValueChange = { houseContactPerson.value = it },
                label = "House Contact Number",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                inputType = "House Contact Person Name",
            )

            Row(modifier = Modifier.padding(4.dp)) {
                Checkbox(
                    checked = houseIs3rdParty.value,
                    onCheckedChange = { isChecked -> houseIs3rdParty.value = isChecked }
                )
                Text(text = "Is 3rd Party House")
            }

            Text(
                text = stringResource(id = R.string.names_of_patients_title),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(4.dp)
            )

            FlowRow(
                modifier = Modifier.padding(1.dp),
                horizontalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                houseNameOfPatients.forEach { patientName ->
                    NameTag(name = patientName, onDeleteClick = {
                        houseNameOfPatients.remove(patientName)
                    })

                }
            }

            Row {
                TextFieldComponent(
                    value = currentPatientName.value,
                    onValueChange = { currentPatientName.value = it },
                    label = "Patient Name (add to list if multiple)",
                    enabled = !houseIs3rdParty.value,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = "House Patient Name",
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                PlainFloatActionButton(
                    fabText = "Add", onClick = {
                        if (currentPatientName.value.isNotBlank()) {
                            houseNameOfPatients.add(currentPatientName.value)
                            currentPatientName.value = ""
                        } else {
                            context.toast("House is 3rd party")
                        }
                    }
                )

            }

            TextFieldComponent(
                value = houseNecessaryInformation.value,
                onValueChange = { houseNecessaryInformation.value = it },
                label = "House Necessary Information",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.None
                ),
                inputType = "House Necessary Information",
            )

            ButtonComponent(buttonText = "Add House", onClick = {
                if (validateNewHouseInputs(
                        houseName.value,
                        houseAddress.value,
                        houseDistrict.value,
                        houseProvince.value,
                        houseNoOfClients.value,
                        houseIsSpecialCare.value,
                        houseSpecialCareType.value,
                        houseContactPerson.value,
                        houseContactNumber.value,
                        houseNameOfPatients,
                        houseNecessaryInformation.value,
                    )
                ) {
                    //save new house
                } else {
                    context.toast("Fill out all fields")
                }
            })


        }
    }
}

fun validateNewHouseInputs(
    houseName: String,
    houseAddress: String,
    houseDistrict: String,
    houseProvince: String,
    houseNoOfClients: Int,
    houseIsSpecialCare: Boolean,
    houseSpecialCareType: String,
    houseContactPerson: String,
    houseContactNumber: String,
    houseNameOfPatients: MutableList<String>,
    houseNecessaryInformation: String
): Boolean {
    if (houseName.isBlank()) {
        return false
    }
    if (houseAddress.isBlank()) {
        return false
    }
    if (houseDistrict.isBlank()) {
        return false
    }
    if (houseProvince.isBlank()) {
        return false
    }
    if (houseNoOfClients < 1) {
        return false
    }
    if (houseIsSpecialCare && houseSpecialCareType.isBlank()) {
        return false
    }
    if (houseContactPerson.isBlank()) {
        return false
    }
    if (houseContactNumber.isBlank() || houseContactNumber.length < 10) {
        return false
    }

    return true

}

private fun getUser(userId: String, context: Context): UserData? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.userCollectionRef.document(userId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(UserData::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}

private fun getProvince(provinceId: String, context: Context): Province? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = Common.provinceCollectionRef.document(provinceId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(Province::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    return runBlocking { deferred.await() }
}