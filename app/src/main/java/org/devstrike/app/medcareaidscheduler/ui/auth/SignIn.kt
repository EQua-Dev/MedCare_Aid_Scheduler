package org.devstrike.app.medcareaidscheduler.ui.auth

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.AuthHeader
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.utils.Common.PASSWORD_INPUT_TYPE
import org.devstrike.app.medcareaidscheduler.utils.toast

//@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SignIn(navController: NavHostController) {
    val context = LocalContext.current
    val password: MutableState<String> = remember { mutableStateOf("") }
    val email: MutableState<String> = remember { mutableStateOf("") }

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
            leadingIcon = R.drawable.ic_email
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
                    context.toast("${password.value} ${email.value}")
                }
            ),
            inputType = PASSWORD_INPUT_TYPE,
            leadingIcon = R.drawable.ic_lock
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

        ButtonComponent(onClick = {
            Log.d("TAG", "SignIn: ")
            navController.navigate(Screen.SupervisorLanding.route)
        }, buttonText = stringResource(id = R.string.sign_in_text))


    }
}