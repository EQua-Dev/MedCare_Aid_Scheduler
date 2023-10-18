/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun NameTag(name: String, onDeleteClick: () -> Unit = {}) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row {
            Text(text = name, color = Color.White, fontWeight = FontWeight.Bold, style = Typography.bodySmall)
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Close, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)            }
        }
    }
}