package com.example.compoundeffectV1_01.utils

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.provider.Settings

fun Activity.requestExactAlarmPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
        startActivity(intent)
    }
}