package org.devstrike.app.medcareaidscheduler.ui.auth

import android.util.Log
import android.util.Patterns
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.AuthHeader
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.UserData
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.Common.userCollectionRef
import org.devstrike.app.medcareaidscheduler.utils.toast

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPassword(navController: NavHostController, userEmail: String? = null) {
    val context = LocalContext.current
    val email: MutableState<String> = remember { mutableStateOf("") }

    val isTaskRunning = remember { mutableStateOf(false) }
    // Show the progress bar while the task is running

    val TAG = "ResetPassword"
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (isTaskRunning.value) {

            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        }
    }


    Column {

        AuthHeader(
            title = stringResource(id = R.string.forgot_password_text),
            subtitle = stringResource(id = R.string.forgot_password_sub_text)
        )

        Spacer(modifier = Modifier.height(124.dp))

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


        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(
            onClick = {

                if (!Patterns.EMAIL_ADDRESS.matcher(email.value)
                        .matches() || email.value.isEmpty()
                ) {
                    context.toast("Invalid Email")
                } else {
                    CoroutineScope(Dispatchers.IO).launch {
                        isTaskRunning.value = true

                        auth.sendPasswordResetEmail(email.value)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    userCollectionRef.document(auth.uid!!)
                                        .update("userChangedPassword", true).addOnCompleteListener {
                                        isTaskRunning.value = false
                                        // Password reset email sent successfully
                                        context.toast("Password reset email sent")
                                        navController.navigate(Screen.SignIn.route)
                                        //navController.popBackStack()


                                    }.addOnFailureListener { e ->
                                        isTaskRunning.value = false
                                        context.toast(e.message.toString())
                                    }
                                } else {
                                    isTaskRunning.value = false
                                    // Error occurred while sending password reset email
                                    context.toast("Failed to send password reset email")
                                }
                                return@addOnCompleteListener
                            }

                    }
                }
            },
            buttonText = stringResource(id = R.string.reset_password_text),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )


    }

}