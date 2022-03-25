package com.example.curentlocation2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Build.*
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class LocationService : Service() {
    private val NOTIFICATION_CHANNEL_ID = "my_notification_location"
    private val TAG = "LocationService"



    override fun onCreate() {
        super.onCreate()
        isServiceStarted = true
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setOngoing(false)
                .setSmallIcon(R.drawable.ic_launcher_background)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_ID, NotificationManager.IMPORTANCE_LOW
            )
            notificationChannel.description = NOTIFICATION_CHANNEL_ID
            notificationChannel.setSound(null, null)
            notificationManager.createNotificationChannel(notificationChannel)
            startForeground(1, builder.build())


        }
    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val timer = Timer()
        LocationHelper().startListeningUserLocation(
            this, object : MyLocationListener {
                override fun onLocationChanged(location: Location?) {
                    mLocation = location
                    var l = Location("")
                        l.latitude = 31.179731133448673
                    l.longitude = 31.563654791935555
                 var distance =   mLocation?.distanceTo(l)
                    Log.e(TAG , "distance by meters" + distance)
                    Log.e(TAG, "onLocationChanged: Latitude ${mLocation?.latitude} , Longitude ${mLocation?.longitude}")
                    Toast.makeText(this@LocationService , "location Latitude" +mLocation?.latitude + "longi" + mLocation?.longitude,Toast.LENGTH_SHORT ).show()
                    mLocation?.let {
                        AppExecutors.instance?.networkIO()?.execute {
                            val apiClient = ApiClient.getInstance(this@LocationService)
                                .create(ApiClient::class.java)
                            val response = apiClient.updateLocation()
                            response.enqueue(object : Callback<LocationResponse> {
                                override fun onResponse(
                                    call: Call<LocationResponse>,
                                    response: Response<LocationResponse>
                                ) {
                                    Log.e(TAG, "onLocationChanged: Latitude ${it.latitude} , Longitude ${it.longitude}")
                                    Log.e(TAG, "run: Running = Location Update Successful")
                                }

                                override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                                    Log.d(TAG, "run: Running = Location Update Failed")

                                }
                            })

                        }
                    }
                }
            })
        return START_STICKY
    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceStarted = false

    }

    companion object {
        var mLocation: Location? = null
        var isServiceStarted = false
    }
}