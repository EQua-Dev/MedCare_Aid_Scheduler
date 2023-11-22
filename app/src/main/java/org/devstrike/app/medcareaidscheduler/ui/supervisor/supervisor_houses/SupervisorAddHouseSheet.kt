/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.NameTag
import org.devstrike.app.medcareaidscheduler.components.PlainFloatActionButton
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.toast
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common.housesCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getUser


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SupervisorAddHouseSheet(
    onClose: () -> Unit = {},
    /*navController: NavHostController*/
) {
    val context = LocalContext.current
    val TAG = "SupervisorAddHouseSheet"
    val houseName: MutableState<String> = remember { mutableStateOf("") }
    val houseAddress: MutableState<String> = remember { mutableStateOf("") }
    val houseDistrict: MutableState<String> = remember { mutableStateOf("") }
    val houseProvince: MutableState<String> = remember { mutableStateOf("") }
    val houseNoOfClients: MutableState<Int> = remember { mutableIntStateOf(1) }
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
    val coroutineScope = rememberCoroutineScope()


    val houseNames = remember { mutableStateOf(listOf<String>()) }

    val heightTextFields by remember { mutableStateOf(64.dp) }
    var textFieldsSize by remember { mutableStateOf(Size.Zero) }


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false
    }
    val userInfo = getUser(auth.uid!!, context)!!
    val provinceInfo = getProvince(userInfo.userProvinceID, context)!!


