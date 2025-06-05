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
        title = "Updates",
        route = "updates"
    ),
    HamburgerNavigation(
        title = "Reset Password",
        route = "resetPassword"
    )
)

val facultyItems = listOf(
    HamburgerNavigation(
        title = "Time-Table",
        route = "timetable"
    ),
    HamburgerNavigation(
        title = "Attendence Dashboard",
        route = "facultyDashboard"
    ),
    HamburgerNavigation(
        title = "Updates",
        route = "updates"
    ),
//    HamburgerNavigation(
//        title = "Send Update",
//        route = "sendUpdate"
//    ),
    HamburgerNavigation(
        title = "Reset Password",
        route = "resetPassword"
    )
)

val adminItems = listOf(
    HamburgerNavigation(
        title = "Admin Panel",
        route = "adminHome"
    ),
    HamburgerNavigation(
        title = "Register Face",
        route = "registerFace"
    ),
    HamburgerNavigation(
        title = "Attendance",
        route = "adminAttendance"
    ),
    HamburgerNavigation(
        title = "Send Update",
        route = "sendUpdate"
    ),
    HamburgerNavigation(
        title = "Updates",
        route = "updates"
    ),
    HamburgerNavigation(
        title = "Reset Password",
        route = "resetPassword"
    )
)
