package com.example.peacetune

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.media.MediaPlayer
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.peacetune.databinding.ActivityPlayerBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class playerActivity : AppCompatActivity(), ServiceConnection,MediaPlayer.OnCompletionListener {

    companion object{
        lateinit var peacelistPA : ArrayList<peace>
        var songPosition: Int = 0
       // var mediaPlayer:MediaPlayer?= null
        var isPalying = false
        var peaceService: PeaceService? = null
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: ActivityPlayerBinding
        var repeat:Boolean = false
        var min15: Boolean = false
        var min30: Boolean = false
        var min60: Boolean = false
        var NowPlayingId:String =""
        var isFavourit:Boolean= false
        var fIndex:Int= -1
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.CurrentTheme[MainActivity.themeIndex])
        binding= ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initailizeLayout()
        binding.backBtnPA.setOnClickListener { finish() }
        binding.pausePA.setOnClickListener{
         if (isPalying) pausemusic()
            else playmusic()
        }

        binding.perviousbtnPA.setOnClickListener { preNext(increment = false) }
        binding.nextbtnPA.setOnClickListener { preNext(increment = true) }
        binding.seekbarPA.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               if (fromUser) peaceService!!.mediaPlayer!!.seekTo(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) =Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })

        binding.repeatBtnPA.setOnClickListener {
            if (!repeat) {
                repeat = true
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple))
            }else{
                repeat = false
                binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.cool_pink))
            }
        }
        binding.equalizerBtnPA.setOnClickListener {
            try {
                val eqIntent =Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL)
                eqIntent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, peaceService!!.mediaPlayer!!.audioSessionId)
                eqIntent.putExtra(AudioEffect.EXTRA_PACKAGE_NAME,baseContext.packageName)
                eqIntent.putExtra(AudioEffect.EXTRA_CONTENT_TYPE,AudioEffect.CONTENT_TYPE_MUSIC)
                startActivityForResult(eqIntent,13)
            }catch (e:Exception){Toast.makeText(this,"Equalizer Feature Not Supported!!",Toast.LENGTH_SHORT).show()}
        }

        binding.timerBtnPA.setOnClickListener {
            val timer = min15 || min30 || min60
            if (!timer) {
                showBottomSheetDialog()
            }
            else{
                val builder = MaterialAlertDialogBuilder(this)
                builder.setTitle("stop")
                    .setMessage("Do You Want To stop timer")
                    .setPositiveButton("yes"){ _,_ ->
                       min15= false
                        min30= false
                        min60= false
                        binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.cool_pink))

                    }
                    .setNegativeButton("No"){dialog,_ ->
                        dialog.dismiss()
                    }
                val customDialog = builder.create()
                customDialog.show()
                customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
            }
        }

        binding.shareBtnPA.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action= Intent.ACTION_SEND
            shareIntent.type= "audio/*"
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(peacelistPA[songPosition].path))
            startActivity(Intent.createChooser(shareIntent,"sharing Musice File!!"))
        }
        binding.favouriteBtnPA.setOnClickListener {
            if (isFavourit){
                isFavourit=false
                binding.favouriteBtnPA.setImageResource(R.drawable.favourit_empty_icon)
                favouriteActivity.FavouriteSongs.removeAt(fIndex)
            }
            else{
                isFavourit=true
                binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
                favouriteActivity.FavouriteSongs.add(peacelistPA[songPosition])}
        }

    }

    private fun setLayout(){
        fIndex= favouriteChecker(peacelistPA[songPosition].id)
        Glide.with(this).load(peacelistPA[songPosition].artUri)
            .apply(RequestOptions().placeholder(R.drawable.peace_icon).centerCrop())
            .into(binding.nameImgPA)
        binding.namePA.text = peacelistPA[songPosition].title
        if (repeat)  binding.repeatBtnPA.setColorFilter(ContextCompat.getColor(this, R.color.purple))
        if (min15 || min30 || min60) binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple))
        if (isFavourit) binding.favouriteBtnPA.setImageResource(R.drawable.favourite_icon)
        else binding.favouriteBtnPA.setImageResource(R.drawable.favourit_empty_icon)

    }
    private fun createMediaPlayer(){
        try {
            if (peaceService!!.mediaPlayer==null) peaceService!!.mediaPlayer= MediaPlayer()
            peaceService!!. mediaPlayer!!.reset()
            peaceService!!. mediaPlayer!!.setDataSource(peacelistPA[songPosition].path)
            peaceService!!. mediaPlayer!!.prepare()
            peaceService!!.mediaPlayer!!.start()
            isPalying = true
            binding.pausePA.setIconResource(R.drawable.pause_icon)
            peaceService!!.showNotification(R.drawable.pause_icon)
            binding.tvseekbarStart.text= formatDuration(peaceService!!.mediaPlayer!!.currentPosition.toLong())
            binding.tvseekbarEnd.text= formatDuration(peaceService!!.mediaPlayer!!.duration.toLong())
            binding.seekbarPA.progress=0
            binding.seekbarPA.max= peaceService!!.mediaPlayer!!.duration
            peaceService!!.mediaPlayer!!.setOnCompletionListener(this)
            NowPlayingId= peacelistPA[songPosition].id
        }catch (e: Exception){return}
    }
    private fun initailizeLayout(){
        songPosition= intent.getIntExtra("index",0)
        when(intent.getStringExtra("class")){
            "favouriteAdaptor"->{
                startService()
                peacelistPA= ArrayList()
                peacelistPA.addAll(favouriteActivity.FavouriteSongs)
                setLayout()
            }
            "NowPlaying"->{
                setLayout()
                binding.tvseekbarStart.text= formatDuration(peaceService!!.mediaPlayer!!.currentPosition.toLong())
                binding.tvseekbarEnd.text= formatDuration(peaceService!!.mediaPlayer!!.duration.toLong())
                binding.seekbarPA.progress= peaceService!!.mediaPlayer!!.currentPosition
                binding.seekbarPA.max= peaceService!!.mediaPlayer!!.duration
                if (isPalying) binding.pausePA.setIconResource(R.drawable.pause_icon)
                else binding.pausePA.setIconResource(R.drawable.play_icon)
            }

            "peaceAdaptorSearch"->{
                startService()
                peacelistPA= ArrayList()
                peacelistPA.addAll(MainActivity.peaceListSearch)
                setLayout()
            }
            "peaceAdaptor" -> {
                startService()
                peacelistPA= ArrayList()
                peacelistPA.addAll(MainActivity.peaceListMA)
                setLayout()


            }
            "MainActivity" -> {
                startService()
                peacelistPA= ArrayList()
                peacelistPA.addAll(MainActivity.peaceListMA)
                peacelistPA.shuffle()
                setLayout()

            }

            "favouriteShuffle"->{
                startService()
                peacelistPA= ArrayList()
                peacelistPA.addAll(favouriteActivity.FavouriteSongs)
                peacelistPA.shuffle()
                setLayout()
            }


        }
    }

    private fun playmusic(){
        binding.pausePA.setIconResource(R.drawable.pause_icon)
        peaceService!!.showNotification(R.drawable.pause_icon)
        isPalying= true
        peaceService!!. mediaPlayer!!.start()
    }
    private fun pausemusic(){
        binding.pausePA.setIconResource(R.drawable.play_icon)
        peaceService!!.showNotification(R.drawable.play_icon)
        isPalying=false
        peaceService!!.mediaPlayer!!.pause()
    }

    private fun preNext(increment: Boolean){
        if (increment) {
            setSongPosition(increment = true)
            setLayout()
            createMediaPlayer()
        }else {
            setSongPosition(increment = false)
            setLayout()
            createMediaPlayer()
        }
    }



    override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
        val binder= service as PeaceService.MyBinder
        peaceService= binder.currentService()
        createMediaPlayer()
        peaceService!!.seekbarSetup()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        peaceService= null
    }

    override fun onCompletion(mp: MediaPlayer?) {
        setSongPosition(increment = true)
        createMediaPlayer()
        try {
            setLayout()
        }catch (e:Exception){return}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==13 || resultCode== RESULT_OK)
            return
    }

    private fun showBottomSheetDialog(){
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(R.layout.bottom_sheet_dialog)
        dialog.show()
        dialog.findViewById<LinearLayout>(R.id.min_15)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will be stop after 15min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple))
            min15= true
            Thread{Thread.sleep((15 * 60000).toLong())
            if (min15) existApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_30)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will be stop after 30min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple))
            min30= true
            Thread{Thread.sleep((30 * 60000).toLong())
                if (min30) existApplication()}.start()
            dialog.dismiss()
        }
        dialog.findViewById<LinearLayout>(R.id.min_60)?.setOnClickListener{
            Toast.makeText(baseContext,"Music will be stop after 30min",Toast.LENGTH_SHORT).show()
            binding.timerBtnPA.setColorFilter(ContextCompat.getColor(this,R.color.purple))
            min60= true
            Thread{Thread.sleep((60 * 60000).toLong())
                if (min60) existApplication()}.start()
            dialog.dismiss()
        }

    }
    private fun startService(){
        // for starting Service
        val intent = Intent(this,PeaceService::class.java)
        bindService(intent,this, BIND_AUTO_CREATE)
        startService(intent)
    }

}