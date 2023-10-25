/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
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
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.toast

@Composable
fun SupervisorStaffDetail(staff: UserData) {

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
                    Text(
                        text = staff.userFirstName.first().toString(),
                        fontWeight = FontWeight.Bold,
                        style = Typography.displaySmall
                    )
                    Text(
                        text = staff.userLastName.first().toString(),
                        fontWeight = FontWeight.Bold,
                        style = Typography.displaySmall
                    )
                }
            }

        }

        val staffName = "${staff.userFirstName} ${staff.userLastName}"
        val dateJoined = getDate(staff.userPasswordChangedDate.toLong(), "dd MMM, yyyy")
        Text(
            text = staffName,
            style = Typography.titleLarge,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Text(
            text = "Joined $dateJoined",
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
                        .weight(0.8F)
                )
                Icon(imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1F)
                        .clickable {

                        })
                Icon(imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1F)
                        .clickable {

                        })
            }
//            Spacer(modifier = Modifier.height(24.dp))


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
                    text = staff.userAddress,
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
                val staffProvince = getProvince(provinceId = staff.userProvinceID, context = context)!!
                val staffDistrictAndProvince = "${staff.userDistrictID}, ${staffProvince.provinceName}"

                Text(
                    text = staffDistrictAndProvince,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )

                Text(
                    text = stringResource(id = R.string.email_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = staff.userEmail,
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
                    text = staff.userContactNumber,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            ButtonComponent(buttonText = stringResource(id = R.string.report_staff_btn_text, staff.userFirstName), onClick = {
                context.toast("Reporting ${staff.userFirstName} ${staff.userLastName}")
            })

            Spacer(modifier = Modifier.height(32.dp))


        }


    }

}