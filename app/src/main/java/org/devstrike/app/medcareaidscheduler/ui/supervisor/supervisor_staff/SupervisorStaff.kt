/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.FloatActionButton
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.Notification
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components.StaffItemCard
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.notificationsCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.userCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.toast
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupervisorStaff(navController: NavHostController) {
    var isItemClicked = false
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }
    val staffData: MutableState<UserData> = remember { mutableStateOf(UserData()) }

    val messageAllStaff: MutableState<Boolean> = remember { mutableStateOf(false) }
    val messageReceiver: MutableState<String> = remember { mutableStateOf("") }
    val messageTitle: MutableState<String> = remember { mutableStateOf("") }
    val messageBody: MutableState<String> = remember { mutableStateOf("") }


    val allStaffForMessage = mutableListOf("All")


    val context = LocalContext.current

    val TAG = "SupervisorHouses"


    // Get the list of items from Firestore
    val staff = remember { mutableStateOf(listOf<UserData>()) }


    var isNewStaffClicked by rememberSaveable {
        mutableStateOf(false)
    }


    var showMessageDialog by rememberSaveable {
        mutableStateOf(false)
    }

    val heightTextFields by remember { mutableStateOf(64.dp) }
    var textFieldsSize by remember { mutableStateOf(Size.Zero) }

    var houseListExpanded by remember {
        mutableStateOf(false)
    }
    var staffListExpanded by remember {
        mutableStateOf(false)
    }

    val interactionSource = remember {
        MutableInteractionSource()
    }

    val isTaskRunning = remember { mutableStateOf(false) }


    val scope = rememberCoroutineScope()
    val supervisor = getUser(
        userId = auth.uid!!,
        context = context
    )!!

    LaunchedEffect(Unit) {
        val staffList = mutableListOf<UserData>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                userCollectionRef.whereEqualTo("userRole", "staff").whereEqualTo(
                    "userProvinceID", supervisor.userProvinceID
                )
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(UserData::class.java)
                staffList.add(item)
            }
        }
        staff.value = staffList
