package ir.roozchinapp.ui.settingsScreen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.roozchinapp.data.notification.PomodoroNotifications
import ir.roozchinapp.ui.navigation.AppRoutes

@Composable
fun SettingsScreen(
    onOpenBackupRestore: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val defaultStartDestination by viewModel.defaultStartDestination.collectAsState()



    val context = LocalContext.current

    Scaffold(
        topBar = {
            SettingsTopBar()
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            SettingsSectionTitle("General")

            DefaultStartDestinationRow(
                title = "صفحه ورود پیش‌فرض",
                subtitle = "انتخاب کن اپ بعد از اجرا با کدام صفحه باز شود"
            )

            DefaultStartOptionRow(
                icon = Icons.Filled.Category,
                title = "Category",
                subtitle = "شروع برنامه از صفحه دسته‌بندی‌ها",
                selected = defaultStartDestination == AppRoutes.CATEGORY,
                onClick = {
                    viewModel.setDefaultStartDestination(AppRoutes.CATEGORY)
                }
            )

            DefaultStartOptionRow(
                icon = Icons.Filled.Schedule,
                title = "Schedule",
                subtitle = "شروع برنامه از صفحه تایم‌لاین",
                selected = defaultStartDestination == AppRoutes.SCHEDULE,
                onClick = {
                    viewModel.setDefaultStartDestination(AppRoutes.SCHEDULE)
                }
            )

            DefaultStartOptionRow(
                icon = Icons.Filled.Analytics,
                title = "آمار",
                subtitle = "شروع برنامه از صفحه تحلیل پومودورو",
                selected = defaultStartDestination == AppRoutes.ANALYTICS,
                onClick = {
                    viewModel.setDefaultStartDestination(
                        AppRoutes.ANALYTICS
                    )
                }
            )

            HorizontalDivider()



            SettingsSectionTitle("Notifications")

            SettingsRow(
                icon = Icons.Filled.Notifications,
                title = "صدای نوتیفیکیشن پومودورو",
                subtitle = "تغییر صدا، ویبره و رفتار نوتیفیکیشن پومودورو",
                onClick = {
                    context.openPomodoroNotificationSettings(
                        channelId = PomodoroNotifications.CHANNEL_ID
                    )
                }
            )

            HorizontalDivider()

            SettingsSectionTitle("Data")

            SettingsRow(
                icon = Icons.Filled.Backup,
                title = "بکاپ و بازیابی اطلاعات",
                subtitle = "ساخت فایل بکاپ یا بازیابی اطلاعات از فایل ZIP",
                onClick = onOpenBackupRestore
            )

            HorizontalDivider()
        }
    }
}

@Composable
private fun SettingsTopBar() {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(Modifier.size(12.dp))

            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 22.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp)
            )
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun DefaultStartDestinationRow(
    title: String,
    subtitle: String
) {
    ListItem(
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
private fun DefaultStartOptionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = ListItemDefaults.colors(
            containerColor = if (selected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            } else {
                MaterialTheme.colorScheme.background
            }
        ),
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                modifier = Modifier.size(26.dp)
            )
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        supportingContent = {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        }
    )
}

private fun Context.openPomodoroNotificationSettings(channelId: String) {
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            putExtra(Settings.EXTRA_CHANNEL_ID, channelId)
        }
    } else {
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
    }

    startActivity(intent)
}