/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.Payment
import org.devstrike.app.medcareaidscheduler.utils.Common.TIME_FORMAT_EDMYHM
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShiftType

@Composable
fun PaymentItemCard(payment: Payment, onClick: () -> Unit = {}) {

    val context = LocalContext.current
//    val configuration = LocalConfiguration.current
//
//
//    val isDarkMode =
//        configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
//
//    val cardColor = if (isDarkMode) {
//        DarkColor
//    } else {
//        WhiteColor
//    }


    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            ,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {

            val dateText = if (payment.paymentDate.isNotBlank()) {
                "Date Paid: ${getDate(payment.paymentDate.toLong(), TIME_FORMAT_EDMYHM)}"
            } else {
                "Date Approved: ${
                    getDate(
                        payment.paymentDateApproved.toLong(),
                        TIME_FORMAT_EDMYHM
                    )
                }"
            }

            Text(
                text = "Status: ${payment.paymentStatus}",
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),

                )
            Text(
                text = dateText,
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),
            )
            Text(
                text = "No of hours: ${payment.paymentNoOfHoursPaid} hours",
                fontStyle = FontStyle.Italic,
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),

                )

            Text(
                text = "Amount: €${payment.paymentAmount}",
                fontStyle = FontStyle.Italic,
                style = Typography.titleSmall,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth(),

                )

        }
    }

}