/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun ReportItemCard(reportShift: AssignedShift, onClick: () -> Unit) {

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
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

    ) {
        val shiftType = getShiftType(reportShift.assignedShiftTypeID, context)!!
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = getDate(reportShift.assignedShiftDate.toLong(), "EEE, dd MMM, yyyy"),
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .padding(.5.dp)
                        .weight(0.4f)
                )
                Text(
                    text = shiftType.shiftTypeName,
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.3f),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = stringResource(id = R.string.view_details_text),
                    style = Typography.bodyMedium,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.2f),
                    fontWeight = FontWeight.Bold,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.End
                )
            }
        }
    }

}