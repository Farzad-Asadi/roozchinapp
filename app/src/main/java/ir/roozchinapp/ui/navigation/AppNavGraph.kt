package ir.roozchinapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import ir.roozchinapp.ui.analyticsScreen.AnalyticsScreen
import ir.roozchinapp.ui.backupRestore.CompoundBackupRestoreScreen
import ir.roozchinapp.ui.categoryScreen.CategoryScreen
import ir.roozchinapp.ui.scheduleScreen.ScheduleScreen
import ir.roozchinapp.ui.settingsScreen.SettingsScreen
import ir.roozchinapp.ui.taskScreen.TaskScreen


@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = AppRoutes.CATEGORY
) {
    NavHost(
        navController = navController,
        startDestination = AppGraphRoutes.ROOT,
        modifier = modifier
    ) {
        navigation(
            route = AppGraphRoutes.ROOT,
            startDestination = startDestination
        ) {
            composable(AppRoutes.CATEGORY) {
                CategoryScreen(navController = navController)
            }
            composable(AppRoutes.SCHEDULE) {
                ScheduleScreen(navController = navController)
            }

            composable(AppRoutes.ANALYTICS) {
                AnalyticsScreen()
            }
            composable(
                route = AppRoutes.TASK_ROUTE,
                arguments = listOf(
                    navArgument(AppRoutes.ARG_CATEGORY_ID) {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                    navArgument(AppRoutes.ARG_TASK_ID) {
                        type = NavType.IntType
                        defaultValue = -1
                    }
                )
            ) { backStackEntry ->

                val exitToScheduleRequested by
                backStackEntry.savedStateHandle
                    .getStateFlow(
                        AppRoutes.KEY_EXIT_TASK_TO_SCHEDULE,
                        false
                    )
                    .collectAsState()

                val returnToSchedule: () -> Unit = {
                    val scheduleWasInBackStack =
                        navController.popBackStack(
                            AppRoutes.SCHEDULE,
                            false
                        )

                    if (!scheduleWasInBackStack) {
                        navController.navigate(AppRoutes.SCHEDULE) {
                            launchSingleTop = true
                            restoreState = false

                            popUpTo(navController.graph.startDestinationId) {
                                saveState = false
                            }
                        }
                    }
                }

                TaskScreen(
                    onClickBack = returnToSchedule,

                    onOpenTask = { taskId ->
                        navController.navigate(
                            AppRoutes.taskEdit(taskId)
                        )
                    },

                    exitToScheduleRequested =
                    exitToScheduleRequested,

                    onExitToScheduleRequestHandled = {
                        backStackEntry.savedStateHandle[
                            AppRoutes.KEY_EXIT_TASK_TO_SCHEDULE
                        ] = false
                    },

                    onExitToSchedule = returnToSchedule
                )
            }
            composable(AppRoutes.BACKUP_RESTORE) {
                CompoundBackupRestoreScreen(
                    onClose = { navController.popBackStack() }
                )
            }
            composable(AppRoutes.SETTINGS) {
                SettingsScreen(
                    onOpenBackupRestore = {
                        navController.navigate(AppRoutes.BACKUP_RESTORE)
                    }
                )
            }
        }
    }
}



object AppRoutes {
    const val CATEGORY = "category"
    const val SCHEDULE = "schedule"
    const val ANALYTICS = "analytics"
    const val TASK = "task"

    const val KEY_EXIT_TASK_TO_SCHEDULE =
        "exit_task_to_schedule"

    const val ARG_CATEGORY_ID = "categoryId"
    const val ARG_TASK_ID = "taskId"
    const val BACKUP_RESTORE = "backup_restore"

    const val SETTINGS = "settings"

    // route template
    const val TASK_ROUTE = "task?$ARG_CATEGORY_ID={$ARG_CATEGORY_ID}&$ARG_TASK_ID={$ARG_TASK_ID}"

    fun taskAdd(categoryId: Int): String =
        "task?$ARG_CATEGORY_ID=$categoryId&$ARG_TASK_ID=-1"

    fun taskEdit(taskId: Int): String =
        "task?$ARG_CATEGORY_ID=-1&$ARG_TASK_ID=$taskId"
}

object AppGraphRoutes {
    const val ROOT = "root"
}



//برای آیتم‌های باتم‌بار
data class AppBottomBarDestination(
    val route: String,
    val label: String,
    val icon: ImageVector
)
val bottomBarDestinations = listOf(
    AppBottomBarDestination(
        route = AppRoutes.SCHEDULE,
        label = "Schedule",
        icon = Icons.Filled.Schedule
    ),
    AppBottomBarDestination(
        route = AppRoutes.CATEGORY,
        label = "Category",
        icon = Icons.Filled.Category
    ),
    AppBottomBarDestination(
        route = AppRoutes.ANALYTICS,
        label = "آمار",
        icon = Icons.Filled.Analytics
    ),
    AppBottomBarDestination(
        route = AppRoutes.SETTINGS,
        label = "Settings",
        icon = Icons.Filled.Settings
    )

)