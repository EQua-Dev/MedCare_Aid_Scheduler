/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.supervisor.supervisor_components

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.data.AssignedShift
import org.devstrike.app.medcareaidscheduler.utils.categorizeDatesByWeekInMonth
import org.devstrike.app.medcareaidscheduler.utils.getDate
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExpandableCard(modifier: Modifier = Modifier, title: String, content: List<AssignedShift>) {
    val TAG = "ExpandableCard"
    var expanded by remember { mutableStateOf(false) }
    Log.d(TAG, "ExpandableCard: $expanded")


    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp
        ), modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)

    ) {
        // Card content goes here, at this state a column to hold items
        Column(
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(width = 0.3.dp, MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .padding(8.dp)
                        .weight(0.8f)
                )
                IconButton(
                    onClick = {
                        expanded = !expanded
                    },
                    modifier = Modifier
                        .weight(0.2f)
                        .padding(4.dp)
                ) {
                    Image(
                        painter = if (expanded) painterResource(R.drawable.ic_arrow_up) else painterResource(
                            R.drawable.ic_arrow_down
                        ),
                        contentDescription = "Toggle Password Visibility",
                    )
                }
            }

            if (expanded) {
                //when expanded show another expandable card
                val listOfDates: MutableList<String> = mutableListOf()
                Log.d(TAG, "Shifts Content: $content")
                for (shift in content) {
                    /*
                    * Get all the shifts in the same week and add to a map with the key as the week number
                    * Display the list of keys in a lazy column
                    * When each key card is clicked, all days of the week from Monday to Sunday is displayed
                    * When each day is clicked, the Day, Night and SleepOver is displayed in a list and the assigned staff beside them
                    * If the assigned staff is empty or null, it shows indication to assign
                    *
                    * */

                    listOfDates.add(getDate(shift.assignedShiftDate.toLong(), "yyyy-MMM-dd"))


                }
                val dateFormat = SimpleDateFormat("yyyy-MMM-dd", Locale.ENGLISH)

                val dates = listOfDates.map { dateFormat.parse(it) ?: Date() }

                val groupedDates = categorizeDatesByWeekInMonth(dates)
                Log.d(TAG, "Grouped Dates: $groupedDates")


                Text(
                    text = "Content Sample for Display on Expansion of Card",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}