/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components

import android.graphics.drawable.shapes.Shape
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_houses.SupervisorAddHouseSheet
import org.devstrike.app.medcareaidscheduler.utils.toast


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatActionButton(
    modifier: Modifier = Modifier, fabText: String = ""/*, addNewHouse: () -> Unit = {}, addNewStaff: () -> Unit = {}
*/, onClick: () -> Unit) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()

//    var isNewHouseClicked by rememberSaveable {
//        mutableStateOf(addNewHouse)
//    }
//
//    var isNewStaffClicked by rememberSaveable {
//        mutableStateOf(addNewStaff)
//    }


    ExtendedFloatingActionButton(shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 30)),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = Color.White,
        onClick = {
            context.toast("Fab clicked")
            onClick()
//            showModal = true
//            isNewHouseClicked = true

        }, modifier = Modifier.offset(y = (-88).dp)
    ) {
        Icon(Icons.Default.Add, contentDescription = null)
        Text(text = fabText)

    }


}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlainFloatActionButton(
    modifier: Modifier = Modifier,
    fabText: String = "",
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current
//    var isNewConnectClicked = false

    var isNewEventClicked by rememberSaveable {
        mutableStateOf(false)
    }

    FloatingActionButton(shape = MaterialTheme.shapes.large.copy(CornerSize(percent = 30)),
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = Color.White,
        onClick = {
            onClick()
//            context.toast("Fab clicked")
//            isNewEventClicked = true

        }, modifier = Modifier.size(24.dp)) {
        Text(text = fabText, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
    }


}
