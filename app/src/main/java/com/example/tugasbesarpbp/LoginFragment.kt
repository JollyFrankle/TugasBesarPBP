package com.example.tugasbesarpbp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.tugasbesarpbp.room.user.User
import com.example.tugasbesarpbp.room.MainDB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        }

        btnLogin.setOnClickListener {
            val username = tilUsername.editText?.text.toString()
            val password = tilPassword.editText?.text.toString()

            // Check username and password
            val mainDB by lazy { MainDB((activity as MainActivity)) }

            // CoroutineScope untuk menjalankan fungsi async
            CoroutineScope(Dispatchers.IO).launch {
                val user: User? = mainDB.UserDao().getUserByCred(username, password)

                if (user != null) {
                    // login success
                    // save session
                    val session = (activity as MainActivity).getSession()
                    session.edit().putInt("id", user.id).apply()

                    // go to home activity
                    (activity as MainActivity).goToHome()
                } else {
                    // login failed
                    Snackbar.make(view, "Login failed", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        // btnLoginMoveToRegis on click
        view.findViewById<Button>(R.id.btnLoginMoveToRegis).setOnClickListener {
            val fragment = RegisterFragment()
            (activity as MainActivity).changeFragment(fragment)
        }
    }
}