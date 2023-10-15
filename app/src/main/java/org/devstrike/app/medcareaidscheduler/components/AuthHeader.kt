/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import org.devstrike.app.medcareaidscheduler.ui.theme.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AuthHeader(title: String = "", subtitle: String = "", modifier: Modifier = Modifier) {

    Column {
        Text(text = title, style = Typography.displayMedium, modifier = modifier
            .fillMaxWidth()
            .padding(4.dp), fontWeight = FontWeight.Bold,
            color = Color.Black, textAlign = TextAlign.Center)
        Text(text = subtitle, style = Typography.titleMedium, modifier = modifier
            .fillMaxWidth()
            .padding(4.dp), fontWeight = FontWeight.Bold,
            color = Color.Black, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(124.dp))

    }

}