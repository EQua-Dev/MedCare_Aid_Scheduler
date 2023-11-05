/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_notifications

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.Notification
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_components.NotificationItemCard
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.HouseItemCard
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses.SupervisorHouseDetail
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff.DropDownListItem
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff.isDateSelectable
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.PERSONAL_NOTIFICATION_TAG
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.openDial
import org.devstrike.app.medcareaidscheduler.utils.toast
import java.util.Calendar

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

    var isItemClicked = false

    val staffInfo = getUser(auth.uid!!, context)!!



    LaunchedEffect(Unit) {
        val notificationList = mutableListOf<Notification>()

        withContext(Dispatchers.IO) {

            val querySnapshot =
                if (notificationTabType == PERSONAL_NOTIFICATION_TAG){
                    Common.notificationsCollectionRef.whereEqualTo("notificationReceiverID", auth.uid!!)
                        .get().await()
                }else{
                    Common.notificationsCollectionRef.whereEqualTo("notificationProvinceID", staffInfo.userProvinceID).whereEqualTo("notificationReceiverID", "")
                        .get().await()
                }


            for (document in querySnapshot) {
                val item = document.toObject(Notification::class.java)
                notificationList.add(item)
            }
        }
        notifications.value = notificationList
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")
    }


    Surface(modifier = Modifier.fillMaxSize()) {

        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Column(modifier = Modifier.padding(4.dp)) {
            //search bar
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
            //list of cards

            LazyColumn {

                val listOfNotifications = notifications.value
                val filteredList = listOfNotifications.filter { notification ->
                    val supervisorInfo = getUser(notification.notificationSenderID, context)!!
                    getUser(notification.notificationSenderID, context)!!.userFirstName.contains(
                        searchQuery.value,
                        true
                    ) || getUser(notification.notificationSenderID, context)!!.userLastName.contains(
                        searchQuery.value,
                        true
                    ) || notification.notificationType.contains(searchQuery.value, true)
                            || notification.notificationMessage.contains(searchQuery.value, true)
                }
                items(filteredList) { notification ->
                    NotificationItemCard(notification = notification, onClick = {
                        notificationData.value = notification
                        isSheetOpen = true
                        isItemClicked = true
                    })
                }
//                    listOfHouses.forEach { house ->
//                        Log.d(TAG, "SupervisorHouses List: $house")
////                        HouseItemCard(house = house)
//                        HouseItemCard(house)
//                    }
            }
//                val houses =
//                    housesCollectionRef.whereEqualTo("houseAddingSupervisor", auth.uid!!).get()
            // Iterate over the list of items and display each item using the ItemComposable composable function


        }
        if (isSheetOpen) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isSheetOpen = false }) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isItemClicked)
                        NotificationDetailDialog(notificationData.value, onDismiss = {
                            isItemClicked = false
                            isSheetOpen = false
                        })

                }

            }


        }


        Text(text = if (notificationTabType == "personal") "Staff Personal Notification" else "Staff General Notification")
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

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(8.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = getDate(notification.notificationSentDate.toLong(), "EEE, dd MMM, yyyy"),
                    style = Typography.titleSmall,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(0.5f),

                    )
                Text(
                    text = notification.notificationType,
                    fontStyle = FontStyle.Italic,
                    style = Typography.titleSmall,
                    modifier = Modifier.padding(4.dp),
                    textAlign = TextAlign.End
                )
            }

            Text(text = notification.notificationTitle, modifier = Modifier.padding(4.dp), fontWeight = FontWeight.Bold)
            Text(text = notification.notificationMessage, modifier = Modifier.padding(4.dp))
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)) {
                Text(
                    text = getUser(notification.notificationSenderID, context)!!.userFirstName,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = "call",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clickable {
                            //openDial
                            openDial(
                                getUser(
                                    notification.notificationSenderID,
                                    context
                                )!!.userContactNumber, context
                            )
                        }

                )
            }
            Spacer(modifier = Modifier.height(24.dp))

            Box(contentAlignment = Alignment.CenterEnd, modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Text(text = "Okay", modifier = Modifier.clickable {
                    onDismiss()
                })
            }


        }
    }
}
