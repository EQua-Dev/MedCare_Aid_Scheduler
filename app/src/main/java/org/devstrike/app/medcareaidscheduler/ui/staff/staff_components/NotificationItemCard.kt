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
import org.devstrike.app.medcareaidscheduler.data.Notification
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getUser

@Composable
fun NotificationItemCard(notification: Notification, onClick: () -> Unit) {

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
            modifier = Modifier.fillMaxWidth().padding(4.dp)
        ) {
            //Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = getDate(notification.notificationSentDate.toLong(), "EEE, dd MMM, yyyy"),
                    style = Typography.titleSmall,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.5f),

                    )
                Text(
                    text = notification.notificationType,
                    fontStyle = FontStyle.Italic,
                    style = Typography.titleSmall,
                    modifier = Modifier.padding(4.dp).weight(0.5f),
                    textAlign = TextAlign.End
                )
            }

            Text(text = notification.notificationTitle, modifier = Modifier.padding(4.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Text(
                    text = getUser(notification.notificationSenderID, context)!!.userFirstName,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End
                )
            }

        }
    }

}