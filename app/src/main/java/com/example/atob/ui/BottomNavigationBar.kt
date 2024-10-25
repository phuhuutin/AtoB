package com.example.atob.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.atob.ui.screen.Screen

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        Screen.Home,
        Screen.Profile,
        Screen.Settings
    )

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                label = { Text(stringResource(screen.resourceId)) },
                selected = false, // Set true if current route matches screen.route
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}