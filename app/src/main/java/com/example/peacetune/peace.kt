package com.example.peacetune

import android.media.MediaMetadataRetriever
import android.net.Uri
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

data class peace(
    val id:String, val title:String, val album:String, val artist:String, val duration:Long = 0, val path:String,
    val artUri: Uri)
class Playlist{
    lateinit var name:String
    lateinit var playlist:ArrayList<peace>
    lateinit var createdBy: String
    lateinit var createdOn:String

}

class Musicplaylist{
    var ref:ArrayList<Playlist> = ArrayList()
}
fun formatDuration(duration: Long): String {
    val mintes =TimeUnit.MINUTES.convert(duration,TimeUnit.MILLISECONDS)

    val second = (TimeUnit.SECONDS.convert(duration,TimeUnit.MILLISECONDS)-
            mintes*TimeUnit.SECONDS.convert(1,TimeUnit.MINUTES))
    return String.format("%02d:%02d",mintes,second)
}

fun getImageArt(path: String): ByteArray? {
    val retriever= MediaMetadataRetriever()
    retriever.setDataSource(path)
    return retriever.embeddedPicture

}
 fun setSongPosition(increment: Boolean){
   if (!playerActivity.repeat){
       if(increment){
           if (playerActivity.peacelistPA.size -1 == playerActivity.songPosition)
               playerActivity.songPosition =0
           else ++playerActivity.songPosition
       }else{
           if (0== playerActivity.songPosition)
               playerActivity.songPosition = playerActivity.peacelistPA.size -1
           else --playerActivity.songPosition
       }

   }
}

fun existApplication(){
    if (playerActivity.peaceService!= null) {
        playerActivity.peaceService!!.stopForeground(true)
        playerActivity.peaceService!!.mediaPlayer!!.release()
        playerActivity.peaceService = null
    }
    exitProcess(1)
}

 fun favouriteChecker(id: String):Int{
     playerActivity.isFavourit=false
      favouriteActivity.FavouriteSongs.forEachIndexed { index, peace ->
            if (id== peace.id){
                playerActivity.isFavourit=true
                return index
            }
      }
     return -1
 }