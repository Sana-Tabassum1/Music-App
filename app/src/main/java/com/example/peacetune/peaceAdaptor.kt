package com.example.peacetune

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.peacetune.databinding.PlayViewBinding

class peaceAdaptor(private  val context: Context,private  var peaceList: ArrayList<peace>) : RecyclerView.Adapter<peaceAdaptor.MyHolder>() {

    class MyHolder(binding: PlayViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val title = binding.NamePV
        val album = binding.albumPV
        val image = binding.imagePV
        val duration= binding.duration
        // this root mean every recyclerview clicklistnr
        val root =binding.root
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): peaceAdaptor.MyHolder {
       return MyHolder(PlayViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }
//this method call when view display on the screen
    override fun onBindViewHolder(holder: peaceAdaptor.MyHolder, position: Int) {
       holder.title.text= peaceList[position].title
       holder.album.text= peaceList[position].album
       holder.duration.text= formatDuration(peaceList[position].duration)
       Glide.with(context).load(peaceList[position].artUri)
           .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
           .into(holder.image)

    holder.root.setOnClickListener {
        when{
            MainActivity.search -> sentItent("peaceAdaptorSearch", pos = position)
            peaceList[position].id==playerActivity.NowPlayingId->
                sentItent(ref = "NowPlaying", pos = playerActivity.songPosition)
            else-> sentItent("peaceAdaptor", pos = position)
        }


    }

    }

    override fun getItemCount(): Int {
        return peaceList.size
    }
    fun updatePeaceList(searchList:ArrayList<peace>){
        peaceList=ArrayList( )
        peaceList.addAll(searchList)
        notifyDataSetChanged()
    }

    fun sentItent(ref:String,pos:Int){
        val intent = Intent(context,playerActivity::class.java)
        intent.putExtra("index", pos )
        intent.putExtra("class",ref)
        ContextCompat.startActivity(context,intent,null)

    }
}