//
//    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//
//    }


    Box(modifier = Modifier
        .fillMaxSize()
        .padding(8.dp), contentAlignment = Alignment.Center) {
        if (isTaskRunning.value) {
            CircularProgressIndicator(modifier = Modifier
                .size(48.dp)
                .zIndex(1f))
        }
        LazyColumn() {
            item {

                Text(
                    text = "Add House",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                )


                OutlinedTextField(
                    value = houseName.value,
                    onValueChange = {
                        houseName.value = it
                    },
                    enabled = true,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_name_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_name_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        //.focusRequester(focusRequester)
                        .onFocusChanged {
                            isTaskRunning.value = true
                            coroutineScope.launch {
                                val houseNamesList = mutableListOf<String>()

                                withContext(Dispatchers.IO) {
                                    val querySnapshot =
                                        housesCollectionRef
                                            .whereEqualTo(
                                                "houseAddingSupervisor",
                                                auth.uid!!
                                            )
                                            .get()
                                            .addOnCompleteListener {
                                                isTaskRunning.value = false
                                                for (document in it.result) {
                                                    val item = document.toObject(House::class.java)
                                                    houseNamesList.add(item.houseName)
                                                }
                                                houseNames.value = houseNamesList
                                            }
                                }
                            }

                        },
                    shape = RoundedCornerShape(8.dp)
                )

              /*  TextFieldComponent(
                    value = houseName.value,
                    onValueChange = {
                        houseName.value = it
                    },
                    label = "House Name",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    inputType = "House Name",
                    modifier = Modifier.onFocusChanged {
                        isTaskRunning.value = true
                        coroutineScope.launch {
                            val houseNamesList = mutableListOf<String>()

                            withContext(Dispatchers.IO) {
                                val querySnapshot =
                                    housesCollectionRef.whereEqualTo(
                                        "houseAddingSupervisor",
                                        auth.uid!!
                                    )
                                        .get().addOnCompleteListener {
                                            isTaskRunning.value = false
                                            for (document in it.result) {
                                                val item = document.toObject(House::class.java)
                                                houseNamesList.add(item.houseName)
                                            }
                                            houseNames.value = houseNamesList
                                        }
                            }
                        }


                    }
                )*/
                OutlinedTextField(
                    value = houseAddress.value,
                    onValueChange = {
                        houseAddress.value = it
                    },
                    enabled = true,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_address_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_address_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
           OutlinedTextField(
                    value = houseDistrict.value,
                    onValueChange = {
                        houseDistrict.value = it
                    },
                    enabled = true,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_district_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_district_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

           OutlinedTextField(
                    value = provinceInfo.provinceName,
                    onValueChange = {
                        //houseDistrict.value = it
                    },
                    enabled = false,
                    singleLine = true,
/*

                    label = { Text(text = stringResource(id = R.string.new_house_district_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_district_placeholder)) },
*/

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                //Row of button
                Text(
                    text = stringResource(id = R.string.no_of_patients_text),
                    fontWeight = FontWeight.Bold
                )
                Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    PlainFloatActionButton(
                        modifier = Modifier
                            .size(12.dp), fabText = "-", onClick = {
                            if (houseNoOfClients.value > 1)
                                houseNoOfClients.value = houseNoOfClients.value - 1
                            else
                                houseNoOfClients.value = houseNoOfClients.value

                            Log.d(TAG, "SupervisorAddHouseSheet: ${houseNoOfClients.value}")
                        }
                    )
                    Text(text = houseNoOfClients.value.toString(), modifier = Modifier.padding(4.dp))
                    PlainFloatActionButton(
                        modifier = Modifier
                            .size(48.dp), fabText = "+", onClick = {
                            houseNoOfClients.value = houseNoOfClients.value + 1

                        }
                    )
                }

                Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = houseIsSpecialCare.value,
                        onCheckedChange = { isChecked -> houseIsSpecialCare.value = isChecked },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
                    )
                    Text(text = "Is Special Care")
                }


                OutlinedTextField(
                    value = houseSpecialCareType.value,
                    onValueChange = {
                        houseSpecialCareType.value = it
                    },
                    enabled = houseIsSpecialCare.value,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_special_care_type_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_special_care_type_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                OutlinedTextField(
                    value = houseContactPerson.value,
                    onValueChange = {
                        houseContactPerson.value = it
                    },
                    enabled = true,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_contact_person_name_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_contact_person_name_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = houseContactNumber.value,
                    onValueChange = {
                        houseContactNumber.value = it
                    },
                    enabled = true,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_contact_number_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_contact_number_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.padding(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = houseIs3rdParty.value,
                        onCheckedChange = { isChecked -> houseIs3rdParty.value = isChecked },
                        modifier = Modifier.clip(RoundedCornerShape(8.dp))
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

                Row(verticalAlignment = Alignment.CenterVertically) {

                    OutlinedTextField(
                        value = currentPatientName.value,
                        onValueChange = {
                            currentPatientName.value = it
                        },
                        enabled = !houseIs3rdParty.value,
                        singleLine = true,

                        label = { Text(text = stringResource(id = R.string.new_house_patient_names_title)) },
                        placeholder = { Text(text = stringResource(id = R.string.new_house_patient_names_placeholder)) },

                        keyboardOptions = KeyboardOptions.Default.copy(
                            autoCorrect = true,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .padding(8.dp)
                            .weight(0.7f),
                        shape = RoundedCornerShape(8.dp)
                    )

                    TextButton(enabled = !houseIs3rdParty.value, modifier = Modifier.weight(0.3F), onClick = {
                        if (currentPatientName.value.isNotBlank()) {
                            houseNameOfPatients.add(currentPatientName.value)
                            currentPatientName.value = ""
                        } else {
                            context.toast("House is 3rd party")
                        }
                    }
                    ) {
                        Text(text = stringResource(id = R.string.add_text))

            }
                    /*ButtonComponent(buttonText = "Add", enabled = !houseIs3rdParty.value, modifier = Modifier.weight(0.3F), onClick = {
                        if (currentPatientName.value.isNotBlank()) {
                            houseNameOfPatients.add(currentPatientName.value)
                            currentPatientName.value = ""
                        } else {
                            context.toast("House is 3rd party")
                        }
                    })*/

                }

                OutlinedTextField(
                    value = houseNecessaryInformation.value,
                    onValueChange = {
                        houseNecessaryInformation.value = it
                    },
                    enabled = !houseIs3rdParty.value,
                    singleLine = true,

                    label = { Text(text = stringResource(id = R.string.new_house_necessary_info_title)) },
                    placeholder = { Text(text = stringResource(id = R.string.new_house_necessary_info_placeholder)) },

                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = true,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(heightTextFields.times(2)),
                    shape = RoundedCornerShape(8.dp)
                )

                ButtonComponent(buttonText = "Add House", modifier = Modifier.fillMaxWidth().padding(8.dp), onClick = {
                    for (nameOfHouse in houseNames.value){
                        if(houseName.value == nameOfHouse){
                            context.toast("House name already exists")
                        }
                    }
                    if (houseName.value.isEmpty()) {
                        context.toast("House name cannot be empty")
                    }
                    if (houseAddress.value.isEmpty()) {
                        context.toast("House address cannot be empty")
                    }
                    if (houseDistrict.value.isEmpty()) {
                        context.toast("House district cannot be empty")
                    }
                    if (houseIsSpecialCare.value && houseSpecialCareType.value.isEmpty()) {
                        context.toast("House special care type cannot be empty")
                    }
                    if (houseContactPerson.value.isEmpty()) {
                        context.toast("House contact person name cannot be empty")
                    }
                    if (houseContactNumber.value.length != 10) {
                        context.toast("Enter a valid phone number")
                    } else {
                        val newHouse = House(
                            houseName = houseName.value,
                            houseAddress = houseAddress.value,
                            houseDistrict = houseDistrict.value,
                            houseProvince = provinceInfo.provinceID,
                            houseNoOfClients = houseNoOfClients.value.toString(),
                            houseID = System.currentTimeMillis().toString(),
                            houseIsSpecialCare = houseIsSpecialCare.value,
                            houseSpecialCareType = houseSpecialCareType.value,
                            houseContactPerson = houseContactPerson.value,
                            houseContactNumber = houseContactNumber.value,
                            houseIs3rdParty = houseIs3rdParty.value,
                            houseNameOfPatients = houseNameOfPatients,
                            houseNecessaryInformation = houseNecessaryInformation.value,
                            houseDateAdded = System.currentTimeMillis().toString(),
                            houseAddingSupervisor = userInfo.userID,
                        )
                        CoroutineScope(Dispatchers.IO).launch {
                            isTaskRunning.value = true
                            try {
                                //val appointmentQuery = Common.facilityCollectionRef.whereEqualTo("facilityId", facility.facilityId).get().await()
                                housesCollectionRef.document(newHouse.houseID).set(newHouse)
                                    .addOnCompleteListener {
                                        isTaskRunning.value = false
                                        context.toast("House Saved")
//                                withContext(Dispatchers.Main){
                                        onClose()
//                                        navController.popBackStack()

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

                    }
//
                })

                Spacer(modifier = Modifier.height(64.dp))


            }
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
//    if (houseProvince.isBlank()) {
//        return false
//    }
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



