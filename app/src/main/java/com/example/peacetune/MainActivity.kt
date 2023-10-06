package com.example.peacetune

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.peacetune.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class MainActivity : AppCompatActivity() {
    //creating binding object
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var peaceAdaptor: peaceAdaptor

    //companion object is static object... access in anywhere

    companion object {
        lateinit var peaceListMA: ArrayList<peace>
        lateinit var peaceListSearch: ArrayList<peace>
        var search: Boolean = false
        var themeIndex:Int=0

        val CurrentTheme = arrayOf(R.style.coolPink,R.style.coolblue,R.style.coolpurple,R.style.coolgreen,R.style.coolblack)
        val CurrentThemeNav = arrayOf(R.style.coolPinkNav,R.style.coolblueNav,R.style.coolpurpleNav,R.style.coolgreenNav,R.style.coolblackNav)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val themEditor =getSharedPreferences("THEME", MODE_PRIVATE)
        themeIndex=themEditor.getInt("themeIndex",0)
        setTheme(CurrentThemeNav[themeIndex])

        //initilized binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //nav drawer
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
       if (requestRunTimepermission())
            initializeLayout()

//            // for retrive data  shared perference
//            favouriteActivity.FavouriteSongs = ArrayList()
//            val editor = getSharedPreferences("FAVOURITE", MODE_PRIVATE)
//            val jsonString = editor.getString("favouriteSongs", null)
//            val typeToken = object : TypeToken<ArrayList<peace>>() {}.type
//            if (jsonString != null) {
//                val data: ArrayList<peace> = GsonBuilder().create().fromJson(jsonString, typeToken)
//                favouriteActivity.FavouriteSongs.addAll(data)
//            }


        //btn on click
        binding.shuffleBtn.setOnClickListener {
            val intent = Intent(this, playerActivity::class.java)
            intent.putExtra("index", 0)
            intent.putExtra("class", "MainActivity")
            startActivity(intent)
        }
        binding.favouriteBtn.setOnClickListener {
            startActivity(Intent(this, favouriteActivity::class.java))
        }
        binding.playlistBtn.setOnClickListener {
            startActivity(Intent(this, playlistActivity::class.java))
        }


        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navfeedback -> Toast.makeText(this, "feedback", Toast.LENGTH_SHORT).show()
                R.id.navsetting ->   startActivity(Intent(this, settingScreen::class.java))
                R.id.navAbout -> Toast.makeText(this, "about", Toast.LENGTH_SHORT).show()
                R.id.navExist -> {
                    val builder = MaterialAlertDialogBuilder(this)
                    builder.setTitle("Exist")
                        .setMessage("Do You Want To Close App")
                        .setPositiveButton("yes") { _, _ ->
                            existApplication()
                        }
                        .setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val customDialog = builder.create()
                    customDialog.show()
                    customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
                    customDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
                }
            }
            true
        }
    }

    //for requestin permission
    private fun requestRunTimepermission(): Boolean {

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                13
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                initializeLayout()
            } else
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    13
                )

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeLayout() {

        search = false
        /*   val ListP = ArrayList<String>()
           ListP.add("1 Song")
           ListP.add("2 Song")
           ListP.add("3 Song")
           ListP.add("4 Song")
           ListP.add("5 Song")
           ListP.add("6 Song")*/

        peaceListMA = getAllaudio()

        binding.peaceRV.setHasFixedSize(true)
        binding.peaceRV.setItemViewCacheSize(13)
        binding.peaceRV.layoutManager = LinearLayoutManager(this)
        peaceAdaptor = peaceAdaptor(this, peaceListMA)
        binding.peaceRV.adapter = peaceAdaptor

        binding.total.text = "total : " + peaceAdaptor.itemCount
    }


    @SuppressLint("Recycle", "Range")
    private fun getAllaudio(): ArrayList<peace> {
        val tempList = ArrayList<peace>()
        val selection = MediaStore.Audio.Media.IS_MUSIC + " !=0"
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID
        )
        val cursor = this.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, null,
            MediaStore.Audio.Media.DATE_ADDED + " DESC", null
        )

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val titleC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val albumC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val artistC =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val idC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val durationC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val pathC = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                    val albumIdC =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
                            .toString()
                    val uri = Uri.parse("content://media/external/audio/albumart")
                    val artUriC = Uri.withAppendedPath(uri, albumIdC)
                    val playP = peace(
                        title = titleC,
                        album = albumC,
                        artist = artistC,
                        id = idC,
                        duration = durationC,
                        path = pathC, artUri = artUriC
                    )
                    val file = File(playP.path)
                    if (file.exists())
                        tempList.add(playP)

                } while (cursor.moveToNext())
                cursor.close()
            }
        }


        return tempList
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!playerActivity.isPalying && playerActivity.peaceService != null) {
            existApplication()
        }
    }

  //  override fun onResume() {
    //    super.onResume()
        // for storing  data shared perference
//        val editor = getSharedPreferences("FAVOURITE", MODE_PRIVATE).edit()
//        val jsonString = GsonBuilder().create().toJson(favouriteActivity.FavouriteSongs)
//        editor.putString("favouriteSongs", jsonString)
//        editor.apply()
 //   }








    //for searching fun

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view_menu, menu)
        val SearchView = menu?.findItem(R.id.searchView)?.actionView as SearchView
        SearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            @SuppressLint("SuspiciousIndentation")
            override fun onQueryTextChange(newText: String?): Boolean {
                peaceListSearch = ArrayList()
                if (newText != null) {
                    val userInput = newText.lowercase()
                    for (song in peaceListMA)
                        if (song.title.lowercase().contains(userInput))
                            peaceListSearch.add(song)
                    search = true
                    peaceAdaptor.updatePeaceList(searchList = peaceListSearch)

                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


}