//        Log.d(TAG, "SupervisorHouses: ${houses.value}")
    }

    Scaffold(
        floatingActionButton = {
            FloatActionButton(modifier = Modifier.clickable {
//            isNewConnectClicked = true
            }, fabText = "Message Staff", onClick = {
                showMessageDialog = true
                isNewStaffClicked = true
            })
        },
        floatingActionButtonPosition = FabPosition.End,
    ) {
        val sheetState = rememberModalBottomSheetState()
        var isSheetOpen by rememberSaveable {
            mutableStateOf(false)
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (isTaskRunning.value) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        Column(modifier = Modifier.padding(it)) {
            //search bar
            //list of cards


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
                val listOfStaff = staff.value

                item {
                    Text(
                        text = "Total Staff in Province: ${listOfStaff.size}",
                        modifier = Modifier.padding(8.dp)
                    )
                }

                val filteredList = listOfStaff.filter { staffItem ->
                    staffItem.userFirstName.contains(
                        searchQuery.value,
                        true
                    ) || staffItem.userLastName.contains(
                        searchQuery.value,
                        true
                    )
                }
                items(filteredList) { staff ->
                    StaffItemCard(staff = staff, onClick = {
                        staffData.value = staff
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
                        SupervisorStaffDetail(staffData.value)

                    Log.d(TAG, "MusicLandingScreen: $isItemClicked")

                }

            }


        }

        if (showMessageDialog) {
            Dialog(onDismissRequest = { showMessageDialog = false }) {
                Card(
                    //shape = MaterialTheme.shapes.medium,
                    shape = RoundedCornerShape(10.dp),
                    // modifier = modifier.size(280.dp, 240.dp)
                    modifier = Modifier.padding(12.dp),
                ) {

                    Text(
                        text = stringResource(id = R.string.send_notification_dialog_title),
                        style = Typography.titleMedium,
                        modifier = Modifier.padding(8.dp)
                    )


                    //staff to message
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = messageAllStaff.value,
                            onCheckedChange = { isChecked -> messageAllStaff.value = isChecked },
                        )
                        Text(text = "Message all staff")
                    }
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(8.dp)
                            .border(
                                width = 1.8.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .onGloballyPositioned { coordinates ->
                                textFieldsSize = coordinates.size.toSize()
                            },
                            enabled = !messageAllStaff.value,
                            placeholder = { Text(text = stringResource(id = R.string.select_staff_to_message_placeholder)) },
                            value = messageReceiver.value, onValueChange = {
                                messageReceiver.value = it
                                staffListExpanded = true
                            }, colors = TextFieldDefaults.colors(
                                cursorColor = Color.Black,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Done
                            ),
                            singleLine = true,
                            trailingIcon = {
                                IconButton(
                                    onClick = { staffListExpanded = !staffListExpanded },
                                    enabled = !messageAllStaff.value
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = "arrow"
                                    )
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = staffListExpanded) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(textFieldsSize.width.dp)
                        ) {
                            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                                if (messageReceiver.value.isNotEmpty()) {
                                    items(
                                        staff.value.filter { staff ->
                                            staff.userFirstName.lowercase()
                                                .contains(messageReceiver.value.lowercase()) || staff.userLastName.contains(
                                                messageReceiver.value.lowercase()
                                            )
                                        }//.sortedBy{}
                                    ) { staff ->
                                        DropDownListItem(title = "${staff.userFirstName}, ${staff.userLastName}") { title ->
                                            messageReceiver.value = title
                                            staffListExpanded = false

                                        }
                                    }
                                } else {
                                    items(staff.value) { staff ->
                                        DropDownListItem(title = "${staff.userFirstName}, ${staff.userLastName}") { title ->
                                            messageReceiver.value = title
                                            staffListExpanded = false
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // message title
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .border(
                                    width = 1.8.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            placeholder = { Text(text = stringResource(id = R.string.notification_title_title)) },
                            value = messageTitle.value,
                            onValueChange = {
                                messageTitle.value = it
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                        )
                    }

                    //message body
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heightTextFields.times(2))
                                .padding(4.dp)
                                .border(
                                    width = 1.8.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                ),
                            placeholder = { Text(text = stringResource(id = R.string.notification_body_title)) },
                            value = messageBody.value,
                            onValueChange = {
                                messageBody.value = it
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    ButtonComponent(buttonText = stringResource(id = R.string.send_text),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        onClick = {
                            isTaskRunning.value = true
                            val notificationID = UUID.randomUUID().toString()
                            var staffReceiver = ""
                            for (staffValue in staff.value) {
                                val staffName =
                                    "${staffValue.userFirstName}, ${staffValue.userLastName}"
                                if (messageReceiver.value == staffName) {
                                    staffReceiver = staffValue.userID
                                }
                            }

                            val notificationType = if (staffReceiver.isNotBlank()) {
                                "Personal Message"
                            } else {
                                "General Message"
                            }
                            val newNotification = Notification(

                                notificationID = notificationID,
                                notificationType = notificationType,
                                notificationSenderID = auth.uid!!,
                                notificationReceiverID = staffReceiver,
                                notificationTitle = messageTitle.value,
                                notificationMessage = messageBody.value,
                                notificationSentDate = System.currentTimeMillis().toString(),
                                notificationProvinceID = getUser(
                                    auth.uid!!,
                                    context
                                )!!.userProvinceID
                            )

                            CoroutineScope(Dispatchers.IO).launch {
                                notificationsCollectionRef.document(notificationID)
                                    .set(newNotification).addOnCompleteListener {
                                        isTaskRunning.value = false
                                        showMessageDialog = false
                                        context.toast("Message Sent")
                                    }.addOnFailureListener { e ->
                                        isTaskRunning.value = false
                                        context.toast(
                                            e.localizedMessage?.toString() ?: "Some error occurred"
                                        )
                                    }
                            }

                        })


                }
            }
        }
    }
}


