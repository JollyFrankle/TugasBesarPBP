package com.example.tugasbesarpbp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.tugasbesarpbp.api.http.UserApi
import com.google.android.material.navigation.NavigationBarView

class HomeActivity : AppCompatActivity() {
    private lateinit var spSession: SharedPreferences
    private lateinit var btmMenu: NavigationBarView
    lateinit var navHostFragment: NavHostFragment

    private lateinit var loader: ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Menu di bawah (bottom navigation/navigation bar)
        btmMenu = findViewById(R.id.bottomNavigationView)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        btmMenu.setupWithNavController(navHostFragment.navController)
        btmMenu.selectedItemId = R.id.homeFragment

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

        // hide action bar
        supportActionBar?.hide()

        // loader
        loader = findViewById<View>(R.id.layoutLoader).findViewById(R.id.layoutLoader)
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

    fun setLoadingScreen(state: Boolean){
        if (state) {
            // fade in
            loader.alpha = 0f
            loader.visibility = View.VISIBLE
            loader.animate().alpha(1f).duration = 250

            // set flag to disable click
            loader.isClickable = true
        } else {
            // fade out
            loader.animate().alpha(0f).setDuration(250).withEndAction {
                loader.visibility = View.GONE
            }
            // set flag to enable click
            loader.isClickable = false
        }
    }

    fun signOut() {
        // confirm
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
        builder.setMessage("Anda akan keluar dari aplikasi ini dan username serta password akan dilupakan oleh sistem.\r\nApakah Anda yakin?")
        builder.setPositiveButton("Yes") { dialog, which ->
            UserApi.logout(this, {
                spSession.edit().clear().apply()

                // go to login
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            })
            // clear session
//            spSession.edit().clear().apply()
//
//            // go to login
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
        }
        builder.setNegativeButton("No") { dialog, which ->
            // do nothing
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    override fun onBackPressed() {
        // confirm
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Keluar")
        builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi ini?")
        builder.setPositiveButton("Yes") { dialog, which ->
            finish()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}