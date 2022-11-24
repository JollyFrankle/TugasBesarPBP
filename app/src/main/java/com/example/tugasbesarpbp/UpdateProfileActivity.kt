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
import com.example.tugasbesarpbp.api.http.UserApi
import com.example.tugasbesarpbp.api.models.User
import com.example.tugasbesarpbp.databinding.ActivityUpdateBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
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
        setLoadingScreen(true)
        UserApi.getProfile(this, {
            val gson = Gson()

            // Dapatkan data user dari response [struktur: response = { "data": { ... } }]
            val jsonRespose = JSONObject(it)
            val user = gson.fromJson(jsonRespose.getJSONObject("data").toString(), User::class.java)
            binding.tilUpdProfNama.editText?.setText(user.nama)
            binding.tilUpdProfUsername.editText?.setText(user.username)
            binding.tilUpdProfEmail.editText?.setText(user.email)
            binding.tilUpdProfTanggalLahir.editText?.setText(user.tanggalLahir)
            binding.tilUpdProfNomorTelepon.editText?.setText(user.nomorTelepon)
            setLoadingScreen(false)
        }, {
            setLoadingScreen(false)
        })

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
                val user = User(
                    nama = binding.tilUpdProfNama.editText?.text.toString(),
                    username = binding.tilUpdProfUsername.editText?.text.toString(),
                    email = binding.tilUpdProfEmail.editText?.text.toString(),
                    tanggalLahir = binding.tilUpdProfTanggalLahir.editText?.text.toString(),
                    nomorTelepon = binding.tilUpdProfNomorTelepon.editText?.text.toString()
                )

                setLoadingScreen(true)
                UserApi.updateProfile(this, user, {
                    finish()
                }, {
                    if(it.statusCode.toString().startsWith("4")) {
                        AlertDialog.Builder(this)
                            .setTitle("Terjadi Kesalahan!")
                            .setMessage(it.jsonData.getString("message"))
                            .setPositiveButton("OK", null)
                            .show()
                        // for each errors
                        val errors = it.jsonData.getJSONObject("errors")
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
                            .setMessage("Kode error: " + it.statusCode + "\r\nHubungi admin.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                    setLoadingScreen(false)
                })
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