/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.Notification
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_components.NotificationItemCard
import org.devstrike.app.medcareaidscheduler.ui.theme.Balsamiq
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.PERSONAL_NOTIFICATION_TAG
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.openDial
import org.devstrike.app.medcareaidscheduler.utils.toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftNotificationScreen(notificationTabType: String) {

    /*
 * 1. the tabs for 'Personal' and 'General'
 * 2. the search bar for searching dates, types and sender of notification
 * 3. list of notifications
 *
 * */
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }

    // Get the list of items from Firestore
    val notifications = remember { mutableStateOf(listOf<Notification>()) }
    val notificationData: MutableState<Notification> = remember { mutableStateOf(Notification()) }

    var isItemClicked by rememberSaveable {
        mutableStateOf(false)
    }

    val staffInfo = getUser(auth.uid!!, context)!!



    LaunchedEffect(Unit) {
        val notificationList = mutableListOf<Notification>()

        withContext(Dispatchers.IO) {

            val querySnapshot =
                if (notificationTabType == PERSONAL_NOTIFICATION_TAG) {
                    Common.notificationsCollectionRef.whereEqualTo(
                        "notificationReceiverID",
                        auth.uid!!
                    )
                        .get()
                } else {
                    Common.notificationsCollectionRef.whereEqualTo(
                        "notificationProvinceID",
                        staffInfo.userProvinceID
                    ).whereEqualTo("notificationReceiverID", "")
                        .get()
                }

            querySnapshot.addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    for (document in snapshot.result) {
                        val item = document.toObject(Notification::class.java)
                        notificationList.add(item)
                    }
                    notifications.value = notificationList
                } else {
                    context.toast(snapshot.exception?.localizedMessage ?: "Some error occurred")
                }
            }.addOnFailureListener { e ->
                context.toast(e.localizedMessage ?: "Some error occurred")
            }
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")
        }
    }


    Surface(modifier = Modifier.fillMaxSize()) {


        //search bar

        //list of cards

        LazyColumn {
            item {
                TextFieldComponent(
                    value = searchQuery.value,
                    onValueChange = { searchQuery.value = it },
                    label = "Search",
                    keyboardOptions = KeyboardOptions.Default.copy(
                        autoCorrect = false,
                    ),
                    inputType = "Search",
                    leadingIcon = R.drawable.ic_search,
                    modifier = Modifier.padding(16.dp)

                )
            }

            val listOfNotifications =
                notifications.value.sortedByDescending { notification -> notification.notificationSentDate }
            val filteredList = listOfNotifications.filter { notification ->
                val supervisorInfo = getUser(notification.notificationSenderID, context)!!
                getUser(notification.notificationSenderID, context)!!.userFirstName.contains(
                    searchQuery.value,
                    true
                ) || getUser(
                    notification.notificationSenderID,
                    context
                )!!.userLastName.contains(
                    searchQuery.value,
                    true
                ) || notification.notificationType.contains(searchQuery.value, true)
                        || notification.notificationMessage.contains(searchQuery.value, true)
            }
            items(filteredList) { notification ->
                NotificationItemCard(notification = notification, onClick = {
                    notificationData.value = notification
                    isItemClicked = true
                })
            }

        }
        if (isItemClicked) {
            Dialog(onDismissRequest = { isItemClicked = false }) {
                Card(
                    //shape = MaterialTheme.shapes.medium,
                    shape = RoundedCornerShape(10.dp),
                    // modifier = modifier.size(280.dp, 240.dp)
                    modifier = Modifier.padding(12.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = getDate(
                                notificationData.value.notificationSentDate.toLong(),
                                "EEE, dd MMM, yyyy"
                            ),
                            style = Typography.titleSmall,
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(0.5f),

                            )
                        Text(
                            text = notificationData.value.notificationType,
                            fontStyle = FontStyle.Italic,
                            style = Typography.titleSmall,
                            modifier = Modifier
                                .padding(4.dp)
                                .weight(0.5f),
                            textAlign = TextAlign.End
                        )
                    }

                    Text(
                        text = notificationData.value.notificationTitle,
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(),
                        fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, style = Typography.titleLarge
                    )
                    Text(
                        text = notificationData.value.notificationMessage,
                        modifier = Modifier.padding(4.dp)
                    )

                    Spacer(modifier = Modifier.padding(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                             .padding(8.dp), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sender: ${
                                getUser(
                                    notificationData.value.notificationSenderID,
                                    context
                                )!!.userFirstName
                            }",
                            modifier = Modifier.fillMaxWidth(0.5f),
                            textAlign = TextAlign.Start
                        )
                        TextButton(onClick = { //openDial
                            openDial(
                                getUser(
                                    notificationData.value.notificationSenderID,
                                    context
                                )!!.userContactNumber, context
                            )
                        }) {

                            Text(
                                text = "call",
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .weight(0.5f)

                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TextButton(onClick = { isItemClicked = false }) {
                            Text(
                                text = "OKAY",
                                modifier = Modifier.padding(4.dp),
                                fontFamily = Balsamiq,
                                fontWeight = FontWeight.Bold
                            )
                        }

                    }


                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailDialog(
    notification: Notification,
    onDismiss: () -> Unit,
) {
    val TAG = "NotificationDetailDialog"
    val context = LocalContext.current

    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false
    }

}
