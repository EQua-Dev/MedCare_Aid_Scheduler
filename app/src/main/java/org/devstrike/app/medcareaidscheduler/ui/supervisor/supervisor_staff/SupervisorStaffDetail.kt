/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.Notification
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.NOTIFICATION_TYPE_SHIFT_ASSIGNMENT
import org.devstrike.app.medcareaidscheduler.utils.Common.SHIFT_ACTIVE
import org.devstrike.app.medcareaidscheduler.utils.Common.assignedShiftsCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.housesCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.notificationsCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.shiftCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.userCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth
import org.devstrike.app.medcareaidscheduler.utils.openDial
import org.devstrike.app.medcareaidscheduler.utils.toast
import org.devstrike.libs.android.timetravel.TimeTraveller
import java.util.Calendar
import java.util.UUID

@Composable
fun SupervisorStaffDetail(staff: UserData) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val TAG = "SupervisorStaffDetail"


    val staffAssignedShifts = remember {
        mutableStateOf(listOf<AssignedShift>())

    }
    val allStaffAssignedShifts = remember {
        mutableStateOf(listOf<AssignedShift>())

    }
    val availableHousesToAssign = remember {
        mutableStateOf(listOf<House>())
    }
    val shiftTypes = remember { mutableStateOf(listOf<ShiftType>()) }


    var showCustomDialog by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(key1 = Unit) {
        val staffAssignedShiftsList = mutableListOf<AssignedShift>()

        coroutineScope.launch {
            val querySnapshot =
                Common.assignedShiftsCollectionRef
                    .whereEqualTo("assignedStaffID", staff.userID)
//                                            .whereEqualTo(
//                                                "userProvinceID", supervisor.userProvinceID
//                                            )
                    .get()
                    .await()

            for (document in querySnapshot) {
                val item = document.toObject(AssignedShift::class.java)
                Log.d(
                    TAG,
                    "SupervisorStaffDetail Is current week?: ${
                        isTimeInCurrentMonth(item.assignedShiftDate.toLong())
                    }"
                )

                if (isTimeInCurrentMonth(item.assignedShiftDate.toLong())) {
                    //time is in current week
                    staffAssignedShiftsList.add(item)
                    staffAssignedShifts.value = staffAssignedShiftsList
                    Log.d(TAG, "SupervisorStaffDetail Item: $item")
                    Log.d(
                        TAG,
                        "SupervisorStaffDetail Is current week?: ${
                            isTimeInCurrentMonth(item.assignedShiftDate.toLong())
                        }"
                    )
                }
            }
        }
    }


    Column {
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
                        text = staff.userFirstName.first().toString(),
                        fontWeight = FontWeight.Bold,
                        style = Typography.displaySmall
                    )
                    Text(
                        text = staff.userLastName.first().toString(),
                        fontWeight = FontWeight.Bold,
                        style = Typography.displaySmall
                    )
                }
            }

        }

        val staffName = "${staff.userFirstName} ${staff.userLastName}"
        val dateJoined = getDate(staff.userPasswordChangedDate.toLong(), "dd MMM, yyyy")
        Text(
            text = staffName,
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

        Spacer(modifier = Modifier.height(24.dp))


        Column(
            modifier = Modifier.scrollable(rememberScrollState(), Orientation.Vertical)
        ) {
            Row {
                Text(
                    text = stringResource(id = R.string.contact_title),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.8F)
                )
                Icon(imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1F)
                        .clickable {
                            openDial(staff.userContactNumber, context)
                        })
                Icon(imageVector = Icons.Default.Chat,
                    contentDescription = null,
                    modifier = Modifier
                        .weight(0.1F)
                        .clickable {

                        })
            }
