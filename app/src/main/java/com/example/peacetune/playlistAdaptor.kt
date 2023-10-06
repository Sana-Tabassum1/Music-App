package com.example.peacetune

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.peacetune.databinding.PlaylistViewBinding

class playlistAdaptor(private  val context: Context, private  var playlistList: ArrayList<Playlist>) : RecyclerView.Adapter<playlistAdaptor.MyHolder>() {

    class MyHolder(binding: PlaylistViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.playlistImg
        val name = binding.playlistName
        val root= binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyHolder {
        return MyHolder(PlaylistViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }
    //this method call when view display on the screen
    override fun onBindViewHolder(holder:MyHolder, position: Int) {
        holder.name.text= playlistList[position].name
        holder.name.isSelected = true


    }

    override fun getItemCount(): Int {
        return playlistList.size
    }

    fun refreshPlaylist(){
        playlistList= ArrayList()
        playlistList.addAll(playlistActivity.musicPlaylist.ref)
        notifyDataSetChanged()
    }



}