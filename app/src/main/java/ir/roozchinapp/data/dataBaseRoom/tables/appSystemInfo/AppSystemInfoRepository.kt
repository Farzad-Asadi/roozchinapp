package ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo

interface AppSystemInfoRepository {
    suspend fun insertAppSystemInfo(appSystemInfo: AppSystemInfo)


    suspend fun getAllAppSystemInfo(): List<AppSystemInfo>

    suspend fun updateAppSystemInfo(dashboardHourHeight: Float ,dashboardZoomState: Float)

//    fun getAllAppSystemInfoStream() : Flow<List<AppSystemInfo>>
}