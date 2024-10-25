package com.example.atob.ui.screen

import androidx.annotation.StringRes
import com.example.atob.R

sealed class Screen(val route: String, @StringRes val resourceId: Int) {
    object Home : Screen("home", R.string.home)
    object Profile : Screen("profile", R.string.profile)
    object Settings : Screen("settings", R.string.settings)
    object Login : Screen("login", R.string.login)
}