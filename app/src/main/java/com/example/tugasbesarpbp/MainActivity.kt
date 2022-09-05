package com.example.tugasbesarpbp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // set statusBarColor
        window.statusBarColor = resources.getColor(R.color.color_secondary_variant, null)

        // go to login fragment
        val fragment = LoginFragment()
        changeFragment(fragment)
    }

    // change fragment
    fun changeFragmentCopilot(fragment: Fragment) {
        // sama sih dengan di bawah
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.commit()
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
    }
}