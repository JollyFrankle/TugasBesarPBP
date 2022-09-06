package com.example.tugasbesarpbp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    // aaa  www
    private lateinit var btnLogin: Button
    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilPassword: TextInputLayout

    private var username: String = ""
    private var password: String = ""

    // array of accepted username and password
    private val accepted = arrayOf(
        "admin" to "admin",
        "user" to "user"
    )

    // check if username and password is valid (copilot)
    private fun isValid(username: String, password: String): Boolean {
        return accepted.contains(username to password)
    }

    // tidak ada guna?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

        // if username & password is not null, show it
        if (username.isNotEmpty() && password.isNotEmpty()) {
            tilUsername.editText?.setText(username)
            tilPassword.editText?.setText(password)

            // add username and password to accepted
            accepted.plus(username to password)

            // print accepted
            for(i in accepted) {
                Log.d("accepted", i.toString())
            }
        }

        btnLogin.setOnClickListener {
            val username = tilUsername.editText?.text.toString()
            val password = tilPassword.editText?.text.toString()

            if (isValid(username, password)) {
                // login success
//                Snackbar.make(view, "Login success", Snackbar.LENGTH_SHORT).show()

                // go to home activity
                (activity as MainActivity).goToHome()
            } else {
                // login failed
                Snackbar.make(view, "Login failed", Snackbar.LENGTH_SHORT).show()
            }
        }

        // btnLoginMoveToRegis on click
        view.findViewById<Button>(R.id.btnLoginMoveToRegis).setOnClickListener {
            val fragment = RegisterFragment()
            (activity as MainActivity).changeFragment(fragment)
        }

        /// aaaa
    }
}