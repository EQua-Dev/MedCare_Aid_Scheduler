/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    assignment: AssignedShift
) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .clickable { onClick() },

        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)

    ) {
        val houseDetail = getHouse(assignment.assignedHouseID, context)!!
        val staffDetail = getUser(assignment.assignedStaffID, context)!!
        val supervisorDetail = getUser(assignment.assignedSupervisorID, context)!!
        val shiftDetail = assignment.assignedShiftTypes
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Text(
                text = getDate(assignment.assignedShiftDate.toLong(), "EEE, dd MMM, yyyy"),
                modifier = Modifier.weight(0.5f),
                fontWeight = FontWeight.SemiBold,
                style = Typography.bodySmall
            )
            Text(
                text = assignment.assignedShiftStatus,
                modifier = Modifier.weight(0.5f).padding(2.dp),
                fontStyle = FontStyle.Italic,
                style = Typography.bodySmall,
                textAlign = TextAlign.End
            )


        }

        Text(
            text = houseDetail.houseName,
            style = Typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(4.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
            Text(
                text = "${staffDetail.userFirstName} ${staffDetail.userLastName}",
                modifier = Modifier.weight(0.5f),
                fontWeight = FontWeight.SemiBold,
                style = Typography.bodySmall
            )
            Text(
                text = shiftDetail,
                modifier = Modifier.weight(0.5f).padding(2.dp),
                fontStyle = FontStyle.Italic,
                style = Typography.bodySmall,
                textAlign = TextAlign.End
            )
        }
    }
}