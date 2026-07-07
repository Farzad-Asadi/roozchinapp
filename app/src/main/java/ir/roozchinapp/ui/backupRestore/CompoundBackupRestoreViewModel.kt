package ir.roozchinapp.ui.backupRestore

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import ir.roozchinapp.data.backup.core.CompoundBackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class CompoundBackupRestoreViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val backupManager: CompoundBackupManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CompoundBackupRestoreUiState())
    val uiState: StateFlow<CompoundBackupRestoreUiState> = _uiState.asStateFlow()

    fun createBackup() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isWorking = true,
                message = null,
                error = null
            )

            runCatching {
                withContext(Dispatchers.IO) {
                    backupManager.createBackup()
                }
            }.onSuccess { file ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    exportReadyFilePath = file.absolutePath,
                    exportSuggestedName = makeBackupFileName(),
                    message = "فایل بکاپ آماده ذخیره‌سازی است."
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    error = e.message ?: "خطا در ساخت بکاپ"
                )
            }
        }
    }

    fun writePreparedBackupToUri(uri: Uri?) {
        val path = _uiState.value.exportReadyFilePath

        if (uri == null || path == null) {
            clearPreparedBackup()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isWorking = true,
                message = null,
                error = null
            )

            runCatching {
                withContext(Dispatchers.IO) {
                    val source = File(path)

                    context.contentResolver.openOutputStream(uri)?.use { output ->
                        source.inputStream().use { input ->
                            input.copyTo(output)
                        }
                    } ?: error("امکان نوشتن فایل انتخاب‌شده وجود ندارد.")
                }
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    exportReadyFilePath = null,
                    exportSuggestedName = null,
                    message = "بکاپ با موفقیت ذخیره شد."
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    exportReadyFilePath = null,
                    exportSuggestedName = null,
                    error = e.message ?: "خطا در ذخیره فایل بکاپ"
                )
            }
        }
    }

    fun previewRestoreFile(uri: Uri?) {
        if (uri == null) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isWorking = true,
                message = null,
                error = null,
                previewInfo = null,
                restoreCandidateFilePath = null
            )

            runCatching {
                withContext(Dispatchers.IO) {
                    val file = copyUriToCache(uri)
                    val preview = backupManager.previewBackup(file)
                    file to preview
                }
            }.onSuccess { (file, preview) ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    previewInfo = preview,
                    restoreCandidateFilePath = file.absolutePath
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    error = e.message ?: "فایل بکاپ قابل خواندن نیست."
                )
            }
        }
    }

    fun restoreConfirmed() {
        val path = _uiState.value.restoreCandidateFilePath ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isWorking = true,
                message = null,
                error = null
            )

            runCatching {
                withContext(Dispatchers.IO) {
                    backupManager.restoreBackup(File(path))
                }
            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    previewInfo = null,
                    restoreCandidateFilePath = null,
                    message = "بازیابی اطلاعات با موفقیت انجام شد."
                )
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(
                    isWorking = false,
                    previewInfo = null,
                    restoreCandidateFilePath = null,
                    error = e.message ?: "خطا در بازیابی بکاپ"
                )
            }
        }
    }

    fun dismissRestorePreview() {
        _uiState.value = _uiState.value.copy(
            previewInfo = null,
            restoreCandidateFilePath = null
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            message = null,
            error = null
        )
    }

    private fun clearPreparedBackup() {
        _uiState.value = _uiState.value.copy(
            exportReadyFilePath = null,
            exportSuggestedName = null
        )
    }

    private fun copyUriToCache(uri: Uri): File {
        val target = File(
            context.cacheDir,
            "compound_restore_candidate_${System.currentTimeMillis()}.zip"
        )

        context.contentResolver.openInputStream(uri)?.use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: error("امکان خواندن فایل انتخاب‌شده وجود ندارد.")

        return target
    }

    private fun makeBackupFileName(): String {
        val stamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.US
        ).format(Date())

        return "compound_backup_$stamp.zip"
    }
}