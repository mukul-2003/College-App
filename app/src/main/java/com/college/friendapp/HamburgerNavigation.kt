package com.college.friendapp

data class HamburgerNavigation(
    val title: String,
    val route: String
)

val studentItems = listOf(
    HamburgerNavigation(
        title = "Time-Table",
        route = "timetable"
    ),
    HamburgerNavigation(
        title = "Attendance",
        route = "studentAttendance"
    ),
    HamburgerNavigation(
        title = "Reset Password",
        route = "resetPassword"
    )
)

val facultyItems = listOf(
    HamburgerNavigation(
        title = "Dashboard",
        route = "facultyDashboard"
    ),
    HamburgerNavigation(
        title = "My Attendance",
        route = "facultyDashboard"
    ),
    HamburgerNavigation(
        title = "Reset Password",
        route = "resetPassword"
    )
)
