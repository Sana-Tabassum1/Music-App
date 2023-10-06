package com.example.peacetune

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlin.system.exitProcess

class NotificationReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            ApplicationClass.PERVIOUS -> prevNextsong(increment = false,context= context!!)
            ApplicationClass.PLAY -> if (playerActivity.isPalying) pauseMusic() else playMusic()
            ApplicationClass.NEXT -> prevNextsong(increment = true,context=context!!)
            ApplicationClass.EXIST ->{
                playerActivity.peaceService!!.stopForeground(true)
                playerActivity.peaceService!!.mediaPlayer!!.release()
                playerActivity.peaceService= null
                exitProcess(1)
            }


        }
    }
    private fun playMusic(){
        playerActivity.isPalying =true
        playerActivity.peaceService!!.mediaPlayer!!.start()
        playerActivity!!.peaceService!!.showNotification(R.drawable.pause_icon)
        playerActivity.binding.pausePA.setIconResource(R.drawable.pause_icon)
        NowPlaying.binding.PlaypauseBtnNP.setIconResource(R.drawable.pause_icon)

    }

    private fun pauseMusic(){
        playerActivity.isPalying =false
        playerActivity.peaceService!!.mediaPlayer!!.pause()
        playerActivity!!.peaceService!!.showNotification(R.drawable.play_icon)
        playerActivity.binding.pausePA.setIconResource(R.drawable.play_icon)
        NowPlaying.binding.PlaypauseBtnNP.setIconResource(R.drawable.play_icon)

    }

    private fun prevNextsong(increment:Boolean,context: Context){
        setSongPosition(increment= increment)
        playerActivity.peaceService!!.createMediaPlayer()
        Glide.with(context)
            .load(playerActivity.peacelistPA[playerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
            .into(playerActivity.binding.nameImgPA)
        playerActivity.binding.namePA.text = playerActivity.peacelistPA[playerActivity.songPosition].title

        Glide.with(context).load(playerActivity.peacelistPA[playerActivity.songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
            .into(NowPlaying.binding.songImgNP)
        NowPlaying.binding.songNameNP.text= playerActivity.peacelistPA[playerActivity.songPosition].title
        playMusic()
        playerActivity.fIndex= favouriteChecker(playerActivity.peacelistPA[playerActivity.songPosition].id)
        if (playerActivity.isFavourit) playerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else playerActivity.binding.favouriteBtnPA.setImageResource(R.drawable.favourit_empty_icon)
    }
}