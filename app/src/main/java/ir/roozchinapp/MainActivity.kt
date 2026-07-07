package ir.roozchinapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ir.roozchinapp.ui.mainScreenUi.MainScreen
import ir.roozchinapp.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)



        setContent {
            val navController = rememberNavController()

            AppTheme(dynamicColor = false)  {
                Surface {
                    MainScreen(navController = navController)
                }
            }
        }
    }
}

