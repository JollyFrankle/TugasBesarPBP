package com.example.tugasbesarpbp

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.tugasbesarpbp.databinding.FragmentHomeBinding
import com.example.tugasbesarpbp.databinding.FragmentRegisterBinding
import com.example.tugasbesarpbp.entity.User
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnRegister: Button
    private lateinit var tilUsername: TextInputLayout
    private lateinit var tilEmail: TextInputLayout
    private lateinit var tilPassword: TextInputLayout
    private lateinit var tilPasswordConfirm: TextInputLayout
    private lateinit var tilTanggalLahir: TextInputLayout
    private lateinit var tilNomorTelepon: TextInputLayout
    private lateinit var tietTanggalLahir: TextInputEditText
    private lateinit var btnPickDateRegis: Button

    // tidak ada guna?
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    // pakai onViewCreated untuk mem-bind view yang sudah di inflate
    // --> https://stackoverflow.com/questions/51672231/kotlin-button-onclicklistener-event-inside-a-fragment && copilot
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        btnRegister = view.findViewById(R.id.btnRegister)
//        tilUsername = view.findViewById(R.id.tilRegisUsername)
//        tilEmail = view.findViewById(R.id.tilRegisEmail)
//        tilPassword = view.findViewById(R.id.tilRegisPassword)
//        tilPasswordConfirm = view.findViewById(R.id.tilRegisPasswordConfirm)
//        tilTanggalLahir = view.findViewById(R.id.tilRegisTanggalLahir)
//        tilNomorTelepon = view.findViewById(R.id.tilRegisNomorTelepon)
//        tietTanggalLahir = view.findViewById(R.id.tietRegisTanggalLahir)
//        btnPickDateRegis = view.findViewById(R.id.btnPickDate)

        _binding?.btnPickDate?.setOnClickListener(View.OnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog((activity as MainActivity), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                _binding?.tilRegisTanggalLahir?.editText?.setText("" + dayOfMonth + "/" + month + "/" + year)
            }, year, month, day)
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()

        })

        _binding?.btnRegister?.setOnClickListener {
            val username = _binding?.tilRegisUsername?.editText?.text.toString()
            val email = _binding?.tilRegisEmail?.editText?.text.toString()
            val password = _binding?.tilRegisPassword?.editText?.text.toString()
            val passwordConfirm = _binding?.tilRegisPasswordConfirm?.editText?.text.toString()
            val tanggalLahir = _binding?.tilRegisTanggalLahir?.editText?.text.toString()
            val nomorTelepon = _binding?.tilRegisNomorTelepon?.editText?.text.toString()

            var error = false

            // check if password and confirm password is same
            // check if password is less than 8 characters
            if (password.length < 8) {
                _binding?.tilRegisPassword?.error = "Password must be at least 8 characters"
                error = true
            } else if (password != passwordConfirm) {
                _binding?.tilRegisPassword?.error = "Password tidak sama"
                _binding?.tilRegisPasswordConfirm?.error = "Password tidak sama"
                error = true
            } else {
                _binding?.tilRegisPassword?.error = null
                _binding?.tilRegisPasswordConfirm?.error = null
            }

            // check if email format is valid using regex
            if (!email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                _binding?.tilRegisEmail?.error = "Email tidak valid"
                error = true
            } else {
                _binding?.tilRegisEmail?.error = null
            }

            // check if name is empty
            if (username.isEmpty()) {
                _binding?.tilRegisUsername?.error = "Username tidak boleh kosong"
                error = true
            } else {
                _binding?.tilRegisUsername?.error = null
            }

            if(tanggalLahir.isEmpty()){
                _binding?.tilRegisTanggalLahir?.error = "Tanggal lahir harus diisi"
                error = true
            } else{
                _binding?.tilRegisTanggalLahir?.error = null
            }

            if(nomorTelepon.length == 12){
                _binding?.tilRegisNomorTelepon?.error = null
            } else{
                _binding?.tilRegisNomorTelepon?.error = "Nomor telepon harus 12 digit"
                error = true
            }

            // check if username is already taken
            val listOfUser: MutableList<User> = User.getList()
            for (user in listOfUser) {
                if (user.getUsername() == username) {
                    _binding?.tilRegisUsername?.error = "Username sudah terdaftar"
                    error = true
                    break
                }
            }

            // all input is valid
            if (!error) {
                // add to User using add()
                val user = User(999, email, username, password, tglLahir = "2022-02-01", noTelp = "081234567890")
                User.add(user)

                Snackbar.make(view, "Register berhasil", Snackbar.LENGTH_SHORT).show()

                val loginFragment: Fragment = LoginFragment()
                // add username and password to bundle
                val bundle = Bundle()
                bundle.putString("username", username)
                bundle.putString("password", password)
                loginFragment.arguments = bundle

                // change fragment to login fragment
                (activity as MainActivity).changeFragment(loginFragment)
            } else {
                Snackbar.make(view, "Register gagal. Cek ulang bagian yang ditandai.", Snackbar.LENGTH_LONG).show()
            }
        }

        // btnRegisMoveToLogin on click
        _binding?.btnRegisMoveToLogin?.setOnClickListener {
            (activity as MainActivity).changeFragment(LoginFragment())
        }
    }
}