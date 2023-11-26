/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.utils.Common.PASSWORD_INPUT_TYPE

@OptIn(ExperimentalMaterial3Api::class)
@Preview (showBackground = true, showSystemUi = true)
@Composable
fun TextFieldComponent(
    value: String = "",
    onValueChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    label: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    leadingIcon: Int? = null,
    inputType: String = "",
    enabled: Boolean = true,
    singleLine: Boolean? = null
) {

    var isPasswordVisible by remember { mutableStateOf(true) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine ?: true,
        label = { Text(text = label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = RoundedCornerShape(4.dp),
        visualTransformation = if (isPasswordVisible && inputType == PASSWORD_INPUT_TYPE)  PasswordVisualTransformation() else VisualTransformation.None,
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color.Gray,
            unfocusedBorderColor = Color.LightGray
        ),enabled = enabled,
        trailingIcon = {
            if (inputType == PASSWORD_INPUT_TYPE) {
                IconButton(
                    onClick = {
                        isPasswordVisible = !isPasswordVisible
                    }
                ) {
                    Image(
                        painter = if (isPasswordVisible) painterResource(R.drawable.ic_visibility_off) else painterResource(
                            R.drawable.ic_visibilty_on
                        ),
                        contentDescription = "Toggle Password Visibility",
                    )
                }
            }
        },
        leadingIcon = {
            leadingIcon?.let {
                Image(
                    painter = painterResource(it),
                    contentDescription = "Toggle Password Visibility",
                )
            }

        },
        keyboardActions = keyboardActions,
        keyboardOptions = keyboardOptions
    )
}


