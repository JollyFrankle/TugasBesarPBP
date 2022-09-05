package com.example.tugasbesarpbp

import android.os.Bundle
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
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var btnRegister: Button
    private lateinit var tilName: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilPasswordConfirm: TextInputLayout

    // tidak ada guna?
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    // pakai onViewCreated untuk mem-bind view yang sudah di inflate
    // --> https://stackoverflow.com/questions/51672231/kotlin-button-onclicklistener-event-inside-a-fragment && copilot
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnRegister = view.findViewById(R.id.btnRegister)
        tilName = view.findViewById(R.id.tilRegisName)
        tilEmail = view.findViewById(R.id.tilRegisEmail)
        tilPassword = view.findViewById(R.id.tilRegisPassword)
        tilPasswordConfirm = view.findViewById(R.id.tilRegisPasswordConfirm)

        btnRegister.setOnClickListener {
            val name = tilName.editText?.text.toString()
            val email = tilEmail.editText?.text.toString()
            val password = tilPassword.editText?.text.toString()
            val passwordConfirm = tilPasswordConfirm.editText?.text.toString()

            var error = false

            // check if password and confirm password is same
            if (password != passwordConfirm) {
                tilPassword.error = "Password tidak sama"
                tilPasswordConfirm.error = "Password tidak sama"
                error = true
            } else {
                tilPassword.error = null
                tilPasswordConfirm.error = null
            }

            // check if email format is valid
            if (!email.contains("@")) {
                tilEmail.error = "Email tidak valid"
                error = true
            } else {
                tilEmail.error = null
            }

            // check if name is empty
            if (name.isEmpty()) {
                tilName.error = "Nama tidak boleh kosong"
                error = true
            } else {
                tilName.error = null
            }

            // all input is valid
            if (!error) {
                Snackbar.make(view, "Register berhasil", Snackbar.LENGTH_SHORT).show()

                // go to home activity
                (activity as MainActivity).goToHome()
            } else {
                Snackbar.make(view, "Register gagal. Cek ulang bagian yang ditandai.", Snackbar.LENGTH_LONG).show()
            }
        }

        // txtRegisChangeFragment on click
        view.findViewById<TextView>(R.id.txtRegisChangeFragment).setOnClickListener {
            val fragment = LoginFragment()
            (activity as MainActivity).changeFragment(fragment)
        }
    }
}