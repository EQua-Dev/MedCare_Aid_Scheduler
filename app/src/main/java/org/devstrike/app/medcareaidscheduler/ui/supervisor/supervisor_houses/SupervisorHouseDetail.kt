/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentWeek
import org.devstrike.app.medcareaidscheduler.utils.toast

@Composable
fun SupervisorHouseDetail(house: House) {

    val TAG = "SupervisorHouseDetail"
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val houseAssignedShifts = remember {
        mutableStateOf(listOf<AssignedShift>())

    }


    LaunchedEffect(key1 = Unit) {
        val houseAssignedShiftsList = mutableListOf<AssignedShift>()

        coroutineScope.launch {
            val querySnapshot =
                Common.assignedShiftsCollectionRef
                    .whereEqualTo("assignedHouseID", house.houseID)
//                                            .whereEqualTo(
//                                                "userProvinceID", supervisor.userProvinceID
//                                            )
                    .get()
                    .await()

            for (document in querySnapshot) {
                val item = document.toObject(AssignedShift::class.java)
                Log.d(
                    TAG,
                    "SupervisorStaffDetail Is current week?: ${
                        isTimeInCurrentMonth(item.assignedShiftDate.toLong())
                    }"
                )

                if (isTimeInCurrentMonth(item.assignedShiftDate.toLong())) {
                    //time is in current week
                    houseAssignedShiftsList.add(item)
                    houseAssignedShifts.value = houseAssignedShiftsList
                    Log.d(TAG, "SupervisorStaffDetail Item: $item")
                    Log.d(
                        TAG,
                        "SupervisorStaffDetail Is current week?: ${
                            isTimeInCurrentMonth(item.assignedShiftDate.toLong())
                        }"
                    )
                }
            }

            //staff.value = staffList
        }

    }




    LazyColumn() {
        item {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Surface(
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.secondary
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
//                    Text(
//                        text = house.houseName.first().toString(),
//                        fontWeight = FontWeight.Bold,
//                        style = Typography.displaySmall
//                    )
                        Text(
                            text = house.houseName.substring(0, 1).toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                    }
                }

            }

            val dateJoined = getDate(house.houseDateAdded.toLong(), "dd MMM, yyyy")
            Text(
                text = house.houseName,
                style = Typography.titleLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Text(
                text = "Added $dateJoined",
                style = Typography.titleSmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
            ) {

                Row {
                    Text(
                        text = stringResource(id = R.string.contact_title),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(0.9F)
                    )
                    Icon(imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        modifier = Modifier
                            .weight(0.1F)
                            .clickable {

                            })
                }

                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )

                ) {


                    Text(
                        text = stringResource(id = R.string.contact_address_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = house.houseAddress,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.district_province_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )
                    val houseProvince =
                        getProvince(provinceId = house.houseProvince, context = context)!!
                    val houseDistrictAndProvince =
                        "${house.houseDistrict}, ${houseProvince.provinceName}"

                    Text(
                        text = houseDistrictAndProvince,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.contact_person_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = house.houseContactPerson,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.phone_number_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = house.houseContactNumber,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row {
                    Text(
                        text = stringResource(id = R.string.service_title),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(0.9F)
                    )
                }


                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )

                ) {


                    Text(
                        text = stringResource(id = R.string.no_of_patients_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = house.houseNoOfClients,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.necessary_info_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )
                    Text(
                        text = house.houseNecessaryInformation.ifBlank { "N/A" },
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    if (house.houseNameOfPatients.isNotEmpty()) {
                        Text(
                            text = stringResource(id = R.string.client_names_title),
                            style = Typography.bodySmall,
                            modifier = Modifier.padding(4.dp)
                        )
                        LazyRow(
                            modifier = Modifier.offset(x = 8.dp)
                        ) {
                            items(house.houseNameOfPatients) { patientName ->
                                Card() {
                                    Text(
                                        text = patientName,
                                        modifier = Modifier.padding(4.dp),
                                        style = Typography.bodyMedium
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))

                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row {
                    Text(
                        text = stringResource(id = R.string.shift_this_week_title),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(0.9F)
                    )
                }


            }
        }

        items(houseAssignedShifts.value.sortedBy { assignedShift -> assignedShift.assignedShiftDate }) { shift ->
            Row(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = getDate(
                        shift.assignedShiftDate.toLong(),
                        "EEE, dd MMM, yyyy"
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.4F),
                )
                Text(
                    text = getShiftType(
                        shift.assignedShiftTypeID,
                        context
                    )!!.shiftTypeName,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.2F),
                    textAlign = TextAlign.Center

                )
                Text(
                    text = getUser(
                        shift.assignedStaffID,
                        context
                    )!!.userFirstName,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.4F),
                    textAlign = TextAlign.Center

                )
            }


            /* Card(
                 modifier = Modifier
                     .padding(8.dp)
                     .fillMaxWidth()
                     .heightIn(240.dp)
                     .background(MaterialTheme.colorScheme.secondary),
                 elevation = CardDefaults.cardElevation(
                     defaultElevation = 8.dp
                 )

             ) {

                 if (houseAssignedShifts.value.isEmpty()) {
                     Box(
                         modifier = Modifier.fillMaxSize(),
                         contentAlignment = Alignment.Center
                     ) {
                         Text(
                             text = stringResource(id = R.string.no_shifts_assigned),
                             fontStyle = FontStyle.Italic
                         )
                     }
                 } else {
                     LazyColumn(
                         contentPadding = PaddingValues(4.dp)
                     ) {
                         items(
                             houseAssignedShifts.value
                         ) { shift ->

                         }
                         //}
                     }
                 }
                 Spacer(modifier = Modifier.height(32.dp))

          //            ButtonComponent(
          //                buttonText = stringResource(
          //                    id = R.string.edit_house_btn_text,
          //                    house.houseName
          //                ), onClick = {
          //                    context.toast("Editing ${house.houseName}")
          //                }, modifier = Modifier.padding(8.dp).fillMaxWidth())

                 ButtonComponent(
                     buttonText = stringResource(
                         id = R.string.delete_house_btn_text,
                         house.houseName
                     ), onClick = {
                         context.toast("Deleting ${house.houseName}")
                     }, modifier = Modifier
                         .padding(8.dp)
                         .fillMaxWidth()
                 )

                 Spacer(modifier = Modifier.height(24.dp))


             }*/
        }
        item {

            Spacer(modifier = Modifier.height(32.dp))

            ButtonComponent(
                buttonText = stringResource(
                    id = R.string.edit_house_btn_text,
                    house.houseName
                ), onClick = {
                    context.toast("Editing ${house.houseName}")
                }, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )

            ButtonComponent(
                buttonText = stringResource(
                    id = R.string.delete_house_btn_text,
                    house.houseName
                ), onClick = {
                    context.toast("Deleting ${house.houseName}")
                }, modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
            )
        }


    }
}