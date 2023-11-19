/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.devstrike.app.medcareaidscheduler.R
import org.devstrike.app.medcareaidscheduler.components.TextFieldComponent
import org.devstrike.app.medcareaidscheduler.data.House
import org.devstrike.app.medcareaidscheduler.data.Payment
import org.devstrike.app.medcareaidscheduler.ui.staff.staff_components.PaymentItemCard
import org.devstrike.app.medcareaidscheduler.utils.Common
import org.devstrike.app.medcareaidscheduler.utils.Common.auth

@Composable
fun StaffPayments(navController: NavHostController) {
    val context = LocalContext.current
    val searchQuery: MutableState<String> = remember { mutableStateOf("") }

    var isItemClicked = false


    // Get the list of items from Firestore
    val payments = remember { mutableStateOf(listOf<Payment>()) }
    val paymentData: MutableState<Payment> = remember { mutableStateOf(Payment()) }

    val scope = rememberCoroutineScope()
    val TAG = "SupervisorHouses"

    LaunchedEffect(Unit) {
        val paymentList = mutableListOf<Payment>()

        withContext(Dispatchers.IO) {
            val querySnapshot =
                Common.paymentsCollectionRef.whereEqualTo("paymentOwnerID", auth.uid!!)
                    .get().await()

            for (document in querySnapshot) {
                val item = document.toObject(Payment::class.java)
                paymentList.add(item)
            }
        }
        payments.value = paymentList
    }

    LazyColumn(modifier = Modifier.padding(4.dp)) {
        item {
            TextFieldComponent(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                label = "Search",
                keyboardOptions = KeyboardOptions.Default.copy(
                    autoCorrect = false,
                ),
                inputType = "Search",
                leadingIcon = R.drawable.ic_search,
                modifier = Modifier.padding(16.dp)

            )
        }
        val filteredPaymentList = payments.value.filter { payment ->
            payment.paymentDate.contains(searchQuery.value, true)
                    || payment.paymentAmount.contains(
                searchQuery.value,
                true
            ) || payment.paymentDateApproved.contains(searchQuery.value, true)
        }.sortedByDescending { payment -> payment.paymentDateApproved }
        items(filteredPaymentList) { payment ->
            PaymentItemCard(payment)
        }
    }
}