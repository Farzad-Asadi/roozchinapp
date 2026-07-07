package ir.roozchinapp.data.backup.core

data class BackupPreviewInfo(
    val version: Int,
    val createdAt: Long,
    val modules: List<String>,

    val categoryCount: Int = 0,
    val taskCount: Int = 0,
    val scheduleCount: Int = 0,
    val reminderCount: Int = 0,
    val systemInfoCount: Int = 0
)