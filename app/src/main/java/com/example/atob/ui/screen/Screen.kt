package com.example.atob.ui.screen

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Report
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.atob.R


sealed class Screen(val route: String, @StringRes val resourceId: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.home, Icons.Filled.Home)
    object Pay : Screen("pay", R.string.pay, Icons.Filled.Money)
    object Profile : Screen("profile", R.string.profile, Icons.Filled.Person)
    object Login : Screen("login", R.string.login, Icons.Filled.Lock)
    object Report : Screen("report", R.string.report, Icons.Filled.Report)

}