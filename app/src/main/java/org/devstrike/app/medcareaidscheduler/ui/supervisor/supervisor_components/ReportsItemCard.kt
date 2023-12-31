/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun ReportItemCard(staffID: String, totalHours: String, onClick: () -> Unit) {

    val context = LocalContext.current
//    val isOwner: MutableState<Boolean> = remember { mutableStateOf(false) }

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
            .height(100.dp)
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

    ) {

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            val staffDetails = getUser(staffID, context)!!
            //Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "${staffDetails.userFirstName}, ${staffDetails.userLastName}",
                fontWeight = FontWeight.Bold,
                style = Typography.bodyLarge,
                modifier = Modifier.padding(4.dp)
            )
      /*      val totalHours = report. reportLogTotalPayableDaysHours.toInt()
                .plus(report.reportLogTotalPayableNightsHours.toInt()).plus(
                report
                    .reportLogTotalPayableSleepOversHours.toInt()
            )

*/
            Text(
                text = "Total Hours Worked: $totalHours",
                style = Typography.bodyMedium,
                modifier = Modifier.padding(4.dp)
            )
        }
    }

}