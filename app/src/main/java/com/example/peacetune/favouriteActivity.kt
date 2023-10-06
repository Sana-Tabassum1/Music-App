package com.example.peacetune

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.peacetune.databinding.ActivityFavouriteBinding

class favouriteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFavouriteBinding
    private lateinit var adaptor: FavouriteAdaptor
    companion object{
        var FavouriteSongs:ArrayList<peace> = ArrayList()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.CurrentTheme[MainActivity.themeIndex])
        binding=ActivityFavouriteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backBtnFA.setOnClickListener { finish() }




        binding.favourtiesRV.setHasFixedSize(true)
        binding.favourtiesRV.setItemViewCacheSize(13)
        binding.favourtiesRV.layoutManager= GridLayoutManager(this,4)
        adaptor=FavouriteAdaptor(this, FavouriteSongs)
        binding.favourtiesRV.adapter = adaptor
        if (FavouriteSongs.size <1) binding.shuffleBtnFA.visibility=View.INVISIBLE
        binding.shuffleBtnFA.setOnClickListener {
            val intent = Intent(this, playerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "favouriteShuffle")
            startActivity(intent)
        }


    }
}