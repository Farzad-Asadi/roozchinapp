package ir.roozchinapp.data.dataBaseRoom.tables.appSystemInfo

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "app_system_info")
data class AppSystemInfo(
    @PrimaryKey(autoGenerate = true)
    val id : Int?=null,

    val dashboardHourHeight: Float=88f,
    val dashboardZoomState: Float=1f,
    val dashboardDayWidth: Int=704,
)
