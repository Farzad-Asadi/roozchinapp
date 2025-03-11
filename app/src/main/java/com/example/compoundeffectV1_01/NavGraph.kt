package com.example.compoundeffectV1_01

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.compoundeffectV1_01.ui.dashboardScreen.DashboardScreen

@Composable
fun NavGraph(
    modifier: Modifier = Modifier
){
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = DashboardRoute
    ) {
        composable(DashboardRoute){
            DashboardScreen()
        }
    }
}