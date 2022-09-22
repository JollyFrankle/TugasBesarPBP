package com.example.tugasbesarpbp

import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var spSession: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Menu di bawah (bottom navigation/navigation bar)
        val btmMenu: NavigationBarView = findViewById(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        btmMenu.setupWithNavController(navHostFragment.navController)

        // Session identifier
        spSession = getSharedPreferences("session", MODE_PRIVATE)
    }

    // set title bar
    fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    // options menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_right_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // on options item selected
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.btnLogOut -> {
                // confirm
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Log Out")
                builder.setMessage("Are you sure?")
                builder.setPositiveButton("Yes") { dialog, which ->
                    // clear session
                    spSession.edit().clear().apply()

                    // go to login
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                builder.setNegativeButton("No") { dialog, which ->
                    // do nothing
                }
                val dialog: AlertDialog = builder.create()
                dialog.show()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun getSession(): SharedPreferences {
        return spSession
    }
}