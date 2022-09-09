package com.example.tugasbesarpbp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tugasbesarpbp.main_ui.HomeFragment
import com.example.tugasbesarpbp.main_ui.ListItemFragment
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btmMenu: NavigationBarView = findViewById(R.id.bottomNavigationView)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        btmMenu.setupWithNavController(navHostFragment.navController)
    }

    // change fragment
    private fun changeFragment(fragment: Fragment) {
        // change fragment with animation
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.frameLayout, fragment)
            .commit()
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
                    // log out
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
}