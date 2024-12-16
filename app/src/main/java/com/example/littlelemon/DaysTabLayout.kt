@file:OptIn(ExperimentalFoundationApi::class)

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.littlelemon.TimetableEntry

//@Composable
//fun DaysTabLayout(
//    timetable: Map<String, List<TimetableEntry>>
//) {
//    var selectedTabIndex by remember { mutableIntStateOf(0) }
//    val tabs = listOf("MON", "TUE", "WED", "THU", "FRI")
//    val pagerState = rememberPagerState {
//        tabs.size
//    }
//    LaunchedEffect(selectedTabIndex) {
//        pagerState.animateScrollToPage(selectedTabIndex)
//    }
//    LaunchedEffect(pagerState.currentPage) {
//        selectedTabIndex = pagerState.currentPage
//    }
//
//
//    Column() {
//        TabRow(
//            selectedTabIndex = selectedTabIndex,
//            backgroundColor = Color(11, 11, 69),
//            modifier = Modifier.height(36.dp)
//        ) {
//            tabs.forEachIndexed { index, tab ->
//                Tab(
//                    selected = selectedTabIndex == index,
//                    onClick = { selectedTabIndex = index},
//                    selectedContentColor = Color.White,
//                    unselectedContentColor = Color.LightGray,
//                ) {
//                    Text(
//                        text = tab, fontSize = 15.sp,
//                        modifier = Modifier.padding(horizontal = 16.dp),
//                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
//                    )
//                }
//            }
//        }
//
//        HorizontalPager(state = pagerState, modifier = Modifier
//            .fillMaxWidth()
//            .weight(1f)
//        ) { pageIndex ->
//            val day = tabs[pageIndex]
//            val timetableForDay = timetable[day] ?: emptyList()
//
//            Box(
//                modifier = Modifier.fillMaxSize(),
//                contentAlignment = Alignment.TopCenter
//            ) {
//                if (timetableForDay.isEmpty()) {
//                    Text(
//                        text = "No classes for $day",
//                        color = Color.Gray,
//                        fontSize = 16.sp
//                    )
//                } else {
//                    Column(
//                        modifier = Modifier.padding(16.dp),
//                        verticalArrangement = Arrangement.spacedBy(8.dp)
//                    ) {
//                        timetableForDay.forEach { entry ->
//                            TimetableCard(entry)
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
@Composable
fun TimetableCard(entry: TimetableEntry) {
    Card(
        backgroundColor = Color(240, 240, 240),
        elevation = 6.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = entry.time,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(11, 11, 69)
            )
            Text(
                text = entry.subject,
                fontSize = 18.sp,
                color = Color(11, 11, 69)
            )
            Text(
                text = entry.location,
                fontWeight = FontWeight.Bold,
                color = Color(11, 11, 69),
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DaysTabLayout(
    timetable: Map<String, List<TimetableEntry>>
) {
    val tabs = listOf("MON", "TUE", "WED", "THU", "FRI") // Fixed list of days
    var selectedTabIndex by remember { mutableStateOf(0) }
    val pagerState = rememberPagerState { tabs.size }

    // Synchronize Tab and Pager
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Column {
        // TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color(11, 11, 69),
            modifier = Modifier.height(36.dp)
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.LightGray,
                ) {
                    Text(
                        text = tab,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }

        // HorizontalPager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            val day = tabs[pageIndex]
            val timetableForDay = timetable[day] ?: emptyList()

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                if (timetableForDay.isEmpty()) {
                    Text(
                        text = "No Class Today",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )
                } else {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        timetableForDay.forEach { entry ->
                            TimetableCard(entry)
                        }
                    }
                }
            }
        }
    }
}