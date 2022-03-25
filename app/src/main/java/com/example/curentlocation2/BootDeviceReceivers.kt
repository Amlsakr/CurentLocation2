package com.example.curentlocation2

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

class BootDeviceReceivers : BroadcastReceiver() {
    override fun onReceive(context: Context?, p1: Intent?) {
        context?.let {
            ContextCompat.startForegroundService(it, Intent(it, LocationService::class.java))
        }
    }
}