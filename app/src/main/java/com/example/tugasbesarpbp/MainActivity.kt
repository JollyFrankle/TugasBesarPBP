package com.example.tugasbesarpbp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.tugasbesarpbp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // set statusBarColor
        window.statusBarColor = resources.getColor(R.color.color_secondary_variant, null)

        // go to login fragment
        val fragment = LoginFragment()
        changeFragment(fragment)
    }

    fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit()
    }

    fun goToHome() {
        // go to home activity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}