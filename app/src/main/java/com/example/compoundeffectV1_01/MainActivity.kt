package com.example.compoundeffectV1_01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.compoundeffectV1_01.ui.mainScreenUi.MainScreen
import com.example.compoundeffectV1_01.ui.navigation.AppNavGraph
import com.example.compoundeffectV1_01.ui.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val navController = rememberNavController()

            AppTheme(dynamicColor = false)  {
                Surface {
                    AppNavGraph(navController = navController)
                }
            }
        }
    }
}

