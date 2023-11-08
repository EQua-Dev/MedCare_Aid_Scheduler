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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
import org.devstrike.app.medcareaidscheduler.utils.Common.assignedShiftsCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.housesCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.notificationsCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.Common.shiftCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.getDate
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getProvince
import org.devstrike.app.medcareaidscheduler.utils.getShiftType
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentWeek
import org.devstrike.app.medcareaidscheduler.utils.toast
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
                        isTimeInCurrentWeek(item.assignedShiftDate.toLong())
                    }"
                )

                if (isTimeInCurrentWeek(item.assignedShiftDate.toLong())) {
                    //time is in current week
                    staffAssignedShiftsList.add(item)
                    staffAssignedShifts.value = staffAssignedShiftsList
                    Log.d(TAG, "SupervisorStaffDetail Item: $item")
                    Log.d(
                        TAG,
                        "SupervisorStaffDetail Is current week?: ${
                            isTimeInCurrentWeek(item.assignedShiftDate.toLong())
                        }"
                    )
                }
            }

            //staff.value = staffList
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
            modifier = Modifier.verticalScroll(
                rememberScrollState()
            )
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
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary),
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

            Row {
                Text(
                    text = stringResource(id = R.string.shift_this_week_title),
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.8F)
                )
                Text(
                    text = stringResource(id = R.string.assign_shift_title),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(4.dp)
                        .weight(0.2F)
                        .clickable {
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

                                                            if (isTimeInCurrentMonth(item.assignedShiftDate.toLong())) {
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

                                                            context.toast("SStaff has been assigned for all days this week")


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
                        }
                )
            }


            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.secondary),
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
                            Row(modifier = Modifier.padding(4.dp)) {
                                Text(
                                    text = getDate(
                                        shift.assignedShiftDate.toLong(),
                                        "EEE, dd MMM, yyyy"
                                    ), modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(0.5F),
                                )
                                Text(
                                    text = getShiftType(
                                        shift.assignedShiftTypeID,
                                        context
                                    )!!.shiftTypeName,
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .fillMaxWidth(0.5F),
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
    var selectedAssignmentType by remember {
        mutableStateOf("")
    }

    var assignmentNecessaryInfo by remember {
        mutableStateOf("")
    }

    var staffIsFreeThatDay by remember {
        mutableStateOf(false)
    }
    var shiftIsFreeForStaff by remember {
        mutableStateOf(false)
    }
    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

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
    futureDate.set(Calendar.YEAR, currentMonth.get(Calendar.YEAR))
    futureDate.set(Calendar.MONTH, currentMonth.get(Calendar.MONTH))
    futureDate.set(Calendar.DAY_OF_MONTH, currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH))

    val datePicker = DatePickerDialog(
        context,
        DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDayOfMonth ->
            val selectedDate = Calendar.getInstance()
            selectedDate.set(selectedYear, selectedMonth, selectedDayOfMonth)

            if (isDateSelectable(selectedDate, currentMonth, futureDate)) {
                // The selected date is within the allowed range
                val selectedMillis = selectedDate.timeInMillis
                // Now, 'selectedMillis' contains the time in milliseconds
                selectedAssignmentDay = selectedMillis.toString()
            } else {
                // The selected date is not within the allowed range
                context.toast("Invalid date selection")
            }
        },
        year,
        month,
        dayOfMonth
    )


