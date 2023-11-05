/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.ui.staff.staff_notifications

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch
import org.devstrike.app.medcareaidscheduler.ui.theme.Purple80
import org.devstrike.app.medcareaidscheduler.utils.Common.GENERAL_NOTIFICATION_TAG
import org.devstrike.app.medcareaidscheduler.utils.Common.PERSONAL_NOTIFICATION_TAG

@OptIn(ExperimentalPagerApi::class)
@Composable
fun StaffNotifications(navController: NavHostController) {
    /*
    * 1. the tabs for 'Personal' and 'General'
    * 2. the search bar for searching dates, types and sender of notification
    * 3. list of notifications
    *
    * */
    // on below line we are creating variable for pager state.
    val pagerState = rememberPagerState(pageCount = 2)

    Surface {

        Column {
            // on the below line we are specifying the top app bar
            // and specifying background color for it.
            // on below line we are calling tabs
            StaffNotificationTabs(pagerState = pagerState)
            // on below line we are calling tabs content
            // for displaying our page for each tab layout
            StaffNotificationTabsContent(pagerState = pagerState)
        }
    }
}


// on below line we are
// creating a function for tabs
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalPagerApi
@Composable
fun StaffNotificationTabs(pagerState: PagerState) {
    // in this function we are creating a list
    // in this list we are specifying data as
    // name of the tab and the icon for it.
    val list = listOf(
        "Personal",
        "General",

        )
    // on below line we are creating
    // a variable for the scope.
    val scope = rememberCoroutineScope()
    // on below line we are creating a
    // individual row for our tab layout.
    TabRow(
        modifier = Modifier.padding(8.dp).clip(
            RoundedCornerShape(8.dp)
        ),

        // on below line we are specifying
        // the selected index.
        selectedTabIndex = pagerState.currentPage,

        // on below line we are
        // specifying background color.
//        containerColor = MaterialTheme.colorScheme.onPrimaryContainer,

        // on below line we are specifying content color.
//        contentColor = MarketPlaceDayColor,

        // on below line we are specifying
        // the indicator for the tab
//        indicator = { tabPositions ->
//            // on below line we are specifying the styling
//            // for tab indicator by specifying height
//            // and color for the tab indicator.
//            TabRowDefaults.Indicator(
//                Modifier.pagerTabIndicatorOffset(pagerState, tabPositions),
//                height = 2.dp,
////                color = MarketPlaceDayColor
//            )
//        }
    ) {
        // on below line we are specifying icon
        // and text for the individual tab item
        list.forEachIndexed { index, _ ->
            // on below line we are creating a tab.
            Tab(
                // on below line we are specifying icon
                // for each tab item and we are calling
                // image from the list which we have created.
//                icon = {
//                    Icon(imageVector = list[index].second, contentDescription = null)
//                },
                // on below line we are specifying the text for
                // the each tab item and we are calling data
                // from the list which we have created.
                text = {
                    Text(
                        list[index],
                        // on below line we are specifying the text color
                        // for the text in that tab
                        color = if (pagerState.currentPage == index) Purple80 else Color.LightGray
                    )
                },
                // on below line we are specifying
                // the tab which is selected.
                selected = pagerState.currentPage == index,
                // on below line we are specifying the
                // on click for the tab which is selected.
                onClick = {
                    // on below line we are specifying the scope.
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }
    }
}


// on below line we are creating a tab content method
// in which we will be displaying the individual page of our tab .
@OptIn(ExperimentalFoundationApi::class)
@ExperimentalPagerApi
@Composable
fun StaffNotificationTabsContent(pagerState: PagerState) {
    // on below line we are creating
    // horizontal pager for our tab layout.
    HorizontalPager(state = pagerState) {
        // on below line we are specifying
        // the different pages.
            page ->
        when (page) {
            // on below line we are calling tab content screen
            // and specifying data as Home Screen.
            0 -> ShiftNotificationScreen(PERSONAL_NOTIFICATION_TAG)
            // on below line we are calling tab content screen
            // and specifying data as Shopping Screen.
            1 -> ShiftNotificationScreen(GENERAL_NOTIFICATION_TAG)
        }
    }
}
