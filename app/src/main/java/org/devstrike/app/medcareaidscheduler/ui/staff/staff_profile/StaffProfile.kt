/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun StaffProfile(navController: NavHostController) {

    val context = LocalContext.current
    val staffDetail = getUser(auth.uid!!, context)!!

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
                            text = staffDetail.userFirstName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                        Text(
                            text = staffDetail.userLastName.first().toString(),
                            fontWeight = FontWeight.Bold,
                            style = Typography.displaySmall
                        )
                    }
                }

            }

            val dateJoined = getDate(staffDetail.userPasswordChangedDate.toLong(), "dd MMM, yyyy")
            Text(
                text = "${staffDetail.userFirstName} ${staffDetail.userLastName}",
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
                text = "Role: ${staffDetail.userRole}",
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
                        text = staffDetail.userGender,
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
                        text = staffDetail.userAddress,
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
                        getProvince(provinceId = staffDetail.userProvinceID, context = context)!!
                    val staffDistrictAndProvince =
                        "${staffDetail.userDistrictID}, ${staffProvince.provinceName}"

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
                        text = staffDetail.userEmail,
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
                        text = staffDetail.userContactNumber,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )
                }
                Row {
                    Text(
                        text = stringResource(id = R.string.account_details_title),
                        fontWeight = FontWeight.Light,
                        modifier = Modifier
                            .padding(4.dp)
                            .weight(0.8F)
                    )
                }

                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(), elevation = CardDefaults.cardElevation(
                        defaultElevation = 8.dp
                    )

                ) {


                    Text(
                        text = stringResource(id = R.string.iban_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = staffDetail.userBankAccountNumber,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.bank_name_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = staffDetail.userBankName,
                        style = Typography.bodyLarge,
                        modifier = Modifier
                            .offset(x = 8.dp)
                            .padding(4.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.account_holder_name_title),
                        style = Typography.bodySmall,
                        modifier = Modifier.padding(4.dp)
                    )

                    Text(
                        text = staffDetail.userBankAccountHolder.ifBlank { "N/A" },
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