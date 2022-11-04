package com.example.tugasbesarpbp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity

class NotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent){
        // get the action from the intent
        val action = intent.getStringExtra("action")
        when(action) {
            "main_activity" -> {
                // Pergi ke main activity, fragment login, dan tampilkan data username dan password yang sudah dipass dari notification
                // Implementasi notification: file MainActivity.kt --> function sendRegisterNotification()
                val mainActivityIntent = Intent(context, MainActivity::class.java)
                mainActivityIntent.putExtra("action", "login")
                mainActivityIntent.putExtra("data", intent.getBundleExtra("data"))
                mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(context!!, mainActivityIntent, null)
            }
            "show_kost" -> {
                // Intent CreateActivity menerima
                // - "action" (tindakan apa? Read, Create, atau Edit)
                // - "id" (kalau tindakan Edit atau Read, maka mau tampilkan data dengan id berapa?) --> diteruskan dari notification
                val showIntent = Intent(context, CRUDKostActivity::class.java)
                showIntent.putExtra("action", CRUDKostActivity.READ)
                showIntent.putExtra("id", intent.getIntExtra("data", 0))
                showIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(context!!, showIntent, null)
            }
        }
    }
}