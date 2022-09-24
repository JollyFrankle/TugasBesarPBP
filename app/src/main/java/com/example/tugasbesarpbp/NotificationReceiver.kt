package com.example.tugasbesarpbp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent){
        // get the action from the intent
        val action = intent.getStringExtra("action")
        when(action) {
            "main_activity" -> {
                // start main activity
                val mainActivityIntent = Intent(context, MainActivity::class.java)
                mainActivityIntent.putExtra("action", "login")
                mainActivityIntent.putExtra("data", intent.getBundleExtra("data"))
                mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(context!!, mainActivityIntent, null)
            }
        }
    }
}