/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_profile

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun SupervisorProfile(navController: NavHostController) {


    val context = LocalContext.current
    val supervisorDetail = getUser(Common.auth.uid!!, context)!!

    LazyColumn(modifier = Modifier.padding(8.dp)) {
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
                        Text(
                            text = supervisorDetail.userFirstName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                        Text(
                            text = supervisorDetail.userLastName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                    }
                }

            }

            val dateJoined = getDate(supervisorDetail.userPasswordChangedDate.toLong(), "dd MMM, yyyy")
            Text(
                text = "${supervisorDetail.userFirstName} ${supervisorDetail.userLastName}",
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
            Text(
                text = "Role: ${supervisorDetail.userRole}",
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
                            .weight(0.8F)
                    )
                }
//            Spacer(modifier = Modifier.height(24.dp))


                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )

                ) {


                    Text(
                        text = stringResource(id = R.string.gender_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = supervisorDetail.userGender,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.contact_address_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = supervisorDetail.userAddress,
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
                    val staffProvince =
                        getProvince(provinceId = supervisorDetail.userProvinceID, context = context)!!
                    val staffDistrictAndProvince =
                        "${supervisorDetail.userDistrictID}, ${staffProvince.provinceName}"

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
                        text = supervisorDetail.userEmail,
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
                        text = supervisorDetail.userContactNumber,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )
                }


                Spacer(modifier = Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {

                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.request_province_switch_text))
                    }
                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = stringResource(id = R.string.reset_password_text_button))
                    }
                }

            }
        }
    }

}