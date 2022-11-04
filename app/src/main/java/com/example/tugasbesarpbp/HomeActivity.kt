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

        //
        val fragment = intent.extras?.getString("fragment")
        fragment?.let {
            when (it) {
                "item" -> btmMenu.selectedItemId = R.id.listItemFragment
                "profile" -> btmMenu.selectedItemId = R.id.profileFragment
            }
        }

        // Session identifier
        spSession = getSharedPreferences("session", MODE_PRIVATE)

        // set navigation bar item selected color
        btmMenu.itemActiveIndicatorColor = getColorStateList(R.color.bs_white)
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
                this.signOut()
            }

//            R.id.btnCreate -> {
//                val intent = Intent(this, CreateActivity::class.java)
//                intent.putExtra("action", CreateActivity.CREATE)
//                intent.putExtra("id", 0)
//                startActivity(intent)
//                finish()
//            }
        }
        return super.onOptionsItemSelected(item)
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

    fun goToActivity(activity: Class<*>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    fun changeMenu(fragment: String) {
        val menu: Int = when(fragment) {
            "home" -> R.id.homeFragment
            "profile" -> R.id.profileFragment
            else -> R.id.homeFragment
        }
        val btmMenu: NavigationBarView = findViewById(R.id.bottomNavigationView)
        btmMenu.selectedItemId = menu
        // refresh
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        navController.navigate(menu)
    }

    fun signOut() {
        // confirm
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
        builder.setMessage("Anda akan keluar dari aplikasi ini dan kredensial Anda akan dihapus.\r\nApakah Anda yakin?")
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