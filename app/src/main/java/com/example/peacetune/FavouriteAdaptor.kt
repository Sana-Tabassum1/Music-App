package com.example.peacetune

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.peacetune.databinding.FavouritesViewBinding

class FavouriteAdaptor(private  val context: Context, private  var peaceList: ArrayList<peace>) : RecyclerView.Adapter<FavouriteAdaptor.MyHolder>() {

    class MyHolder(binding: FavouritesViewBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.songImgFV
        val name = binding.songNameFV
        val root= binding.root

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):MyHolder {
        return MyHolder(FavouritesViewBinding.inflate(LayoutInflater.from(context),parent,false))
    }
    //this method call when view display on the screen
    override fun onBindViewHolder(holder:MyHolder, position: Int) {
         holder.name.text= peaceList[position].title
        Glide.with(context).load(peaceList[position].artUri)
            .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
            .into(holder.image)

         holder.root.setOnClickListener {
             val intent = Intent(context,playerActivity::class.java)
             intent.putExtra("index", position )
             intent.putExtra("class","favouriteAdaptor")
             ContextCompat.startActivity(context,intent,null)
         }


    }

    override fun getItemCount(): Int {
        return peaceList.size
    }



}