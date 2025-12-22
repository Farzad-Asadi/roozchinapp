package com.example.compoundeffectV1_01.ui.scheduleScreen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.compoundeffectV1_01.ui.navigation.bottomBarDestinations
import com.example.compoundeffectV1_01.ui.navigation.AppRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen2(
    onNavigateToCategory: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ScheduleScreen2") }
            )
        },
        bottomBar = {
            NavigationBar {
                val items = bottomBarDestinations
                items.forEach { dest ->
                    val selected = dest.route == AppRoutes.Schedule2
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (!selected && dest.route == AppRoutes.Category2) onNavigateToCategory()
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) }
                    )
                }
            }
        }
    ) { padding ->
        Text(
            text = "Schedule content (foundation)",
            modifier = Modifier.padding(padding)
        )
    }
}
