/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import org.devstrike.app.medcareaidscheduler.navigation.Screen
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import org.devstrike.app.medcareaidscheduler.utils.Common

@Composable
fun AlertDialogComponent(onDismiss: () -> Unit = {}, title: String, message: String, onConfirm: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismiss()
        },
        title = {
            Text(text = title, style = Typography.titleLarge)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(
                onClick = {
                   onConfirm()
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text("No")
            }
        },

        )
}


/*

@Composable
fun AlertRejectDialog(onDismiss: () -> Unit = {}, title: String, message: String, onSubmit: () -> Unit, onCancel: () -> Unit) {
    AlertDialog(
        onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            onDismiss()
        },
        title = {
            Text(text = title, style = Typography.titleLarge)
        },
        text = {
            Text(text = message)
        },
        confirmButton = {
            TextButton(
                onClick = {
                   onConfirm()
                }
            ) {
                Text("Yes")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onCancel()
                }
            ) {
                Text("No")
            }
        },

        )
}
*/


