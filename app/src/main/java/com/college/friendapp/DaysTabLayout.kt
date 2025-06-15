package com.college.friendapp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Collections.emptyList
import java.util.Locale

@Composable
fun TimetableCard(entry: TimetableEntry) {
    Card(
        backgroundColor = Color(255, 255, 255, 255),
        elevation = 12.dp,
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.fillMaxWidth()
            .border(width = 0.1.dp,
                color = Color(11, 11, 69),
                RoundedCornerShape(6.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(75.dp)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = entry.subject,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(11, 11, 69),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = entry.className,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,   // not bold
                    color = Color(11,11,69)
                )
            }
            Text(
                text = runCatching { toRomanNumeral(entry.Period.toInt()) }.getOrDefault(entry.Period),
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 12.dp),
                color = Color(11, 11, 69)
            )
        }
    }
}


@Composable
fun DaysTabLayout(
    timetable: Map<String, List<TimetableEntry>>
) {
    val tabs = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT")

    val today = remember {
        val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)
        val day = dayFormat.format(Calendar.getInstance().time).uppercase()
        when (day) {
            "MON" -> 0
            "TUE" -> 1
            "WED" -> 2
            "THU" -> 3
            "FRI" -> 4
            "SAT" -> 5
            else -> 0 // default to Monday if weekend
        }
    }

    var selectedTabIndex by remember { mutableStateOf(today) }
    val pagerState = rememberPagerState(initialPage = today, pageCount = { tabs.size })
    val coroutineScope = rememberCoroutineScope()

    // Synchronize Tab and Pager
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Column {
        // TabRow
        TabRow(
            selectedTabIndex = selectedTabIndex,
            backgroundColor = Color(11, 11, 69),
            modifier = Modifier.height(36.dp),
            indicator = { }
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index
                        coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    selectedContentColor = Color.White,
                    unselectedContentColor = Color.LightGray,
                ) {
                    Text(
                        text = tab,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(horizontal = 12.dp),
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
                modifier = Modifier.fillMaxSize().background(Color.White),
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

fun toRomanNumeral(number: Int): String {
    val romanMap = mapOf(
        1 to "I",
        2 to "II",
        3 to "III",
        4 to "IV",
        5 to "V",
        6 to "VI",
        7 to "VII",
        8 to "VIII",
        9 to "IX"
    )
    return romanMap[number] ?: number.toString()
}
