/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.toast

@Composable
fun SupervisorHouseDetail(house: House) {

    val context = LocalContext.current

    Column {
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

//        val staffName = "${house.houseName} ${house.userLastName}"
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
            modifier = Modifier.verticalScroll(
                rememberScrollState()
            )
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
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary),
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

            Text(
                text = stringResource(id = R.string.service_title),
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .padding(4.dp)
                    .weight(0.9F)
            )

            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary),
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
                    LazyRow {
                        items(house.houseNameOfPatients) { patientName ->
                            Text(
                                text = patientName,
                                modifier = Modifier.padding(4.dp),
                                style = Typography.bodyMedium
                            )

                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            ButtonComponent(
                buttonText = stringResource(
                    id = R.string.edit_house_btn_text,
                    house.houseName
                ), onClick = {
                    context.toast("Editing ${house.houseName}")
                }, modifier = Modifier.padding(8.dp))

            ButtonComponent(
                buttonText = stringResource(
                    id = R.string.delete_house_btn_text,
                    house.houseName
                ), onClick = {
                    context.toast("Deleting ${house.houseName}")
                }, modifier = Modifier.padding(8.dp))

            Spacer(modifier = Modifier.height(24.dp))


        }


    }

}