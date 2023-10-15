package org.devstrike.app.medcareaidscheduler.ui.auth

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.AuthHeader
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.toast

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPassword(navController: NavHostController) {
    val context = LocalContext.current
    val email: MutableState<String> = remember { mutableStateOf("") }

    Column {

        AuthHeader(title = stringResource(id = R.string.forgot_password_text), subtitle = stringResource(id = R.string.forgot_password_sub_text))

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
            leadingIcon = R.drawable.ic_email
        )


        Spacer(modifier = Modifier.height(16.dp))

        ButtonComponent(onClick = {
            Log.d("TAG", "SignIn: ")
        }, buttonText = stringResource(id = R.string.reset_password_text))



    }

}