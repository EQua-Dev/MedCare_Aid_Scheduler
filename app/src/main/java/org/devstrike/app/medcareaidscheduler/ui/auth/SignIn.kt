package org.devstrike.app.medcareaidscheduler.ui.auth

import android.content.Context
import android.util.Log
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.AuthHeader
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.utils.Common.PASSWORD_INPUT_TYPE
import org.devstrike.app.medcareaidscheduler.utils.Common.STAFF_ROLE
import org.devstrike.app.medcareaidscheduler.utils.Common.SUPERVISOR_ROLE
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.userCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.SessionManager
import org.devstrike.app.medcareaidscheduler.utils.toast

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SignIn(navController: NavHostController) {
    val context = LocalContext.current
    val password: MutableState<String> = remember { mutableStateOf("") }
    val email: MutableState<String> = remember { mutableStateOf("") }

    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running


    // Perform the task
    LaunchedEffect(Unit) {
        isTaskRunning.value = true

        // Do something

        isTaskRunning.value = false
    }

    val TAG = "SignIn"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isTaskRunning.value) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }

    val sessionManager = SessionManager(context)
    Column {

        AuthHeader(
            title = stringResource(id = R.string.sign_in_text),
            subtitle = stringResource(id = R.string.sign_in_sub_text)
        )

        Spacer(modifier = Modifier.height(64.dp))

        TextFieldComponent(
            value = email.value,
            onValueChange = { email.value = it },
            label = "Email",
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = false,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            inputType = "Email",
            leadingIcon = R.drawable.ic_email,
            modifier = Modifier.padding(8.dp)
        )

        TextFieldComponent(
            value = password.value,
            onValueChange = { password.value = it },
            label = "Password",
            keyboardOptions = KeyboardOptions.Default.copy(
                autoCorrect = false,
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    //context.toast("${password.value} ${email.value}")
                }
            ),
            inputType = PASSWORD_INPUT_TYPE,
            leadingIcon = R.drawable.ic_lock,
            modifier = Modifier.padding(8.dp)

        )
        Text(
            text = stringResource(id = R.string.forgot_password_text),
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .clickable {
                    //nav to forgot password
                    context.toast("Nav To Forgot Password")
                    navController.navigate(Screen.ForgotPassword.route)

                },
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(enabled = email.value.isNotBlank() && password.value.isNotBlank(), onClick = {
            signInUser(email.value, password.value, context, onSuccess = {
                isTaskRunning.value = false
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        withContext(Dispatchers.Main) {

                            val getUser = getUser(auth.uid!!, context)

                            if (getUser?.userChangedPassword!!){
                                when (getUser.userRole) {
                                    STAFF_ROLE -> navController.navigate(Screen.StaffLanding.route)
                                    SUPERVISOR_ROLE -> navController.navigate(Screen.SupervisorLanding.route)
                                    else -> context.toast("User Invalid")
                                }
                            }else {
                                navController.navigate(Screen.ForgotPassword.route)
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                                    context.toast(e.message.toString())

                        }
                    }
                }

            }, isLoading = {
                isTaskRunning.value = true
            }, onFailure = {e ->
                isTaskRunning.value = false
                context.toast(e)
            })

    }, buttonText = stringResource(id = R.string.sign_in_text))


}
}

fun signInUser(email: String, password: String, context: Context, onSuccess:() -> Unit, isLoading: () -> Unit, onFailure: (error: String) -> Unit) {

    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        context.toast("Invalid Email or Password")
    }else if (email.isEmpty() || password.isEmpty()){
        context.toast("Invalid Email or Password")
    }else{

        CoroutineScope(Dispatchers.IO).launch {
            isLoading()
            email.let { auth.signInWithEmailAndPassword(it, password) }
                .addOnCompleteListener { it ->
                    if (it.isSuccessful) {
                        onSuccess()
                        //login success
                    } else {
                        onFailure(it.exception?.message.toString())
                    }
                }
                .addOnFailureListener { e ->
                    onFailure(e.message.toString())
                }
        }
    }
}

private fun getUser(userId: String, context: Context): UserData? {
    val deferred = CoroutineScope(Dispatchers.IO).async {
        try {
            val snapshot = userCollectionRef.document(userId).get().await()
            if (snapshot.exists()) {
                return@async snapshot.toObject(UserData::class.java)
            } else {
                return@async null
            }
        } catch (e: Exception) {
            Handler(Looper.getMainLooper()).post {
                context.toast(e.message.toString())
            }
            return@async null
        }
    }

    val clientUser = runBlocking { deferred.await() }

    return clientUser
}