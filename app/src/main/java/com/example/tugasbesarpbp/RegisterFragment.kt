package com.example.tugasbesarpbp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.tugasbesarpbp.databinding.FragmentHomeBinding
import com.example.tugasbesarpbp.databinding.FragmentRegisterBinding
import com.example.tugasbesarpbp.room.user.User
import com.example.tugasbesarpbp.room.MainDB
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

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

        val db by lazy { MainDB(activity as MainActivity) }
        val userDao = db.UserDao()
//        btnRegister = view.findViewById(R.id.btnRegister)
//        tilUsername = view.findViewById(R.id.tilRegisUsername)
//        tilEmail = view.findViewById(R.id.tilRegisEmail)
//        tilPassword = view.findViewById(R.id.tilRegisPassword)
//        tilPasswordConfirm = view.findViewById(R.id.tilRegisPasswordConfirm)
//        tilTanggalLahir = view.findViewById(R.id.tilRegisTanggalLahir)
//        tilNomorTelepon = view.findViewById(R.id.tilRegisNomorTelepon)
//        tietTanggalLahir = view.findViewById(R.id.tietRegisTanggalLahir)
//        btnPickDateRegis = view.findViewById(R.id.btnPickDate)

        binding.btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog((activity as MainActivity), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                binding.tilRegisTanggalLahir?.editText?.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month+1) + "/" + year)
            }, year, month, day)
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        binding.btnRegister?.setOnClickListener {
            val username = binding.tilRegisUsername.editText?.text.toString()
            val email = binding.tilRegisEmail.editText?.text.toString()
            val password = binding.tilRegisPassword.editText?.text.toString()
            val passwordConfirm = binding.tilRegisPasswordConfirm.editText?.text.toString()
            val tanggalLahir = binding.tilRegisTanggalLahir.editText?.text.toString()
            val nomorTelepon = binding.tilRegisNomorTelepon.editText?.text.toString()
            val name = binding.tilRegisNama.editText?.text.toString()

            var error = false

            CoroutineScope(Dispatchers.IO).launch {
                withContext(Dispatchers.Main) {
                    if (password.length < 8) {
                        // check if password is less than 8 characters
                        binding.tilRegisPassword.error = "Password must be at least 8 characters"
                        error = true
                    } else if (password != passwordConfirm) {
                        // check if password and confirm password is same
                        binding.tilRegisPassword.error = "Password tidak sama"
                        binding.tilRegisPasswordConfirm.error = "Password tidak sama"
                        error = true
                    } else {
                        binding.tilRegisPassword.error = null
                        binding.tilRegisPasswordConfirm.error = null
                    }

                    // check if email format is valid using regex
                    if (!email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                        binding.tilRegisEmail.error = "Email tidak valid"
                        error = true
                    } else {
                        binding.tilRegisEmail.error = null
                    }

                    // check if name is empty
                    if (username.isEmpty()) {
                        binding.tilRegisUsername.error = "Username tidak boleh kosong"
                        error = true
                    } else {
                        binding.tilRegisUsername?.error = null
                    }

                    // check if username is empty
                    if (name.isEmpty()) {
                        binding.tilRegisNama.error = "Nama tidak boleh kosong"
                        error = true
                    } else {
                        binding.tilRegisNama.error = null
                    }

                    if(tanggalLahir.isEmpty()){
                        binding.tilRegisTanggalLahir?.error = "Tanggal lahir harus diisi"
                        error = true
                    } else{
                        binding.tilRegisTanggalLahir?.error = null
                    }

                    if(nomorTelepon.length == 12){
                        binding.tilRegisNomorTelepon?.error = null
                    } else{
                        binding.tilRegisNomorTelepon?.error = "Nomor telepon harus 12 digit"
                        error = true
                    }

                    if (!error) {
                        // check if username is already taken
                        if (userDao.getUserByUsernameOrEmail(username, email) != null) {
                            binding.tilRegisUsername?.error =
                                "Username atau email sudah terdaftar"
                            binding.tilRegisEmail?.error = "Username atau email sudah terdaftar"
                            error = true
                        } else {
                            binding.tilRegisUsername?.error = null
                            binding.tilRegisEmail?.error = null
                        }
                    }

                    // all input is valid
                    if(!error) {
                        val user =
                            User(0, name, username, password, email, tanggalLahir, nomorTelepon)
                        userDao.addUser(user)

                        Snackbar.make(view, "Register berhasil!", Snackbar.LENGTH_SHORT).show()

                        val loginFragment: Fragment = LoginFragment()
                        // add username and password to bundle --> TIDAK LAGI DIGUNAKAN: Username & password akan muncul kalau user click di notifikasi (implementasi di bawah)
//                        val bundle = Bundle()
//                        bundle.putString("username", username)
//                        bundle.putString("password", password)
//                        loginFragment.arguments = bundle

                        // change fragment to login fragment
                        (activity as MainActivity).changeFragment(loginFragment)

                        // Passing: name akan ditampilkan di notifikasi, username dan password akan ditampilkan di login fragment apabila user men-click "Log in" di notifikasi
                        (activity as MainActivity).sendRegisterNotification(name, username, password)

                        Snackbar.make(view, "Register berhasil.\r\nSilakan login melalui notifikasi yang dikirimkan.", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(
                            view,
                            "Register gagal. Cek ulang bagian yang ditandai.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        // btnRegisMoveToLogin on click
        binding.btnRegisMoveToLogin?.setOnClickListener {
            (activity as MainActivity).changeFragment(LoginFragment())
        }
    }
}