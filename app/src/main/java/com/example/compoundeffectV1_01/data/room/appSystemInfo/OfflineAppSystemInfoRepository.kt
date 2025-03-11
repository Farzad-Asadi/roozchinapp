package com.example.compoundeffectV1_01.data.room.appSystemInfo

class OfflineAppSystemInfoRepository (
    private val appSystemInfoDao: AppSystemInfoDao
): AppSystemInfoRepository {
    override suspend fun insertAppSystemInfo(appSystemInfo: AppSystemInfo) =
        appSystemInfoDao.insertAppSystemInfo(appSystemInfo)

    override suspend fun getAllAppSystemInfo(): List<AppSystemInfo> =
        appSystemInfoDao.getAllAppSystemInfo()


    override suspend fun updateAppSystemInfo(dashboardHourHeight: Float,dashboardZoomState: Float) =
        appSystemInfoDao.updateAppSystemInfo(dashboardHourHeight,dashboardZoomState)

//    override fun getAllAppSystemInfoStream(): Flow<List<AppSystemInfo>> =
//        appSystemInfoDao.getAllAppSystemInfoStream()


}