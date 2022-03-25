package com.example.curentlocation2

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {
    lateinit var  bootDeviceReceivers: BootDeviceReceivers
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bootDeviceReceivers = BootDeviceReceivers()
        IntentFilter(Intent.ACTION_BOOT_COMPLETED ).also {
            registerReceiver(bootDeviceReceivers ,it)
        }
        val startButton = findViewById<Button>(R.id.buttonStartService)
        val stopButton = findViewById<Button>(R.id.buttonStopService)

        startButton.setOnClickListener {
            ContextCompat.startForegroundService(this , Intent(this ,LocationService::class.java))
        }
        stopButton.setOnClickListener {
            stopService(Intent(this, LocationService::class.java))
        }
        if (!checkPermission()) {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_FINE_LOCATION)
        val result1 = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION)
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
    }
}