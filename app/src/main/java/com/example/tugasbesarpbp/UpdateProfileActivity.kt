package com.example.tugasbesarpbp

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.tugasbesarpbp.api.UserApi
import com.example.tugasbesarpbp.databinding.ActivityUpdateBinding
import com.example.tugasbesarpbp.room.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.tugasbesarpbp.room.user.User
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {
    private var _binding: ActivityUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var spSession: SharedPreferences

    private var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Shared Preferences
        spSession = getSharedPreferences("session", Context.MODE_PRIVATE)

        // show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // initialise queue
        queue = Volley.newRequestQueue(this)

        // Load data
        getUserDetails()

//        val db by lazy { MainDB(this) }
//        val userDao = db.UserDao()

        // load data
//        CoroutineScope(Dispatchers.IO).launch {
//            val user: User? = db.UserDao().getUserById(spSession.getInt("id", 0))
//            if (user != null) {
//                withContext(Dispatchers.Main) {
//                    binding.tilUpdProfNama.editText?.setText(user.nama)
//                    binding.tilUpdProfUsername.editText?.setText(user.username)
//                    binding.tilUpdProfEmail.editText?.setText(user.email)
//                    binding.tilUpdProfTanggalLahir.editText?.setText(user.tanggalLahir)
//                    binding.tilUpdProfNomorTelepon.editText?.setText(user.nomorTelepon)
//                }
//            }
//        }

        binding.btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView: YYYY-MM-DD
                    _binding?.tilUpdProfTanggalLahir?.editText?.setText(year.toString() + "-" + String.format("%02d", month+1) + "-" + String.format("%02d", dayOfMonth))
                },
                year,
                month,
                day
            )
            dpd.datePicker.maxDate = System.currentTimeMillis()
            dpd.show()
        }

        binding.btnUpdProfile.setOnClickListener {
            val name = binding.tilUpdProfNama.editText?.text.toString()
            val email = binding.tilUpdProfEmail.editText?.text.toString()
            val tanggalLahir = binding.tilUpdProfTanggalLahir.editText?.text.toString()
            val nomorTelepon = binding.tilUpdProfNomorTelepon.editText?.text.toString()

            var error = false

            // check if email format is valid using regex
            if (!email.matches(Regex("^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})"))) {
                binding.tilUpdProfEmail.error = "Email tidak valid"
                error = true
            } else {
                binding.tilUpdProfEmail.error = null
            }

            // check if username is empty
            if (name.isEmpty()) {
                binding.tilUpdProfNama.error = "Username tidak boleh kosong"
                error = true
            } else {
                binding.tilUpdProfNama.error = null
            }

            if (tanggalLahir.isEmpty()) {
                binding.tilUpdProfTanggalLahir.error = "Tanggal lahir harus diisi"
                error = true
            } else {
                binding.tilUpdProfTanggalLahir.error = null
            }

            if (nomorTelepon.length == 12) {
                binding.tilUpdProfNomorTelepon.error = null
            } else {
                binding.tilUpdProfNomorTelepon.error = "Nomor telepon harus 12 digit"
                error = true
            }

//            CoroutineScope(Dispatchers.IO).launch {
//                // all input is valid
            if (!error) {
                updateUser()
//                    // do update user
//                    userDao.updateUser(
//                        spSession.getInt("id", 0),
//                        name,
//                        email,
//                        tanggalLahir,
//                        nomorTelepon
//                    )
//
//                    // go back to previous activity
//                    finish()
            } else {
                Snackbar.make(
                    binding.root,
                    "Update profil gagal. Cek ulang bagian yang ditandai.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
//            }
        }
    }

    // on navigation back button pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun getUserDetails() {
        // Dapatkan session untuk mengambil token dan id user
        val session = this.getSharedPreferences("session", Context.MODE_PRIVATE)

        // set loading screen
        setLoadingScreen(true)
        val stringRequest: StringRequest = object: StringRequest(Method.GET, UserApi.GET_URL, {
            val gson = Gson()

            // Dapatkan data user dari response [struktur: response = { "data": { ... } }]
            val jsonRespose = JSONObject(it)
            val user = gson.fromJson(jsonRespose.getJSONObject("data").toString(), com.example.tugasbesarpbp.api_models.User::class.java)
            binding.tilUpdProfNama.editText?.setText(user.nama)
            binding.tilUpdProfUsername.editText?.setText(user.username)
            binding.tilUpdProfEmail.editText?.setText(user.email)
            binding.tilUpdProfTanggalLahir.editText?.setText(user.tanggalLahir)
            binding.tilUpdProfNomorTelepon.editText?.setText(user.nomorTelepon)
            setLoadingScreen(false)
        }, {
            // Kalau ada error
            var respObj: JSONObject? = null
            try {
                respObj = JSONObject(String(it.networkResponse.data))
                AlertDialog.Builder(this)
                    .setTitle("Terjadi Kesalahan!")
                    .setMessage(respObj.toString(4))
                    .setPositiveButton("OK", null)
                    .show()
            } catch (e: Exception) {
                AlertDialog.Builder(this)
                    .setTitle("Error " + it.networkResponse.statusCode)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                // Dapatkan token dari session dan tambahkan ke header
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + session.getString("token", "")
                return headers
            }
        }

        // Tambahkan request ke queue
        this.queue!!.add(stringRequest)
    }

    private fun updateUser() {
        // Dapatkan session untuk mengambil token dan id user
        val session = this.getSharedPreferences("session", Context.MODE_PRIVATE)

        // set loading screen
        setLoadingScreen(true)
        val stringRequest: StringRequest = object: StringRequest(Method.PUT, UserApi.UPDATE_URL, {
            finish()
        }, {
            // Kalau ada error
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
                    val errors = respObj.getJSONObject("errors")
                    for (key in errors.keys()) {
                        val error = errors.getJSONArray(key)
                        when (key) {
                            "username" -> binding.tilUpdProfUsername.error = error[0].toString()
                            "email" -> binding.tilUpdProfEmail.error = error[0].toString()
                            "tanggalLahir" -> binding.tilUpdProfTanggalLahir.error = error[0].toString()
                            "nomorTelepon" -> binding.tilUpdProfNomorTelepon.error = error[0].toString()
                        }
                    }
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
            setLoadingScreen(false)
        }) {
            override fun getHeaders(): MutableMap<String, String> {
                // Dapatkan token dari session dan tambahkan ke header
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer " + session.getString("token", "")
                return headers
            }

            override fun getBody(): ByteArray {
                // Dapatkan data user dari form
                val user = com.example.tugasbesarpbp.api_models.User(
                    nama = binding.tilUpdProfNama.editText?.text.toString(),
                    username = binding.tilUpdProfUsername.editText?.text.toString(),
                    email = binding.tilUpdProfEmail.editText?.text.toString(),
                    tanggalLahir = binding.tilUpdProfTanggalLahir.editText?.text.toString(),
                    nomorTelepon = binding.tilUpdProfNomorTelepon.editText?.text.toString()
                )

                // Ubah data user ke JSON
                val gson = Gson()
                val json = gson.toJson(user)

                // Return JSON
                return json.toByteArray(StandardCharsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        // Tambahkan request ke queue
        this.queue!!.add(stringRequest)
    }

    private fun setLoadingScreen(state: Boolean) {
        if (state) {
            binding.layoutLoader.layoutLoader.visibility = View.VISIBLE
            binding.layoutLoader.layoutLoader.alpha = 1f

            // set flag to disable click
            binding.layoutLoader.layoutLoader.isClickable = true
        } else {
//            binding.layoutLoader.visibility = View.GONE
            // fade out
            binding.layoutLoader.layoutLoader.animate().alpha(0f).setDuration(250).withEndAction {
                binding.layoutLoader.layoutLoader.visibility = View.GONE
            }
            // set flag to enable click
            binding.layoutLoader.layoutLoader.isClickable = false
        }
    }
}