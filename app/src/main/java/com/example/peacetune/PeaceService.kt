package com.example.peacetune

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat

class PeaceService: Service() {
    private var myBinder = MyBinder()
    var mediaPlayer: MediaPlayer?= null
    private lateinit var mediaSession : MediaSessionCompat

    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder {
        mediaSession= MediaSessionCompat(baseContext,"My peace")
        return myBinder
    }
    inner class MyBinder:Binder(){
        fun currentService(): PeaceService {
            return this@PeaceService
        }
    }

    fun showNotification(playPausebtn:Int){
         val prevIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.PERVIOUS)
        val prevpendingIntent = PendingIntent.getBroadcast(baseContext,0,prevIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val playIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.PLAY)
        val playpendingIntent = PendingIntent.getBroadcast(baseContext,0,playIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val nextIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.NEXT)
        val nextpendingIntent = PendingIntent.getBroadcast(baseContext,0,nextIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val existIntent = Intent(this,NotificationReceiver::class.java).setAction(ApplicationClass.EXIST)
        val existpendingIntent = PendingIntent.getBroadcast(baseContext,0,existIntent, PendingIntent.FLAG_UPDATE_CURRENT)

        val imgArt= getImageArt(playerActivity.peacelistPA[playerActivity.songPosition].path)
        val img =if (imgArt!= null){
            BitmapFactory.decodeByteArray(imgArt,0,imgArt.size)
        }else{
            BitmapFactory.decodeResource(resources, R.drawable.splash_screen)
        }




        val notification= NotificationCompat.Builder(baseContext,ApplicationClass.CHANNEL_ID)
            .setContentTitle(playerActivity.peacelistPA[playerActivity.songPosition].title)
            .setContentText(playerActivity.peacelistPA[playerActivity.songPosition].artist)
            .setSmallIcon(R.drawable.music_note_icon)
            .setLargeIcon(img)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(true)
            .addAction(R.drawable.pervious_icon,"Pervious", prevpendingIntent)
            .addAction(playPausebtn,"Play", playpendingIntent)
            .addAction(R.drawable.back_right_icon,"Pause", nextpendingIntent)
            .addAction(R.drawable.exizst_icon,"Exist", existpendingIntent)
            .build()
        startForeground(13,notification)
    }
     fun createMediaPlayer(){
        try {
            if (playerActivity.peaceService!!.mediaPlayer==null) playerActivity.peaceService!!.mediaPlayer= MediaPlayer()
            playerActivity.peaceService!!. mediaPlayer!!.reset()
            playerActivity.peaceService!!. mediaPlayer!!.setDataSource(playerActivity.peacelistPA[playerActivity.songPosition].path)
            playerActivity.peaceService!!. mediaPlayer!!.prepare()

            playerActivity.binding.pausePA.setIconResource(R.drawable.pause_icon)
            playerActivity.peaceService!!.showNotification(R.drawable.pause_icon)
            playerActivity.binding.tvseekbarStart.text= formatDuration(mediaPlayer!!.currentPosition.toLong())
            playerActivity.binding.tvseekbarEnd.text= formatDuration(mediaPlayer!!.duration.toLong())
            playerActivity.binding.seekbarPA.progress=0
            playerActivity.binding.seekbarPA.max= mediaPlayer!!.duration
            playerActivity.NowPlayingId = playerActivity.peacelistPA[playerActivity.songPosition].id
        }catch (e: Exception){return}
    }
    fun seekbarSetup(){
        runnable = Runnable {
            playerActivity.binding.tvseekbarStart.text= formatDuration(mediaPlayer!!.currentPosition.toLong())
            playerActivity.binding.seekbarPA.progress=mediaPlayer!!.currentPosition
            Handler(Looper.getMainLooper()).postDelayed(runnable,200)
        }
        Handler(Looper.getMainLooper()).postDelayed(runnable,0)
    }

}