// Show the DatePickerDialog
    datePicker.datePicker.maxDate = futureDate.timeInMillis
    datePicker.datePicker.minDate = currentMonth.timeInMillis


    /*
    when supervisor selects the date, only this month's dates will be clickable, future ones
    when supervisor selects a date, the system checks if the staff already has a shift that day
     */

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
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
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
                        TextField(modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(4.dp)
                            .border(
                                width = 1.8.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
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
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heightTextFields)
                                .padding(4.dp)
                                .border(
                                    width = 1.8.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                )
                                .clickable {
                                    datePicker.show()
                                }
                                .onFocusChanged {
                                    if (it.isFocused) {
                                        // focused
                                        datePicker.show()
                                    }
                                },
                            placeholder = { Text(text = stringResource(id = R.string.select_day_to_assign_placeholder)) },
                            value = selectedAssignmentDay,
                            onValueChange = {
                                selectedAssignmentDay = it
                            },
                            textStyle = TextStyle(
                                color = Color.Black,
                                fontSize = 16.sp
                            ),
                            singleLine = true,
                        )
                    }

                    //shift type to assign
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(modifier = Modifier
                            .fillMaxWidth()
                            .height(heightTextFields)
                            .padding(4.dp)
                            .border(
                                width = 1.8.dp,
                                color = Color.Black,
                                shape = RoundedCornerShape(15.dp)
                            )
                            .onGloballyPositioned { coordinates ->
                                textFieldsSize = coordinates.size.toSize()
                            },
                            placeholder = { Text(text = stringResource(id = R.string.select_shift_to_assign_placeholder)) },
                            value = selectedAssignmentType, onValueChange = {
                                selectedAssignmentType = it
                                shiftListExpanded = true
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
                                IconButton(onClick = { shiftListExpanded = !shiftListExpanded }) {
                                    Icon(
                                        imageVector = Icons.Rounded.ArrowDropDown,
                                        contentDescription = "arrow"
                                    )
                                }
                            }
                        )
                    }

                    AnimatedVisibility(visible = shiftListExpanded) {
                        Card(
                            modifier = Modifier
                                .padding(horizontal = 5.dp)
                                .width(textFieldsSize.width.dp)
                        ) {
                            LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                                if (selectedAssignmentType.isNotEmpty()) {
                                    items(
                                        shiftTypes.filter {
                                            it.shiftTypeName.lowercase()
                                                .contains(selectedAssignmentType.lowercase())
                                        }//.sortedBy{}
                                    ) {
                                        DropDownListItem(title = it.shiftTypeName) { title ->
                                            selectedAssignmentType = title
                                            shiftListExpanded = false

                                        }
                                    }
                                } else {
                                    items(shiftTypes) {
                                        DropDownListItem(title = it.shiftTypeName) { title ->
                                            selectedAssignmentType = title
                                            shiftListExpanded = false
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //any necessary information
                    Row(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(heightTextFields)
                                .padding(4.dp)
                                .border(
                                    width = 1.8.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(15.dp)
                                ),
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
                    }

                    ButtonComponent(
                        buttonText = stringResource(id = R.string.assign_shift_title),
                        enabled = selectedHouse.isNotBlank() && selectedAssignmentDay.isNotBlank() && selectedAssignmentType.isNotBlank(),
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

                                    for (staffShift in staffAssignedShiftsList) {
                                        staffAssignedShiftsList.forEach { staffShift ->
                                            //val assignedHouseName = getHouse(it.assignedHouseID, context)!!.houseName
                                            //check if the staff has been assigned to a house that day already
                                            staffIsFreeThatDay = if (getDate(
                                                    staffShift.assignedShiftDate.toLong(),
                                                    "EEE, dd MMM, yyyy"
                                                ) != getDate(
                                                    selectedAssignmentDay.toLong(),
                                                    "EEE, dd MMM, yyyy"
                                                )
                                            ) {
                                                true
                                            } else {
                                                context.toast("Staff already assigned for this day")
                                                //false
                                                return@addOnCompleteListener
                                            }
                                        }
                                    }
                                    allMonthShifts.forEach { otherShift ->
                                        //check if that house has already been assigned for that day
                                        val shiftHasBeenAssignedToOtherStaff = getDate(
                                            otherShift.assignedShiftDate.toLong(),
                                            "EEE, dd MMM, yyyy"
                                        ) == getDate(
                                            selectedAssignmentDay.toLong(),
                                            "EEE, dd MMM, yyyy"
                                        ) && getHouse(
                                            otherShift.assignedHouseID,
                                            context
                                        )!!.houseName == selectedHouse && getShiftType(
                                            otherShift.assignedShiftTypeID,
                                            context
                                        )!!.shiftTypeName == selectedAssignmentType
                                        shiftIsFreeForStaff =
                                            if (!shiftHasBeenAssignedToOtherStaff) {

                                                true
                                            } else {
                                                context.toast("shift has been assigned to another staff already")
                                                //false
                                                return@addOnCompleteListener

                                            }
                                    }
                                    Log.d(
                                        TAG,
                                        "AssignStaffShiftFormDialog: staffIsFreeThatDay => $staffIsFreeThatDay\nshiftIsFreeForStaff => $shiftIsFreeForStaff"
                                    )
                                    if (staffIsFreeThatDay && shiftIsFreeForStaff) {
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
                                                    assignedShiftTypeID = selectedShiftTypeID,
                                                    assignedShiftDate = selectedAssignmentDay,
                                                    assignedShiftDateAdded = System.currentTimeMillis()
                                                        .toString(),
                                                    assignedShiftNecessaryInformation = assignmentNecessaryInfo,
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
                                                            notificationProvinceID = getUser(auth.uid!!, context)!!.userProvinceID,
                                                            notificationMessage = "You have been assigned to $selectedHouse on ${
                                                                getDate(
                                                                    selectedAssignmentDay.toLong(),
                                                                    "EEE, dd MMM, yyyy"
                                                                )
                                                            } for $selectedAssignmentType",
                                                            notificationSentDate = System.currentTimeMillis()
                                                                .toString(),
                                                        )

                                                        notificationsCollectionRef.document(
                                                            System.currentTimeMillis().toString()
                                                        ).set(notification).addOnCompleteListener {
                                                            isTaskRunning.value = false
                                                            onDismiss()
                                                        }

                                                    }.addOnFailureListener { e ->
                                                        isTaskRunning.value = false
                                                        context.toast(e.message.toString())
                                                    }
                                                }


                                            }
                                        }

                                    }

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

fun isDateSelectable(
    selectedDate: Calendar,
    currentMonth: Calendar,
    futureDate: Calendar
): Boolean {
    return selectedDate >= currentMonth && selectedDate <= futureDate
}