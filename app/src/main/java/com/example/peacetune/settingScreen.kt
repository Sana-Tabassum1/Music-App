
package com.example.peacetune

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.peacetune.databinding.ActivitySettingScreenBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class settingScreen : AppCompatActivity() {

    lateinit var binding: ActivitySettingScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(MainActivity.CurrentThemeNav[MainActivity.themeIndex])
        binding= ActivitySettingScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title="setting"
        when(MainActivity.themeIndex){
            0 -> binding.coolpinkTheme.setBackgroundColor(Color.YELLOW)
            1 -> binding.coolblueTheme.setBackgroundColor(Color.YELLOW)
            2 -> binding.coolPurpleTheme.setBackgroundColor(Color.YELLOW)
            3 -> binding.coolgreenTheme.setBackgroundColor(Color.YELLOW)
            4 -> binding.coolBlackTheme.setBackgroundColor(Color.YELLOW)
        }

        binding.coolpinkTheme.setOnClickListener { saveTheme(0)}
        binding.coolblueTheme.setOnClickListener { saveTheme(1)}
        binding.coolPurpleTheme.setOnClickListener { saveTheme(2)}
        binding.coolgreenTheme.setOnClickListener { saveTheme(3)}
        binding.coolBlackTheme.setOnClickListener { saveTheme(4)}
    }

    private fun saveTheme(index:Int){
        if(MainActivity.themeIndex != index){
            val editor=getSharedPreferences("THEME", MODE_PRIVATE).edit()
            editor.putInt("themeIndex",index)
            editor.apply()
            val builder = MaterialAlertDialogBuilder(this)
            builder.setTitle("Apply Theme")
                .setMessage("Do You Want To apply theme")
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

}