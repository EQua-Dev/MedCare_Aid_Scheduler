/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.auth

import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.geometry.Size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.AuthHeader
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.Province
import org.devstrike.app.medcareaidscheduler.data.ShiftType
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_staff.DropDownListItem
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.userCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.SessionManager
import org.devstrike.app.medcareaidscheduler.utils.getUser
import org.devstrike.app.medcareaidscheduler.utils.isTimeInCurrentMonth
import org.devstrike.app.medcareaidscheduler.utils.toast

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateNewUser(navController: NavHostController) {

    //user first name
    //user last name
    //user email
    //password is automatically test1234
    //province (from the registered provinces)
    //user district
    //user gender
    //user role
    //contact number
    //

    val context = LocalContext.current
    val password: MutableState<String> = remember { mutableStateOf("") }
    val email: MutableState<String> = remember { mutableStateOf("") }
    val firstName: MutableState<String> = remember { mutableStateOf("") }
    val lastName: MutableState<String> = remember { mutableStateOf("") }
    val district: MutableState<String> = remember { mutableStateOf("") }
    val address: MutableState<String> = remember { mutableStateOf("") }
    val gender: MutableState<String> = remember { mutableStateOf("") }
    val role: MutableState<String> = remember { mutableStateOf("") }
    val contactNumber: MutableState<String> = remember { mutableStateOf("") }
    val provinceID: MutableState<String> = remember { mutableStateOf("") }

    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running
    val coroutineScope = rememberCoroutineScope()


    // state of the gender menu
    var genderExpanded by remember {
        mutableStateOf(false)
    }
    // state of the role menu
    var roleExpanded by remember {
        mutableStateOf(false)
    }
    // state of the province menu
    var provinceExpanded by remember {
        mutableStateOf(false)
    }
    var textFilledSize by remember {
        mutableStateOf(Size.Zero)
    }
    val genderIcon = if (genderExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val genderListItems = stringArrayResource(id = R.array.genders)
    val heightTextFields by remember { mutableStateOf(64.dp) }
    var textFieldsSize by remember { mutableStateOf(Size.Zero) }


    val roleIcon = if (roleExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }
    val roleListItems = stringArrayResource(id = R.array.roles)
    val availableProvinceToAssign = remember {
        mutableStateOf(listOf<Province>())
    }
    // Perform the task
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isTaskRunning.value = true
            val provinceList = mutableListOf<Province>()
            Common.provinceCollectionRef
                .get()
                .addOnCompleteListener { provinceSnapshot ->
                    for (document in provinceSnapshot.result) {
                        val item = document.toObject(Province::class.java)
                        provinceList.add(item)
                    }

                    availableProvinceToAssign.value = provinceList
                    isTaskRunning.value = false

                }
        }

    }

    val TAG = "Create User"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isTaskRunning.value) {
            CircularProgressIndicator(modifier = Modifier.size(48.dp))
        }
    }

    val sessionManager = SessionManager(context)
    LazyColumn {
        item {
            AuthHeader(
                title = stringResource(id = R.string.creat_user_text),
                subtitle = stringResource(id = R.string.create_user_sub_text)
            )

            OutlinedTextField(
                value = firstName.value,
                onValueChange = { firstName.value = it },
                label = { Text(text = stringResource(id = R.string.first_name_title)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .padding(4.dp)
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    }
            )

            OutlinedTextField(
                value = lastName.value,
                onValueChange = { lastName.value = it },
                label = { Text(text = stringResource(id = R.string.last_name_title)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    }
            )

            OutlinedTextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text(text = stringResource(id = R.string.email_title)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    }
            )

            OutlinedTextField(value = gender.value!!,
                onValueChange = { gender.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    },
                label = { Text(text = stringResource(id = R.string.gender)) },
                trailingIcon = {
                    Icon(
                        genderIcon,
                        "",
                        Modifier.clickable { genderExpanded = !genderExpanded })
                }
            )

            AnimatedVisibility(visible = genderExpanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFilledSize.width.dp)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                        if (gender.value.isNotEmpty()) {
                            items(
                                genderListItems.filter { gend ->
                                    gend.lowercase()
                                        .contains(gender.value.lowercase())
                                }.sorted()
                            ) { gend ->
                                DropDownListItem(title = gend) { title ->
                                    gender.value = title
                                    genderExpanded = false

                                }
                            }
                        } else {
                            items(genderListItems) { loc ->
                                DropDownListItem(title = loc) { title ->
                                    gender.value = title
                                    genderExpanded = false
                                }
                            }
                        }
                    }
                }
            }
            OutlinedTextField(value = role.value!!,
                onValueChange = { role.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    },
                label = { Text(text = stringResource(id = R.string.role)) },
                trailingIcon = {
                    Icon(
                        roleIcon,
                        "",
                        Modifier.clickable { roleExpanded = !roleExpanded })
                }
            )

            AnimatedVisibility(visible = roleExpanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFilledSize.width.dp)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                        if (role.value.isNotEmpty()) {
                            items(
                                roleListItems.filter { gend ->
                                    gend.lowercase()
                                        .contains(role.value.lowercase())
                                }.sorted()
                            ) { gend ->
                                DropDownListItem(title = gend) { title ->
                                    role.value = title
                                    roleExpanded = false

                                }
                            }
                        } else {
                            items(roleListItems) { loc ->
                                DropDownListItem(title = loc) { title ->
                                    role.value = title
                                    roleExpanded = false
                                }
                            }
                        }
                    }
                }
            }


            OutlinedTextField(
                value = address.value,
                onValueChange = { address.value = it },
                label = { Text(text = stringResource(id = R.string.new_user_address_title)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    }
            )


            OutlinedTextField(
                value = district.value,
                onValueChange = { district.value = it },
                label = { Text(text = stringResource(id = R.string.new_user_district_title)) },
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .onGloballyPositioned { coordinates ->
                        textFilledSize = coordinates.size.toSize()
                    }
            )


            OutlinedTextField(
                value = contactNumber.value,
                onValueChange = {
                    contactNumber.value = it
                },
                enabled = true,
                singleLine = true,

                label = { Text(text = stringResource(id = R.string.new_user_contact_number_title)) },
                placeholder = { Text(text = stringResource(id = R.string.new_user_contact_number_title)) },

                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = true,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
            )
            //house to assign
            OutlinedTextField(modifier = Modifier
                .fillMaxWidth()
                .height(heightTextFields)
                .padding(8.dp)
                .onGloballyPositioned { coordinates ->
                    textFieldsSize = coordinates.size.toSize()
                },
                placeholder = { Text(text = stringResource(id = R.string.select_province_placeholder)) },
                value = provinceID.value, onValueChange = {
                    provinceID.value = it
                    provinceExpanded = true
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                singleLine = true,
                trailingIcon = {
                    IconButton(onClick = {
                        provinceExpanded = !provinceExpanded
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowDropDown,
                            contentDescription = "arrow"
                        )
                    }
                }
            )

            AnimatedVisibility(visible = provinceExpanded) {

                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldsSize.width.dp)
                ) {
                    LazyColumn(modifier = Modifier.heightIn(max = 150.dp)) {
                        if (provinceID.value.isNotEmpty()) {
                            items(
                                availableProvinceToAssign.value.filter {
                                    it.provinceName.lowercase()
                                        .contains(provinceID.value.lowercase())
                                }//.sortedBy{}
                            ) {
                                DropDownListItem(title = it.provinceName) { title ->
                                    provinceID.value = title
                                    provinceExpanded = false
                                }
                            }
                        } else {
                            items(availableProvinceToAssign.value) {
                                DropDownListItem(title = it.provinceName) { title ->
                                    provinceID.value = title
                                    provinceExpanded = false
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ButtonComponent(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                onClick = {
                    if (firstName.value == ""){
                        context.toast("First name is required")
                    }
                    if (lastName.value == ""){
                        context.toast("Last name is required")
                    }
                    if (!Patterns.EMAIL_ADDRESS.matcher(email.value).matches()){
                        context.toast("Valid email is required")
                    }
                    if (contactNumber.value.length != 10){
                        context.toast("Valid contact number is required")
                    }
                    if (address.value == ""){
                        context.toast("Address is required")
                    }
                    if (district.value == ""){
                        context.toast("District is required")
                    }
                    if (gender.value == ""){
                        context.toast("Gender is required")
                    }
                    if (role.value == ""){
                        context.toast("Role is required")
                    }
                    if (provinceID.value == ""){
                        context.toast("Province is required")
                    }else{
                        var selectedProvinceID = ""
                        for (province in availableProvinceToAssign.value){
                            if (province.provinceName == provinceID.value){
                                selectedProvinceID = province.provinceID
                                break
                            }
                        }
                        val newUser = UserData(
                            userRole = role.value,
                                    userFirstName = firstName.value,
                                    userLastName = lastName.value,
                                    userDistrictID = district.value,
                                    userContactNumber = contactNumber.value,
                                    userEmail = email.value,
                                    userAddress = address.value,
                                    userGender = gender.value,
                                    userProvinceID = selectedProvinceID,
                        )
                        createUser(newUser, context, onSuccess = {
                            isTaskRunning.value = false
                            coroutineScope.launch {
                                try {
                                    withContext(Dispatchers.Main) {
                                        //nav back to sign in
                                        navController.navigate(Screen.SignIn.route)
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        context.toast(e.message.toString())

                                    }
                                }
                            }

                        }, isLoading = {
                            isTaskRunning.value = true
                        }, onFailure = { e ->
                            isTaskRunning.value = false
                            context.toast(e)
                        }, coroutineScope)
                    }

                },
                buttonText = stringResource(id = R.string.create_user_text)
            )

        }


    }
}

fun createUser(newUser: UserData, context: Context, onSuccess:() -> Unit, isLoading: () -> Unit, onFailure: (error: String) -> Unit, coroutineScope: CoroutineScope) {
        val defaultPassword = "test1234"

    coroutineScope.launch {
            isLoading()
            newUser.userEmail.let { auth.createUserWithEmailAndPassword(it, defaultPassword) }
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        //login success
                        newUser.userID = auth.uid!!
                        userCollectionRef.document(auth.uid!!).set(newUser).addOnCompleteListener { 
                            if (it.isSuccessful){
                                onSuccess()
                            }else{
                                onFailure(it.exception?.message.toString())
                            }
                        }.addOnFailureListener {
                            onFailure(it.message.toString())
                        }
                    } else {
                        onFailure(it.exception?.message.toString())
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e.message.toString())
                }
        }
}
