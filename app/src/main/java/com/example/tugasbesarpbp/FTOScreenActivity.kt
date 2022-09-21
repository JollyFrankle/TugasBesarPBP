package com.example.tugasbesarpbp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer

/*
 * First-Time Opened Screen Activity
 * "Splash screen" on first time application opened.
 */

class FTOScreenActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get shared preference
        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        var isFirstTimeOpened = true
        if(sharedPreferences.contains("showSplash")) {
            isFirstTimeOpened = sharedPreferences.getBoolean("showSplash", true)
        }

        // If first time opened, open FTOScreenActivity
        if (isFirstTimeOpened) {
            setContentView(R.layout.activity_ftoscreen)
            // set timer for 3 seconds
            val timer = object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    // do nothing
                }

                override fun onFinish() {
                    // set firstTimeOpened to false
                    sharedPreferences
                        .edit()
                        .putBoolean("showSplash", false)
                        .apply()
                    // open MainActivity
                    this@FTOScreenActivity.goToMainActivity()
                }
            }
            timer.start()
        } else {
            this.goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}