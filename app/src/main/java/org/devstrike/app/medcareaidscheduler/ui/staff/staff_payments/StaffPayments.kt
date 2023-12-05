/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_payments

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.components.ButtonComponent
import org.devstrike.app.medcareaidscheduler.data.ConcludedShift
import org.devstrike.app.medcareaidscheduler.data.Payment
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth
import org.devstrike.app.medcareaidscheduler.utils.getHouse
import org.devstrike.app.medcareaidscheduler.utils.getShift
import org.devstrike.libs.android.timetravel.TimeTraveller

@Composable
fun StaffPayments(navController: NavHostController) {
    val context = LocalContext.current
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }

    var isItemClicked = false


    // Get the list of items from Firestore
    val worksheets = remember { mutableStateOf(listOf<ConcludedShift>()) }
    val paymentData: MutableState<Payment> = remember { mutableStateOf(Payment()) }

    val scope = rememberCoroutineScope()
    val TAG = "SupervisorHouses"

    val totalHours = 0

    LaunchedEffect(Unit) {
        val workSheetList = mutableListOf<ConcludedShift>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.concludedShiftsCollectionRef.whereEqualTo("shiftStaffID", auth.uid!!)
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(ConcludedShift::class.java)
                workSheetList.add(item)
                Log.d(TAG, "StaffPayments: worksheet item = $item")
            }
            Log.d(TAG, "StaffPayments: worksheet = $workSheetList")
        }
        worksheets.value = workSheetList
        Log.d(TAG, "StaffPayments: worksheet list = ${worksheets.value}")
    }



    LazyColumn(modifier = Modifier.padding(4.dp)) {
        item {
            worksheets.value.forEach { concludedShift ->
                Log.d(TAG, "StaffPayments: concluded shift = $concludedShift")
                val assignedShift = getShift(concludedShift.assignedShiftID, context)!!
                Log.d(TAG, "StaffPayments: assigned shift = $assignedShift")
                val assignedHouse = getHouse(assignedShift.assignedHouseID, context)!!
                Log.d(TAG, "StaffPayments: assigned house = $assignedHouse")

                if (TimeTraveller.isDateWithinThisWeek(assignedShift.assignedShiftDate.toLong())) {
                    totalHours.plus(concludedShift.noOfTotalHours.toInt())
                    Text(
                        text = TimeTraveller.getDate(
                            assignedShift.assignedShiftDate.toLong(),
                            "EEE, dd MMM, yyyy"
                        ), modifier = Modifier.padding(2.dp)
                    )
                    Text(text = assignedHouse.houseName, modifier = Modifier.padding(2.dp))
                    Text(
                        text = "D: ${concludedShift.noOfDayHours}",
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = "N: ${concludedShift.noOfNightHours}",
                        modifier = Modifier.padding(2.dp)
                    )
                    Text(
                        text = "S/O: ${concludedShift.noOfSleepOverHours}",
                        modifier = Modifier.padding(2.dp)
                    )
                    Divider(modifier = Modifier.padding(4.dp))
                }
            }

            Text(text = "Total Hours: $totalHours")

            ButtonComponent(buttonText = "Submit", onClick = {

            }, modifier = Modifier.fillMaxWidth())
        }
        /*val filteredPaymentList = worksheets.value.filter { concludedShift ->
            concludedShift.paymentDate.contains(searchQuery.value, true)
                    || concludedShift.paymentAmount.contains(
                searchQuery.value,
                true
            ) || concludedShift.paymentDateApproved.contains(searchQuery.value, true)
        }.sortedByDescending { payment -> payment.paymentDateApproved }
        items(filteredPaymentList) { payment ->
            PaymentItemCard(payment)
        }*/
    }
}