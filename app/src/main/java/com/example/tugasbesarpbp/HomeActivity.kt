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
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.UserApi
import com.example.tugasbesarpbp.auth_ui.LoginFragment
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class HomeActivity : AppCompatActivity() {
    private lateinit var spSession: SharedPreferences
    private lateinit var btmMenu: NavigationBarView
    lateinit var navHostFragment: NavHostFragment

    var queue: RequestQueue? = null

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

        // Volley Queue
        queue = Volley.newRequestQueue(this)

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

    fun signOut() {
        // confirm
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sign out")
        builder.setMessage("Anda akan keluar dari aplikasi ini dan username serta password akan dilupakan oleh sistem.\r\nApakah Anda yakin?")
        builder.setPositiveButton("Yes") { dialog, which ->
            logOutWeb()
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
        builder.setNegativeButton("No", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun logOutWeb() {
        val token = spSession.getString("token", null)
        val stringRequest: StringRequest = object: StringRequest(Method.POST, UserApi.LOGOUT_URL, {
//            val jsonObject = JSONObject(it)

            spSession.edit().clear().apply()

            // go to login
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            // change fragment to login fragment
            finish()
        }, {
            var respObj: JSONObject? = null
            try {
                respObj = JSONObject(String(it.networkResponse.data))
                if(it.networkResponse.statusCode.toString().startsWith("4")) {
                    AlertDialog.Builder(this)
                        .setTitle("Terjadi Kesalahan!")
                        .setMessage(respObj.getString("message"))
                        .setPositiveButton("OK", null)
                        .show()
                    // for each errors
                } else {
                    AlertDialog.Builder(this)
                        .setTitle("Terjadi Kesalahan!")
                        .setMessage("Kode error: " + it.networkResponse.statusCode + "\r\nHubungi admin.")
                        .setPositiveButton("OK", null)
                        .show()
                }
            } catch (e: Exception) {
                val response = it.networkResponse
                var dialogContent = ""
                if(response != null) {
                    dialogContent = "Error ${response.statusCode}\r\nHubungi admin."
                } else {
                    dialogContent = "Tidak dapat terhubung ke server.\r\nPeriksa koneksi internet."
                }
                AlertDialog.Builder(this)
                    .setMessage(dialogContent)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                // Dapatkan token dari session dan tambahkan ke header
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + token
                return headers
            }
        }
        queue!!.add(stringRequest)
    }
}