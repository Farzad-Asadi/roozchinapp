package ir.roozchinapp.data.backup.core

interface BackupModuleProvider {

    val moduleName: String

    suspend fun exportData(): String

    suspend fun importData(json: String)
}