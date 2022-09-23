package com.example.tugasbesarpbp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import com.example.tugasbesarpbp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels()
    private lateinit var spSession: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Shared Preferences
        spSession = getSharedPreferences("session", Context.MODE_PRIVATE)

        // set statusBarColor
        window.statusBarColor = resources.getColor(R.color.color_secondary_variant, null)

        // go to login fragment
        val fragment = LoginFragment()
        changeFragment(fragment)
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
}