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
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun UpcomingShiftItemCard(assignedShift: AssignedShift, onClick: () -> Unit, onContestClick: () -> Unit) {

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
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            //Spacer(modifier = Modifier.height(16.dp))
            val shiftTypeInfo = getShiftType(assignedShift.assignedShiftTypeID, context)!!
            val houseInfo = getHouse(assignedShift.assignedHouseID, context)!!

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = getDate(assignedShift.assignedShiftDate.toLong(), "EEE, dd MMM, yyyy"),
                    style = Typography.titleSmall,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(0.5f),

                    )
                Text(
                    text = shiftTypeInfo.shiftTypeName,
                    fontStyle = FontStyle.Italic,
                    style = Typography.titleSmall,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.End
                )
            }

            Text(text = houseInfo.houseName, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
            Text(text = houseInfo.houseAddress, modifier = Modifier.padding(4.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                ButtonComponent(
                    buttonText = "contest",
                    modifier = Modifier.padding(4.dp),
                    onClick = {
                        // pop up dialog box for the staff to contest the shift allocation
                        onContestClick()
                    }
                )
            }

        }
    }

}