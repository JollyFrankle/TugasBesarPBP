package com.example.tugasbesarpbp

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import com.example.tugasbesarpbp.api.http.UserApi
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
            val session = this.getSharedPreferences("session", Context.MODE_PRIVATE)
            if(session.getString("token", "")!!.isNotEmpty()) {
                UserApi.checkToken(this, {
                    // token masih valid
                }, {
                    // token tidak valid, tampilkan dialog kemudian buka MainActivity
                    if(it.statusCode == 401) {
                        Toast.makeText(this, "Token tidak valid, silahkan login kembali", Toast.LENGTH_SHORT).show()
                        session.edit().clear().apply()
                        this.goToMainActivity()
                    } else {
                        Toast.makeText(this, "Terjadi kesalahan saat hendak mengotentikasi ke server. Silakan login ulang.", Toast.LENGTH_SHORT).show()
                        session.edit().clear().apply()
                        this.goToMainActivity()
                    }
                })
                // langsung saja ke home activity dulu
                this.goToHomeActivity()
            } else {
                this.goToMainActivity()
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}