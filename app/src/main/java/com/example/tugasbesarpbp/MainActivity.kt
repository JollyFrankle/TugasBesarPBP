package com.example.tugasbesarpbp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import com.example.tugasbesarpbp.auth_ui.LoginFragment
import com.example.tugasbesarpbp.databinding.ActivityMainBinding
import com.example.tugasbesarpbp.other.HomeFragViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: HomeFragViewModel by viewModels()
    private lateinit var spSession: SharedPreferences

    private val CHANNEL_ID = "channel_notification_01"
    private val notificationId = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Shared Preferences
        spSession = getSharedPreferences("session", Context.MODE_PRIVATE)

        // set statusBarColor
        window.statusBarColor = resources.getColor(R.color.color_secondary_variant, null)

        createNotificationChannel()

        // get fragment from intent
        val action = intent.getStringExtra("action")
        println("------------------------------ " + action + intent.getBundleExtra("data").toString())
        if(action == "login") {
            val fragment = LoginFragment()
            fragment.arguments = intent.getBundleExtra("data")
            changeFragment(fragment)
        } else {
            // go to login fragment
            val fragment = LoginFragment()
            changeFragment(fragment)
        }
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    fun getSession(): SharedPreferences {
        return spSession
    }

    fun goToHome() {
        // go to home activity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Title"
            val descriptionText = "Notification Description"

            val channel1 = NotificationChannel(
                CHANNEL_ID,
                name,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel1)
        }
    }

    fun sendRegisterNotification(name: String, username: String, password: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        // Siapkan intent untuk membuka aplikasi ke bagian login, auto isi username dan password
        val broadcastIntent = Intent(this, NotificationReceiver::class.java)
        val bundle = Bundle()
        bundle.putString("goToFragment", "profile")
        bundle.putString("password", password)
        broadcastIntent.putExtra("action", "main_activity")
        broadcastIntent.putExtra("data", bundle)
        val actionIntent = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle("Halo, $name! 👋👋")
            .setContentText("Selamat bergabung di JogjaKost! Silakan login.")
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
            .setColor(Color.BLUE)
            .addAction(R.mipmap.ic_launcher, "Log in", actionIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val pictureBitmap = BitmapFactory.decodeResource(resources, R.drawable.img16x9welcome)
        // Big picture style
        val bigPictureStyle = NotificationCompat.BigPictureStyle(builder)
            .bigPicture(pictureBitmap)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, bigPictureStyle.build()!!)
        }
    }
}