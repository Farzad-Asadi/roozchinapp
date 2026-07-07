package ir.roozchinapp.ui.backupRestore

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import ir.roozchinapp.data.backup.core.BackupPreviewInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompoundBackupRestoreScreen(
    onClose: () -> Unit,
    viewModel: CompoundBackupRestoreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    val createBackupDocumentLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/zip")
        ) { uri: Uri? ->
            viewModel.writePreparedBackupToUri(uri)
        }

    val openBackupDocumentLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument()
        ) { uri: Uri? ->
            viewModel.previewRestoreFile(uri)
        }

    LaunchedEffect(uiState.exportReadyFilePath) {
        val path = uiState.exportReadyFilePath ?: return@LaunchedEffect
        val name = uiState.exportSuggestedName ?: "compound_backup.zip"

        if (path.isNotBlank()) {
            createBackupDocumentLauncher.launch(name)
        }
    }

    LaunchedEffect(uiState.message) {
        val msg = uiState.message ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(msg)
        viewModel.clearMessages()
    }

    LaunchedEffect(uiState.error) {
        val err = uiState.error ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(err)
        viewModel.clearMessages()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onClose,
                        enabled = !uiState.isWorking
                    ) {
                        Text("بازگشت")
                    }

                    Text(
                        text = "بکاپ و بازیابی اطلاعات",
                        textAlign = TextAlign.Right,
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Text(
                    text = "از اطلاعات فعلی برنامه فایل بکاپ بگیر یا یک بکاپ قبلی را بازیابی کن.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ساخت بکاپ",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "یک فایل ZIP شامل دسته‌ها، تسک‌ها، زمان‌بندی‌ها، یادآورها و اطلاعات سیستمی ساخته می‌شود.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.bodySmall
                        )

                        Button(
                            onClick = { viewModel.createBackup() },
                            enabled = !uiState.isWorking,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ساخت فایل بکاپ")
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "بازیابی بکاپ",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "با بازیابی، اطلاعات فعلی برنامه با اطلاعات داخل فایل بکاپ جایگزین می‌شود.",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )

                        OutlinedButton(
                            onClick = {
                                openBackupDocumentLauncher.launch(
                                    arrayOf(
                                        "application/zip",
                                        "application/octet-stream",
                                        "application/x-zip-compressed",
                                        "*/*"
                                    )
                                )
                            },
                            enabled = !uiState.isWorking,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("انتخاب فایل بکاپ برای بازیابی")
                        }
                    }
                }

                if (uiState.isWorking) {
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "در حال پردازش...",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Right,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        CircularProgressIndicator()
                    }
                }

                Spacer(Modifier.weight(1f))


            }
        }

        uiState.previewInfo?.let { preview ->
            RestoreConfirmDialog(
                preview = preview,
                onDismiss = { viewModel.dismissRestorePreview() },
                onConfirm = { viewModel.restoreConfirmed() }
            )
        }
    }
}

@Composable
private fun RestoreConfirmDialog(
    preview: BackupPreviewInfo,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تأیید بازیابی بکاپ",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Right
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "بازیابی این فایل، اطلاعات فعلی برنامه را جایگزین می‌کند.",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right,
                    color = MaterialTheme.colorScheme.error
                )

                Spacer(Modifier.height(8.dp))

                Text("نسخه بکاپ: ${preview.version}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Text("تاریخ ساخت: ${formatBackupTime(preview.createdAt)}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Text("دسته‌ها: ${preview.categoryCount}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Text("تسک‌ها: ${preview.taskCount}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Text("زمان‌بندی‌ها: ${preview.scheduleCount}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Text("یادآورها: ${preview.reminderCount}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
                Text("اطلاعات سیستمی: ${preview.systemInfoCount}", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Right)
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("بازیابی")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}

private fun formatBackupTime(timeMillis: Long): String {
    return SimpleDateFormat(
        "yyyy/MM/dd HH:mm",
        Locale.US
    ).format(Date(timeMillis))
}