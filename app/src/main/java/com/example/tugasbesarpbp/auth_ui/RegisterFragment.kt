package com.example.tugasbesarpbp.auth_ui

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.StringRequest
import com.example.tugasbesarpbp.MainActivity
import com.example.tugasbesarpbp.api.UserApi
import com.example.tugasbesarpbp.databinding.FragmentRegisterBinding
import com.example.tugasbesarpbp.room.user.User
import com.example.tugasbesarpbp.room.MainDB
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    // pakai onViewCreated untuk mem-bind view yang sudah di inflate
    // --> https://stackoverflow.com/questions/51672231/kotlin-button-onclicklistener-event-inside-a-fragment && copilot
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db by lazy { MainDB(activity as MainActivity) }
        val userDao = db.UserDao()

        binding.btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog((activity as MainActivity), DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                binding.tilRegisTanggalLahir.editText?.setText(year.toString() + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth))
            }, year, month, day)
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        binding.btnRegister.setOnClickListener {
            val username = binding.tilRegisUsername.editText?.text.toString()
            val email = binding.tilRegisEmail.editText?.text.toString()
            val password = binding.tilRegisPassword.editText?.text.toString()
            val passwordConfirm = binding.tilRegisPasswordConfirm.editText?.text.toString()
            val tanggalLahir = binding.tilRegisTanggalLahir.editText?.text.toString()
            val nomorTelepon = binding.tilRegisNomorTelepon.editText?.text.toString()
            val name = binding.tilRegisNama.editText?.text.toString()

            var error = false

//            CoroutineScope(Dispatchers.IO).launch {
//                withContext(Dispatchers.Main) {
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
                binding.tilRegisUsername.error = null
            }

            // check if username is empty
            if (name.isEmpty()) {
                binding.tilRegisNama.error = "Nama tidak boleh kosong"
                error = true
            } else {
                binding.tilRegisNama.error = null
            }

            if(tanggalLahir.isEmpty()){
                binding.tilRegisTanggalLahir.error = "Tanggal lahir harus diisi"
                error = true
            } else{
                binding.tilRegisTanggalLahir.error = null
            }

            if(nomorTelepon.length == 12){
                binding.tilRegisNomorTelepon.error = null
            } else{
                binding.tilRegisNomorTelepon.error = "Nomor telepon harus 12 digit"
                error = true
            }

//                    if (!error) {
//                        // check if username is already taken
//                        if (userDao.getUserByUsernameOrEmail(username, email) != null) {
//                            binding.tilRegisUsername.error =
//                                "Username atau email sudah terdaftar"
//                            binding.tilRegisEmail.error = "Username atau email sudah terdaftar"
//                            error = true
//                        } else {
//                            binding.tilRegisUsername.error = null
//                            binding.tilRegisEmail.error = null
//                        }
//                    }

                    // all input is valid
                    if(!error) {
                        val user = com.example.tugasbesarpbp.api_models.User(
                            username = username,
                            email = email,
                            password = password,
                            nama = name,
                            tanggalLahir = tanggalLahir,
                            nomorTelepon = nomorTelepon
                        )

                        this.register(user)
//                        val user = User(0, name, username, password, email, tanggalLahir, nomorTelepon)
//                        userDao.addUser(user)

//                        Snackbar.make(view, "Register berhasil!", Snackbar.LENGTH_SHORT).show()

//                        val loginFragment: Fragment = LoginFragment()
//                        // add username and password to bundle --> TIDAK LAGI DIGUNAKAN: Username & password akan muncul kalau user click di notifikasi (implementasi di bawah)
////                        val bundle = Bundle()
////                        bundle.putString("username", username)
////                        bundle.putString("password", password)
////                        loginFragment.arguments = bundle
//
//                        // change fragment to login fragment
//                        (activity as MainActivity).changeFragment(loginFragment)

                        // Passing: name akan ditampilkan di notifikasi, username dan password akan ditampilkan di login fragment apabila user men-click "Log in" di notifikasi
//                        (activity as MainActivity).sendRegisterNotification(name, username, password)

//                        Snackbar.make(view, "Register berhasil.\r\nSilakan login melalui notifikasi yang dikirimkan.", Snackbar.LENGTH_SHORT).show()
                    } else {
                        Snackbar.make(
                            view,
                            "Register gagal. Cek ulang bagian yang ditandai.",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
//                }
//            }
        }

        // btnRegisMoveToLogin on click
        binding.btnRegisMoveToLogin.setOnClickListener {
            (activity as MainActivity).changeFragment(LoginFragment())
        }
    }

    private fun register(user: com.example.tugasbesarpbp.api_models.User) {
        // disable button
        binding.btnRegister.isEnabled = false

        val stringRequest: StringRequest = object: StringRequest(Method.POST, UserApi.REGISTER_URL, {
            val jsonObject = JSONObject(it)

            // send notification
            (activity as MainActivity).sendRegisterNotification(user.nama, user.username, user.password!!)

            Snackbar.make(requireView(), "Register berhasil.\r\nSilakan login melalui notifikasi yang dikirimkan.", Snackbar.LENGTH_SHORT).show()

            // change fragment to login fragment
            val loginFragment: Fragment = LoginFragment()
            (activity as MainActivity).changeFragment(loginFragment)
        }, {
            var respObj: JSONObject? = null
            try {
                respObj = JSONObject(String(it.networkResponse.data))
                if(it.networkResponse.statusCode.toString().startsWith("4")) {
                    AlertDialog.Builder(requireActivity())
                        .setTitle("Terjadi Kesalahan!")
                        .setMessage(respObj.getString("message"))
                        .setPositiveButton("OK", null)
                        .show()
                    // for each errors
                    val errors = respObj.getJSONObject("errors")
                    for (key in errors.keys()) {
                        val error = errors.getJSONArray(key)
                        when (key) {
                            "username" -> binding.tilRegisUsername.error = error.getString(0)
                            "email" -> binding.tilRegisEmail.error = error.getString(0)
                            "password" -> binding.tilRegisPassword.error = error.getString(0)
                            "nama" -> binding.tilRegisNama.error = error.getString(0)
                            "tanggalLahir" -> binding.tilRegisTanggalLahir.error = error.getString(0)
                            "nomorTelepon" -> binding.tilRegisNomorTelepon.error = error.getString(0)
                        }
                    }
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

            // enable button
            binding.btnRegister.isEnabled = true
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getBody(): ByteArray {
                val gson = Gson()
                val json = gson.toJson(user)
                return json.toByteArray(StandardCharsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        (activity as MainActivity).queue!!.add(stringRequest)
    }
}