package com.example.zajil.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Looper
import androidx.annotation.Nullable
import androidx.core.app.NotificationCompat
import com.example.zajil.R
import com.example.zajil.activities.HomeActivity
import com.example.zajil.util.Constants
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority


class LocationService : Service() {

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            super.onLocationResult(p0)
            p0.let {
                val intent = Intent(Constants.LOCATION_BROADCAST)
                intent.putExtra(Constants.LATITUDE, it.locations[0].latitude)
                intent.putExtra(Constants.LONGITUDE, it.locations[0].longitude)
                sendBroadcast(intent)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        prepareForegroundNotification()
        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun prepareForegroundNotification() {
        val mLocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000L).build()

        /*  mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
          mLocationRequest.interval = 2000
          mLocationRequest.fastestInterval = 2000*/

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                Constants.CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager: NotificationManager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }

        val notificationIntent = Intent(this, HomeActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            Constants.SERVICE_LOCATION_REQUEST_CODE,
            notificationIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notification.Builder(this, Constants.CHANNEL_ID)
                .setContentTitle(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_notification_description))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pendingIntent)
                .build()
        else
            NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentTitle(getString(R.string.app_notification_description))
                .setSmallIcon(R.drawable.ic_logo)
                .setContentIntent(pendingIntent)
                .build()


        startForeground(Constants.LOCATION_SERVICE_NOTIF_ID, notification)

    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}