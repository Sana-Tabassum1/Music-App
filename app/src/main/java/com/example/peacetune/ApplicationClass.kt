package com.example.peacetune

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class ApplicationClass: Application() {
    companion object{
        const val CHANNEL_ID ="channel1"
        const val PLAY = "play"
        const val NEXT = "next"
        const val PERVIOUS = "pervious"
        const val EXIST ="exist"
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
           val notificationChannel= NotificationChannel(CHANNEL_ID,"Now Playing Song",NotificationManager.IMPORTANCE_HIGH)
           notificationChannel.description="This is a Important Channel for Showing Song!!"
           val notificationManager= getSystemService(NOTIFICATION_SERVICE) as NotificationManager
           notificationManager.createNotificationChannel(notificationChannel)
        }
    }
}