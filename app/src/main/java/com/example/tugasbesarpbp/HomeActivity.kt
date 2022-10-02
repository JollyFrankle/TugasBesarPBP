package com.example.tugasbesarpbp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationBarView
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var spSession: SharedPreferences
    private lateinit var btmMenu: NavigationBarView
    lateinit var navHostFragment: NavHostFragment

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
        val navController = navHostFragment.navController
        navController.navigate(menu)
    }

    fun signOut() {
        // confirm
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
        builder.setMessage("Anda akan keluar dari aplikasi ini dan username serta password akan dilupakan oleh sistem.\r\nApakah Anda yakin?")
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

    val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
//            val fragment = data?.extras?.getString("fragment")
//            fragment?.let {
//                when (it) {
//                    "item" -> btmMenu.selectedItemId = R.id.listItemFragment
//                    "profile" -> btmMenu.selectedItemId = R.id.profileFragment
//                }
//            }
            // refresh current fragment
            val navController = navHostFragment.navController
            navController.navigate(btmMenu.selectedItemId)
//            println("_________________________ result: $fragment")
        }
    }

    override fun onBackPressed() {
//        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
//        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
//        if (currentFragment is HomeFragment) {
            // confirm
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Keluar")
            builder.setMessage("Apakah Anda yakin ingin keluar dari aplikasi ini?")
            builder.setPositiveButton("Yes") { dialog, which ->
                finish()
            }
            builder.setNegativeButton("No") { dialog, which ->
                // do nothing
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()
//        } else {
//            super.onBackPressed()
//        }
    }

    // change fragment
//    fun changeFragment(dest: Int, args: Bundle? = null) {
//        navHostFragment.navController.navigate(dest, args)
//    }
}