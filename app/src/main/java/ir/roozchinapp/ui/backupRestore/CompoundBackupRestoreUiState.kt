package ir.roozchinapp.ui.backupRestore

import ir.roozchinapp.data.backup.core.BackupPreviewInfo


data class CompoundBackupRestoreUiState(
    val isWorking: Boolean = false,

    val message: String? = null,
    val error: String? = null,

    // وقتی بکاپ ساخته شد، UI با CreateDocument از کاربر مسیر ذخیره می‌گیرد
    val exportReadyFilePath: String? = null,
    val exportSuggestedName: String? = null,

    // وقتی فایل ریستور انتخاب شد، preview نشان داده می‌شود
    val previewInfo: BackupPreviewInfo? = null,
    val restoreCandidateFilePath: String? = null
)