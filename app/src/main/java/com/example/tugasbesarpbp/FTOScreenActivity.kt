package com.example.tugasbesarpbp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.user.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            // Add default username & password to database
            val mainDB by lazy { MainDB(this) }
            val userDAO = mainDB.UserDao()

            val userAdmin = User(1, "Administrator", "admin", "admin", "admin@www.com", "2022-01-01", "083456789012")
            CoroutineScope(Dispatchers.IO).launch {
                userDAO.addUser(userAdmin)
            }

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