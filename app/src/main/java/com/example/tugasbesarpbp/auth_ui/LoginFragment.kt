package com.example.tugasbesarpbp.auth_ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.tugasbesarpbp.MainActivity
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.api.UserApi
import com.example.tugasbesarpbp.room.user.User
import com.example.tugasbesarpbp.room.MainDB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

class LoginFragment : Fragment() {
    // aaa  www
    private lateinit var btnLogin: Button
    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout

    private var username: String = ""
    private var password: String = ""

    private lateinit var spSession: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shared Preferences
        spSession = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)

        // get username and password from shared preferences
//        val session = (activity as MainActivity).getSession()
        username = spSession.getString("username", "").toString()
        password = spSession.getString("password", "").toString()

        // get username and password from arguments
        arguments?.let {
            username = it.getString("username").toString()
            password = it.getString("password").toString()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    // pakai onViewCreated untuk mem-bind view yang sudah di inflate
    // --> https://stackoverflow.com/questions/51672231/kotlin-button-onclicklistener-event-inside-a-fragment && copilot
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnLogin = view.findViewById(R.id.btnLogin)
        tilUsername = view.findViewById(R.id.tilLoginUsername)
        tilPassword = view.findViewById(R.id.tilLoginPassword)

        val session: SharedPreferences = (activity as MainActivity).getSession()
        if(session.contains("username")) {
            tilUsername.editText?.setText(session.getString("username", ""))
        }
        if(session.contains("password")) {
            tilUsername.editText?.setText(session.getString("username", ""))
        }

        // if username & password is not null, show it
        if (username.isNotEmpty() && password.isNotEmpty()) {
            tilUsername.editText?.setText(username)
            tilPassword.editText?.setText(password)
        }

        btnLogin.setOnClickListener {
            val username = tilUsername.editText?.text.toString()
            val password = tilPassword.editText?.text.toString()

            // check username and password (Volley)
            if(username.isNotBlank() && password.isNotEmpty()) {
                login(username, password)
            } else {
                Snackbar.make(view, "Username dan password harus diisi!", Snackbar.LENGTH_SHORT).show()
            }
            // Check username and password
//            val mainDB by lazy { MainDB((activity as MainActivity)) }

            // CoroutineScope untuk menjalankan fungsi async
//            CoroutineScope(Dispatchers.IO).launch {
//                val user: User? = mainDB.UserDao().getUserByCred(username, password)
//
//                if (user != null) {
//                    // login success
//                    // save session
//                    spSession.edit()
//                        .putInt("id", user.id)
//                        .putString("username", user.username)
//                        .putString("password", user.password)
//                        .apply()
//
//                    // go to home activity
//                    (activity as MainActivity).goToHome()
//                } else {
//                    // login failed
//                    Snackbar.make(view, "Login failed", Snackbar.LENGTH_SHORT).show()
//                }
//            }
        }

        // btnLoginMoveToRegis on click
        view.findViewById<Button>(R.id.btnLoginMoveToRegis).setOnClickListener {
            val fragment = RegisterFragment()
            (activity as MainActivity).changeFragment(fragment)
        }
    }

    private fun login(username: String, password: String) {
        // set login button disabled
        btnLogin.isEnabled = false

        val stringRequest: StringRequest = object: StringRequest(Method.POST, UserApi.LOGIN_URL, {
            val jsonObject = JSONObject(it)
            val token = jsonObject.getString("access_token")
            val id = jsonObject.getJSONObject("user").getLong("id")

            // save to shared preferences
            spSession.edit()
                .putString("token", token)
                .putLong("id", id)
                .putString("username", username)
                .putString("password", password)
                .apply()

            (activity as MainActivity).goToHome()
        }, {
            var respObj: JSONObject? = null
            try {
                respObj = JSONObject(String(it.networkResponse.data))
                if(it.networkResponse.statusCode == 401) {
                    Snackbar.make(requireView(), "Username atau password salah!", Snackbar.LENGTH_LONG).show()
                } else {
                    AlertDialog.Builder(requireActivity())
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
                AlertDialog.Builder(requireActivity())
                    .setMessage(dialogContent)
                    .setPositiveButton("OK", null)
                    .show()
            }

            // set login button enabled
            btnLogin.isEnabled = true
        }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["username"] = username
                params["password"] = password
                return params
            }
        }
        (activity as MainActivity).queue!!.add(stringRequest)
    }
}