//            Spacer(modifier = Modifier.height(24.dp))


            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )

            ) {


                Text(
                    text = stringResource(id = R.string.contact_address_title),
                    style = Typography.bodySmall,
                    modifier = Modifier.padding(4.dp)
                )

                Text(
                    text = staff.userAddress,
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
                    getProvince(provinceId = staff.userProvinceID, context = context)!!
                val staffDistrictAndProvince =
                    "${staff.userDistrictID}, ${staffProvince.provinceName}"

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
                    text = staff.userEmail,
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
                    text = staff.userContactNumber,
                    style = Typography.bodyLarge,
                    modifier = Modifier
                        .offset(x = 8.dp)
                        .padding(4.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.shift_this_week_title),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.8F)
                )
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            val houseList = mutableListOf<House>()
                            Common.housesCollectionRef
                                .whereEqualTo(
                                    "houseAddingSupervisor",
                                    Common.auth.uid!!
                                )
                                .get()
                                .addOnCompleteListener { housesSnapshot ->
                                    for (document in housesSnapshot.result) {
                                        val item = document.toObject(House::class.java)
                                        houseList.add(item)
                                    }

                                    availableHousesToAssign.value = houseList


                                    val shiftTypeList = mutableListOf<ShiftType>()

                                    Common.shiftCollectionRef
                                        .whereEqualTo(
                                            "shiftTypeOwnerProvinceID",
                                            staff.userProvinceID
                                        )
                                        .get()
                                        .addOnCompleteListener { shiftTypeSnapshot ->
                                            for (document in shiftTypeSnapshot.result) {
                                                val item =
                                                    document.toObject(ShiftType::class.java)
                                                shiftTypeList.add(item)
                                            }

                                            shiftTypes.value = shiftTypeList


                                            val allShiftsList =
                                                mutableListOf<AssignedShift>()

                                            Common.assignedShiftsCollectionRef
                                                .whereEqualTo(
                                                    "assignedSupervisorID",
                                                    auth.uid!!
                                                )
                                                .get()
                                                .addOnCompleteListener { assignedShiftsSnapshot ->
                                                    for (document in assignedShiftsSnapshot.result) {
                                                        val item = document.toObject(
                                                            AssignedShift::class.java
                                                        )

                                                        if (TimeTraveller.isTimeInCurrentMonth(
                                                                item.assignedShiftDate.toLong()
                                                            )
                                                        ) {
                                                            //time is in current month
                                                            allShiftsList.add(item)
                                                            allStaffAssignedShifts.value =
                                                                allShiftsList
                                                        }
                                                    }


                                                    /* check if the assigned shifts of the staff is up to 7 || he has been assigned for every day of the week ☑️
                                                     if he has been for all, show toast or even disable the 'assign' button ☑️
                                                     if he is free...
                                                     show assign staff dialog


                                                     when submitted...
                                                  system will get the date of the assigned shift
                                                  the system will then all assigned shifts where the date assigned is equal to the given date
                                                  the system then gets the ID of that shift
                                                  the system then checks the assigned shift ID (type) of this proposed shift
                                                  if the same, it indicates that the shift has been assigned already
                                                  if the supervisor proceeds to replace the currently assigned person to this new on...
                                                  a. the existing one will be deleted from the assigned shifts table
                                                  b. the previously assigned staff will be notified about the change



                                                  the system checks the list of assigned shifts, if that shift has already been assigned to someone else and the staff is informed*/
                                                    Log.d(
                                                        TAG,
                                                        "SupervisorStaffDetail staffAssignedShifts: ${staffAssignedShifts.value.size}"
                                                    )
                                                    if (staffAssignedShifts.value.size > 6) {

                                                        context.toast("Staff has been assigned for all days this week")


                                                    } else {
                                                        //create the form dialog

                                                        showCustomDialog = !showCustomDialog

                                                        Log.d(
                                                            TAG,
                                                            "SupervisorStaffDetail: Staff has not been assigned for all days this week"
                                                        )
                                                    }
                                                }
                                        }
                                }
                        }
                    }, modifier = Modifier
                        .weight(0.2F)
                        .padding(4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.assign_shift_title),
                        fontWeight = FontWeight.Bold,
                    )
                }

            }


            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 8.dp
                )

            ) {

                if (staffAssignedShifts.value.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(id = R.string.no_shifts_assigned),
                            fontStyle = FontStyle.Italic
                        )
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        items(
                            staffAssignedShifts.value
                        ) { shift ->
                            Row(
                                modifier = Modifier.padding(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = getDate(
                                        shift.assignedShiftDate.toLong(),
                                        "EEE, dd MMM, yyyy"
                                    ),
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .weight(0.5F),
                                )
                                Text(
                                    text = "${
                                        TimeTraveller.getDate(
                                            shift.assignedShiftStartTime.toLong(),
                                            "HH:mm a"
                                        )
                                    } - ${
                                        TimeTraveller.getDate(
                                            shift.assignedShiftStopTime.toLong(),
                                            "HH:mm a"
                                        )
                                    }",
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .weight(0.5F),
                                    textAlign = TextAlign.Center

                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        ButtonComponent(
            buttonText = stringResource(
                id = R.string.report_staff_btn_text,
                staff.userFirstName
            ), onClick = {
                context.toast("Reporting ${staff.userFirstName} ${staff.userLastName}")
            })

        Spacer(modifier = Modifier.height(32.dp))


    }

    if (showCustomDialog) {
        AssignStaffShiftFormDialog(
            staff = staff,
            houses = availableHousesToAssign.value,
            staffAssignedShiftsList = staffAssignedShifts.value,
            shiftTypes = shiftTypes.value,
            allMonthShifts = allStaffAssignedShifts.value,
            coroutineScope = coroutineScope,
            onDismiss = { showCustomDialog = !showCustomDialog },
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignStaffShiftFormDialog(
    staff: UserData,
    houses: List<House>,
    staffAssignedShiftsList: List<AssignedShift>,
    shiftTypes: List<ShiftType>,
    allMonthShifts: List<AssignedShift>,
    onDismiss: () -> Unit,
    coroutineScope: CoroutineScope,
    loadingEvent: (Boolean) -> Unit = {}
) {
    val TAG = "AssignStaffShiftFormDialog"
    val context = LocalContext.current
    var selectedHouse by remember {
        mutableStateOf("")
    }
    var selectedHouseId by remember {
        mutableStateOf("")
    }
    var selectedAssignmentDay by remember {
        mutableStateOf("")
    }
    var selectedAssignmentDayToSave by remember {
        mutableStateOf("")
    }
    var selectedAssignmentType by remember {
        mutableStateOf("")
    }
    var assignmentStartTime by remember { mutableStateOf("") }
    var assignmentStopTime by remember { mutableStateOf("") }

    var assignmentStartTimeToSave by remember {
        mutableStateOf("")
    }
    var assignmentStopTimeToSave by remember {
        mutableStateOf("")
    }

    var assignmentShiftTypes by remember {
        mutableStateOf("")
    }
    var selectedHour by remember { mutableIntStateOf(0) }
    var selectedMinute by remember { mutableIntStateOf(0) }
    var shiftIsWithinDay by remember { mutableStateOf(false) }
    var shiftIsWithinNight by remember { mutableStateOf(false) }
    var shiftIsWithinSleepOver by remember { mutableStateOf(false) }

    var assignmentNecessaryInfo by remember {
        mutableStateOf("")
    }
    var staffIsFreeThatDay by remember {
        mutableStateOf(false)
    }
    var shiftIsFreeForStaff by remember {
        mutableStateOf(false)
    }
    var dayStartTime by remember {
        mutableStateOf("")
    }
    var dayStopTime by remember {
        mutableStateOf("")
    }

    var nightStartTime by remember {
        mutableStateOf("")
    }
    var nightStopTime by remember {
        mutableStateOf("")
    }

    var sleepoverStartTime by remember {
        mutableStateOf("")
    }
    var sleepoverStopTime by remember {
        mutableStateOf("")
    }
    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something
        val supervisorInfo = getUser(auth.uid!!, context)!!
        coroutineScope.launch {
            shiftCollectionRef.whereEqualTo(
                "shiftTypeOwnerProvinceID",
                supervisorInfo.userProvinceID
            ).get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        val item = document.toObject(ShiftType::class.java)
                        when (item.shiftTypeName) {
                            "Day" -> {
                                dayStartTime =
                                    TimeTraveller.convertMillisToHourAndMinute(item.shiftTypeStartTime.toLong())
                                dayStopTime =
                                    TimeTraveller.convertMillisToHourAndMinute(item.shiftTypeEndTime.toLong())

                            }

                            "Night" -> {
                                nightStartTime =
                                    TimeTraveller.convertMillisToHourAndMinute(item.shiftTypeStartTime.toLong())
                                nightStopTime =
                                    TimeTraveller.convertMillisToHourAndMinute(item.shiftTypeEndTime.toLong())

                            }

                            "Sleep Over" -> {
                                sleepoverStartTime =
                                    TimeTraveller.convertMillisToHourAndMinute(item.shiftTypeStartTime.toLong())
                                sleepoverStopTime =
                                    TimeTraveller.convertMillisToHourAndMinute(item.shiftTypeEndTime.toLong())
                            }
                        }
                    }
                }
            }
        }

        isTaskRunning.value = false
    }


    val calendar = Calendar.getInstance()

    // Fetching current year, month and day
    val year = calendar[Calendar.YEAR]
    val month = calendar[Calendar.MONTH]
    val dayOfMonth = calendar[Calendar.DAY_OF_MONTH]

    val currentMonth = Calendar.getInstance()
    currentMonth.set(Calendar.DAY_OF_MONTH, 1) // Set to the first day of the current month

// Set the futureDate to the last day of the current month
    val futureDate = Calendar.getInstance()
    futureDate.time = currentMonth.time
    futureDate.set(Calendar.DAY_OF_MONTH, 30)


    val datePicker = DatePickerDialog(
        context,
        DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

            if (TimeTraveller.isDateSelectable(selectedDate, currentMonth, futureDate)) {
                // The selected date is within the allowed range
                val selectedMillis = selectedDate.timeInMillis
                // Now, 'selectedMillis' contains the time in milliseconds
                selectedAssignmentDayToSave = selectedMillis.toString()
                selectedAssignmentDay =
                    TimeTraveller.getDate(selectedMillis.toString().toLong(), "EEE, dd MMM, yyyy")
            } else {
                // The selected date is not within the allowed range
                context.toast("Invalid date selection")
            }
        },
        year,
        month,
        dayOfMonth
    )

    datePicker.datePicker.minDate = calendar.timeInMillis


    val heightTextFields by remember { mutableStateOf(64.dp) }
    var textFieldsSize by remember { mutableStateOf(Size.Zero) }

    var houseListExpanded by remember {
        mutableStateOf(false)
    }
    var shiftListExpanded by remember {
        mutableStateOf(false)
    }


    val interactionSource = remember {
        MutableInteractionSource()
    }


    /*
    the dialog will be a form of 4 fields
    a. house to assign: which will be fetched from the list of province house ☑️
    b. day to assign: which will pop up either a calendar and enable only free days of that week, or show a list of the available days
    c. the shift type: which will be from the list of  created shift types
    d. any necessary information about the particular shift.
    */

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            //shape = MaterialTheme.shapes.medium,
            shape = RoundedCornerShape(10.dp),
            // modifier = modifier.size(280.dp, 240.dp)
            modifier = Modifier.padding(8.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (isTaskRunning.value) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(Color.Transparent) //will later be converted to background color
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = {
                                houseListExpanded = false

                            }
                        )
                ) {

                    Text(
                        text = stringResource(id = R.string.assign_staff_shift_dialog_title),
                        modifier = Modifier.padding(8.dp),
                        fontSize = 20.sp
                    )

                    //house to assign
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(4.dp)
                            .onGloballyPositioned { coordinates ->
                                textFieldsSize = coordinates.size.toSize()
                            },
                            placeholder = { Text(text = stringResource(id = R.string.select_house_to_assign_placeholder)) },
                            value = selectedHouse, onValueChange = {
                                selectedHouse = it
                                houseListExpanded = true
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
                                IconButton(onClick = { houseListExpanded = !houseListExpanded }) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = "arrow"
                                    )
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = houseListExpanded) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(textFieldsSize.width.dp)
                        ) {
                            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                                if (selectedHouse.isNotEmpty()) {
                                    items(
                                        houses.filter {
                                            it.houseName.lowercase()
                                                .contains(selectedHouse.lowercase())
                                        }//.sortedBy{}
                                    ) {
                                        DropDownListItem(title = it.houseName) { title ->
                                            selectedHouse = title
                                            houseListExpanded = false

                                        }
                                    }
                                } else {
                                    items(houses) {
                                        DropDownListItem(title = it.houseName) { title ->
                                            selectedHouse = title
                                            houseListExpanded = false
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //day to assign
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heightTextFields)
                                .padding(4.dp)
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        // focused
                                        //TimeTraveller.pickADate(context)
                                        datePicker.show()
                                    }
                                },
                            placeholder = { Text(text = stringResource(id = R.string.select_day_to_assign_placeholder)) },
                            value = selectedAssignmentDay,
                            onValueChange = {
                                selectedAssignmentDay = it
                                Log.d(
                                    TAG,
                                    "AssignStaffShiftFormDialog: $selectedAssignmentDayToSave"
                                )
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                        )
                    }


                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(4.dp)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    // focused
                                    TimeTraveller.pickATime(
                                        context,
                                        selectedHour,
                                        selectedMinute
                                    ) { hour, minute ->
                                        selectedHour = hour
                                        selectedMinute = minute
                                        assignmentStartTime = "${String.format("%02d", hour)}:${
                                            String.format(
                                                "%02d",
                                                minute
                                            )
                                        }"
                                        assignmentStartTimeToSave =
                                            TimeTraveller
                                                .calculateHourTimeInMillis(hour, minute)
                                                .toString()

                                        Log.d(
                                            TAG,
                                            "AssignStaffShiftFormDialog: assignmentStartTime = $assignmentStartTime"
                                        )
                                        Log.d(
                                            TAG,
                                            "AssignStaffShiftFormDialog: assignmentStartTimeToSave = $assignmentStartTimeToSave"
                                        )
                                    }
                                }
                            },
                        placeholder = { Text(text = stringResource(id = R.string.shift_start_time_title)) },
                        value = assignmentStartTime,
                        onValueChange = {
                            assignmentStartTime = it
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                    )


                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(4.dp)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    // focused
                                    TimeTraveller.pickATime(
                                        context,
                                        selectedHour,
                                        selectedMinute
                                    ) { hour, minute ->
                                        assignmentStopTime = "${String.format("%02d", hour)}:${
                                            String.format(
                                                "%02d",
                                                minute
                                            )
                                        }"
                                        assignmentStopTimeToSave =
                                            TimeTraveller
                                                .calculateHourTimeInMillis(hour, minute)
                                                .toString()
                                        Log.d(
                                            TAG,
                                            "dayStartTime: $dayStartTime \ndayStopTime: $dayStopTime\nassignmentStartTime: $assignmentStartTime\nassignmentStopTime: $assignmentStopTime"
                                        )
                                        if (TimeTraveller.isTimeInRange(
                                                dayStartTime,
                                                dayStopTime,
                                                assignmentStartTime,
                                                assignmentStopTime
                                            )
                                        ) {
                                            shiftIsWithinDay = true
                                            assignmentShiftTypes += "D, "
                                            //append Day to to the string
                                        }
                                        Log.d(TAG, "shiftIsWithinDay: $shiftIsWithinDay")
                                        if (TimeTraveller.isTimeInRange(
                                                nightStartTime,
                                                nightStopTime,
                                                assignmentStartTime,
                                                assignmentStopTime
                                            )
                                        ) {
                                            shiftIsWithinNight = true
                                            assignmentShiftTypes += "N, "
                                            //append Night to the string
                                        }

                                        Log.d(TAG, "shiftIsWithinNight: $shiftIsWithinNight")
                                        if (TimeTraveller.isTimeInRange(
                                                sleepoverStartTime,
                                                sleepoverStopTime,
                                                assignmentStartTime,
                                                assignmentStopTime
                                            )
                                        ) {
                                            shiftIsWithinSleepOver = true
                                            assignmentShiftTypes += "SO, "
                                            //append Sleepover to the string
                                        }

                                        Log.d(
                                            TAG,
                                            "shiftIsWithinSleepOver: $shiftIsWithinSleepOver"
                                        )

                                        Log.d(
                                            TAG,
                                            "AssignStaffShiftFormDialog: assignmentStopTime = $assignmentStopTime"
                                        )
                                        Log.d(
                                            TAG,
                                            "AssignStaffShiftFormDialog: assignmentStopTimeToSave = $assignmentStopTimeToSave"
                                        )
                                    }
                                }
                            },
                        enabled = assignmentStartTime != "",
                        placeholder = { Text(text = stringResource(id = R.string.shift_stop_time_title)) },
                        value = assignmentStopTime,
                        onValueChange = {
                            assignmentStopTime = it

                            /*
                                 shiftIsWithinDay = isTimeWithinDayRange

                                 shiftIsWithinNight = isTimeWithinNightRange

                                 shiftIsWithinSleepOver = isTimeWithinNightRange
 */
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                    )


                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.33f)
                        ) {
                            Checkbox(
                                enabled = false,
                                checked = shiftIsWithinDay,
                                onCheckedChange = { isChecked -> shiftIsWithinDay = isChecked },
                                modifier = Modifier.clip(RoundedCornerShape(8.dp))
                            )
                            Text(text = "Day")
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.33f)
                        ) {
                            Checkbox(
                                enabled = false,
                                checked = shiftIsWithinNight,
                                onCheckedChange = { isChecked -> shiftIsWithinNight = isChecked },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Text(text = "Night")
                        }
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(0.33f)
                        ) {
                            Checkbox(
                                enabled = false,
                                checked = shiftIsWithinSleepOver,
                                onCheckedChange = { isChecked ->
                                    shiftIsWithinSleepOver = isChecked
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Text(text = "Sleep Over")
                        }

                    }
                    //any necessary information

                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(4.dp),
                        placeholder = { Text(text = stringResource(id = R.string.necessary_info_title)) },
                        value = assignmentNecessaryInfo,
                        onValueChange = {
                            assignmentNecessaryInfo = it
                        },
                        textStyle = TextStyle(
                            color = Color.Black,
                            fontSize = 16.sp
                        ),
                    )


                    ButtonComponent(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        buttonText = stringResource(id = R.string.assign_shift_title),
                        enabled = selectedHouse.isNotBlank() && selectedAssignmentDay.isNotBlank() && assignmentStartTimeToSave.isNotBlank() && assignmentStopTimeToSave.isNotBlank(),
                        onClick = {
                            coroutineScope.launch {
                                housesCollectionRef.whereEqualTo(
                                    "houseProvince",
                                    staff.userProvinceID
                                ).get().addOnCompleteListener {
                                    var selectedHouseID = ""
                                    for (document in it.result) {
                                        val item = document.toObject(House::class.java)
                                        if (item.houseName == selectedHouse)
                                            selectedHouseId = item.houseID
                                    }


                                    //if (staffIsFreeThatDay && shiftIsFreeForStaff) {
                                    val shiftUID = UUID.randomUUID().toString()
                                    //upload assigned shift
                                    //get title of shift type
                                    coroutineScope.launch {
                                        shiftCollectionRef.whereEqualTo(
                                            "shiftTypeOwnerProvinceID",
                                            staff.userProvinceID
                                        ).get().addOnCompleteListener {
                                            var selectedShiftTypeID = ""
                                            for (document in it.result) {
                                                val item =
                                                    document.toObject(ShiftType::class.java)
                                                if (item.shiftTypeName == selectedAssignmentType)
                                                    selectedShiftTypeID = item.shiftTypeID

                                            }
                                            val assignedShift = AssignedShift(
                                                assignedShiftID = shiftUID,
                                                assignedHouseID = selectedHouseId,
                                                assignedStaffID = staff.userID,
                                                assignedSupervisorID = auth.uid!!,
                                                assignedShiftStartTime = assignmentStartTimeToSave,
                                                assignedShiftStopTime = assignmentStopTimeToSave,
                                                assignedShiftTypes = assignmentShiftTypes,
                                                assignedShiftDate = selectedAssignmentDayToSave,
                                                assignedShiftDateAdded = System.currentTimeMillis()
                                                    .toString(),
                                                assignedShiftNecessaryInformation = assignmentNecessaryInfo,
                                                assignedShiftStatus = SHIFT_ACTIVE
                                            )
                                            coroutineScope.launch {
                                                isTaskRunning.value = true
                                                assignedShiftsCollectionRef.document(
                                                    shiftUID
                                                ).set(assignedShift).addOnCompleteListener {
                                                    //add notification
                                                    val notification = Notification(
                                                        notificationID = System.currentTimeMillis()
                                                            .toString(),
                                                        notificationType = NOTIFICATION_TYPE_SHIFT_ASSIGNMENT,
                                                        notificationSenderID = auth.uid!!,
                                                        notificationReceiverID = staff.userID,
                                                        notificationTitle = "New shift assignment!",
                                                        notificationProvinceID = getUser(
                                                            auth.uid!!,
                                                            context
                                                        )!!.userProvinceID,
                                                        notificationMessage = "You have been assigned to $selectedHouse on $selectedAssignmentDay for $assignmentStartTime -$assignmentStopTime ($assignmentShiftTypes)", //use string resource
                                                        notificationSentDate =
                                                        System.currentTimeMillis()
                                                            .toString(),
                                                    )

                                                    notificationsCollectionRef.document(
                                                        System.currentTimeMillis().toString()
                                                    ).set(notification).addOnCompleteListener {
                                                        userCollectionRef.document(staff.userID)
                                                            .update(
                                                                "userAssignedHouse",
                                                                selectedHouseID
                                                            ).addOnCompleteListener {
                                                            isTaskRunning.value = false
                                                            onDismiss()
                                                        }

                                                    }


                                                }.addOnFailureListener { e ->
                                                    isTaskRunning.value = false
                                                    context.toast(e.message.toString())
                                                }
                                            }


                                        }
                                    }

                                    //}

                                }
                            }
                            /*ensure that the staff has not already been assigned a shift on that day ☑️
                            * ensure that no other staff has been assigned that particular day and time, to the same house ☑️*/
                        }
                    )
                }
            }

        }
    }
}

@Composable
fun DropDownListItem(title: String, onSelect: (String) -> Unit) {

    Row(modifier = Modifier
        .fillMaxWidth()
        .clickable { onSelect(title) }
        .padding(10.dp)) {
        Text(text = title, fontSize = 16.sp)
    }

}
