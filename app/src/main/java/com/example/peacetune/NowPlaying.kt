package com.example.peacetune

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.peacetune.databinding.FragmentNowPlayingBinding


class NowPlaying : Fragment() {
  companion object{
      @SuppressLint("StaticFieldLeak")
      lateinit var binding: FragmentNowPlayingBinding
  }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        requireContext().theme.applyStyle(MainActivity.CurrentTheme[MainActivity.themeIndex],true)
        val view =inflater.inflate(R.layout.fragment_now_playing, container, false)
        binding= FragmentNowPlayingBinding.bind(view)
        binding.root.visibility= View.INVISIBLE
        binding.PlaypauseBtnNP.setOnClickListener {
            if (playerActivity.isPalying) pauseMusic() else playMusic()
        }
        binding.nextBtnNP.setOnClickListener {
            setSongPosition(increment= true)
            playerActivity.peaceService!!.createMediaPlayer()

            Glide.with(this).load(playerActivity.peacelistPA[playerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
                .into(NowPlaying.binding.songImgNP)
            NowPlaying.binding.songNameNP.text= playerActivity.peacelistPA[playerActivity.songPosition].title
            playerActivity.peaceService!!.showNotification(R.drawable.pause_icon)
            playMusic()
        }
        binding.root.setOnClickListener {
            val intent = Intent(requireContext(),playerActivity::class.java)
            intent.putExtra("index", playerActivity.songPosition)
            intent.putExtra("class","NowPlaying")
            ContextCompat.startActivity(requireContext(),intent,null)
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        if (playerActivity.peaceService!=null){
            binding.root.visibility=View.VISIBLE
            binding.songNameNP.isSelected=true
            Glide.with(this).load(playerActivity.peacelistPA[playerActivity.songPosition].artUri)
                .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
                .into(binding.songImgNP)
            binding.songNameNP.text= playerActivity.peacelistPA[playerActivity.songPosition].title
            if (playerActivity.isPalying) binding.PlaypauseBtnNP.setIconResource(R.drawable.pause_icon)
            else binding.PlaypauseBtnNP.setIconResource(R.drawable.play_icon)
        }
    }
      private fun playMusic(){
          playerActivity.peaceService!!.mediaPlayer!!.start()
          binding.PlaypauseBtnNP.setIconResource(R.drawable.pause_icon)
          playerActivity.peaceService!!.showNotification(R.drawable.pause_icon)
          playerActivity.binding.nextbtnPA.setIconResource(R.drawable.pause_icon)
          playerActivity.isPalying=true
      }
    private fun pauseMusic(){
        playerActivity.peaceService!!.mediaPlayer!!.pause()
        binding.PlaypauseBtnNP.setIconResource(R.drawable.play_icon)
        playerActivity.peaceService!!.showNotification(R.drawable.play_icon)
        playerActivity.binding.nextbtnPA.setIconResource(R.drawable.play_icon)
        playerActivity.isPalying=false
    }

}