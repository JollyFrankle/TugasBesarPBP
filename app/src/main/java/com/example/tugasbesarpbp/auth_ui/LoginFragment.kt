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
import com.example.tugasbesarpbp.MainActivity
import com.example.tugasbesarpbp.R
import com.example.tugasbesarpbp.api.http.UserApi
import com.google.android.material.textfield.TextInputLayout
import com.shashank.sony.fancytoastlib.FancyToast
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
//            if(username.isNotBlank() && password.isNotEmpty()) {
            btnLogin.isEnabled = false
            UserApi.cekLogin(requireActivity(), username, password, {
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
                    if(it.statusCode == 401) {
                        FancyToast.makeText(requireContext(), "Username atau password salah", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
                    } else {
                        AlertDialog.Builder(requireActivity())
                            .setTitle("Terjadi Kesalahan!")
                            .setMessage("Kode error: " + it.statusCode + "\r\nHubungi admin.")
                            .setPositiveButton("OK", null)
                            .show()
                    }

                    // set login button enabled
                    btnLogin.isEnabled = true
                })
            } else {
                FancyToast.makeText(requireContext(), "Username atau password tidak boleh kosong", FancyToast.LENGTH_LONG, FancyToast.ERROR, false).show()
            }
        }

        // btnLoginMoveToRegis on click
        view.findViewById<Button>(R.id.btnLoginMoveToRegis).setOnClickListener {
            val fragment = RegisterFragment()
            (activity as MainActivity).changeFragment(fragment)
        }
    }
}