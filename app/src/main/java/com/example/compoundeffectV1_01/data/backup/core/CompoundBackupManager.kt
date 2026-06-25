package com.example.compoundeffectV1_01.data.backup.core

import android.content.Context
import com.example.compoundeffectV1_01.data.backup.compound.CompoundBackupData
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.inject.Inject

class CompoundBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val providers: Set<@JvmSuppressWildcards BackupModuleProvider>,
    private val gson: Gson
) {

    suspend fun createBackup(): File = withContext(Dispatchers.IO) {
        val backupFile = File(
            context.cacheDir,
            "compound_backup_${System.currentTimeMillis()}.zip"
        )

        val enabledProviders = providers.toList()

        val manifest = BackupManifest(
            modules = enabledProviders.map { it.moduleName }
        )

        ZipOutputStream(backupFile.outputStream()).use { zip ->
            zip.putNextEntry(ZipEntry("manifest.json"))
            zip.write(gson.toJson(manifest).toByteArray())
            zip.closeEntry()

            enabledProviders.forEach { provider ->
                val json = provider.exportData()

                zip.putNextEntry(ZipEntry("${provider.moduleName}.json"))
                zip.write(json.toByteArray())
                zip.closeEntry()
            }
        }

        backupFile
    }

    suspend fun restoreBackup(backupFile: File) = withContext(Dispatchers.IO) {
        ZipFile(backupFile).use { zip ->
            val manifestEntry = zip.getEntry("manifest.json")
                ?: error("manifest.json پیدا نشد")

            val manifestJson = zip.getInputStream(manifestEntry)
                .bufferedReader()
                .use { it.readText() }

            val manifest = gson.fromJson(
                manifestJson,
                BackupManifest::class.java
            )

            if (manifest.version > CURRENT_BACKUP_VERSION) {
                error("این فایل بکاپ با نسخه جدیدتری از اپ ساخته شده و قابل بازیابی نیست.")
            }

            val providerMap = providers.associateBy { it.moduleName }

            manifest.modules.forEach { moduleName ->
                val provider = providerMap[moduleName]
                    ?: return@forEach

                val entry = zip.getEntry("$moduleName.json")
                    ?: return@forEach

                val json = zip.getInputStream(entry)
                    .bufferedReader()
                    .use { it.readText() }

                provider.importData(json)
            }
        }
    }

    suspend fun previewBackup(backupFile: File): BackupPreviewInfo =
        withContext(Dispatchers.IO) {
            ZipFile(backupFile).use { zip ->
                val manifestEntry = zip.getEntry("manifest.json")
                    ?: error("manifest.json پیدا نشد")

                val manifestJson = zip.getInputStream(manifestEntry)
                    .bufferedReader()
                    .use { it.readText() }

                val manifest = gson.fromJson(
                    manifestJson,
                    BackupManifest::class.java
                )

                if (manifest.version > CURRENT_BACKUP_VERSION) {
                    error("این فایل بکاپ با نسخه جدیدتری از اپ ساخته شده است.")
                }

                val compoundEntry = zip.getEntry("compound.json")

                val compoundData =
                    if (compoundEntry != null) {
                        val compoundJson = zip.getInputStream(compoundEntry)
                            .bufferedReader()
                            .use { it.readText() }

                        gson.fromJson(
                            compoundJson,
                            CompoundBackupData::class.java
                        )
                    } else {
                        null
                    }

                BackupPreviewInfo(
                    version = manifest.version,
                    createdAt = manifest.createdAt,
                    modules = manifest.modules,

                    categoryCount = compoundData?.categories?.size ?: 0,
                    taskCount = compoundData?.tasks?.size ?: 0,
                    scheduleCount = compoundData?.schedules?.size ?: 0,
                    reminderCount = compoundData?.reminders?.size ?: 0,
                    systemInfoCount = compoundData?.systemInfos?.size ?: 0
                )
            }
        }
}