package com.example.peacetune

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.peacetune.databinding.ActivityPlaylistBinding
import com.example.peacetune.databinding.AddPlaylistDailougBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.SimpleDateFormat
import java.util.Locale

class playlistActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaylistBinding
    private lateinit var adaptor: playlistAdaptor

    companion object{
        var musicPlaylist:Musicplaylist= Musicplaylist()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.CurrentTheme[MainActivity.themeIndex])
        binding= ActivityPlaylistBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnPL.setOnClickListener { finish() }


        binding.playlistRV.setHasFixedSize(true)
        binding.playlistRV.setItemViewCacheSize(13)
        binding.playlistRV.layoutManager = GridLayoutManager(this,2)
        adaptor = playlistAdaptor(this, playlistList = musicPlaylist.ref)
        binding.playlistRV.adapter = adaptor

        binding.addPlayListBtn.setOnClickListener {
            customAlertDailoug()
        }


    }

    private fun customAlertDailoug(){
        val customDialog= LayoutInflater.from(this,).inflate(R.layout.add_playlist_dailoug,binding.root,false)
         val binder= AddPlaylistDailougBinding.bind(customDialog)
        val builder = MaterialAlertDialogBuilder(this)
        builder.setView(customDialog)
        builder.setTitle("playlist Detail")

            .setPositiveButton("Add") { dialog, _ ->
               val playlistName= binder.playlistName.text
                val createdBy = binder.yourName.text
                if (playlistName!= null && createdBy!=null)
                    if (playlistName.isNotEmpty()&& createdBy.isNotEmpty()){
                        addplaylist(playlistName.toString(),createdBy.toString())
                    }

                 dialog.dismiss()
            }.show()


    }

    private fun addplaylist(name:String,createdBy:String) {
        var playlistExist= false
        for(i in musicPlaylist.ref){
            if (name.equals(i.name)){
                playlistExist= true
                break
            }
        }

        if (playlistExist) Toast.makeText(this,"playlist exist",Toast.LENGTH_SHORT).show()
        else{
            val tempList = Playlist()
            tempList.name= name
            tempList.playlist= ArrayList()
            tempList.createdBy= createdBy
            val calender= java.util.Calendar.getInstance().time
            val sdf =SimpleDateFormat("dd  MM yyyy", Locale.ENGLISH)
            tempList.createdOn = sdf.format(calender)
            musicPlaylist.ref.add(tempList)
            adaptor.refreshPlaylist()



        }

    }
}