package com.example.tugasbesarpbp

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.tugasbesarpbp.room.MainDB
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.tugasbesarpbp.databinding.ActivityUpdateBinding
import com.example.tugasbesarpbp.main_ui.ProfileFragment
import com.example.tugasbesarpbp.room.user.User
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.withContext
import java.util.*

class UpdateProfileActivity : AppCompatActivity() {
    private var _binding: ActivityUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var spSession: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Shared Preferences
        spSession = getSharedPreferences("session", Context.MODE_PRIVATE)

        // show back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db by lazy { MainDB(this) }
        val userDao = db.UserDao()

        // load data
        CoroutineScope(Dispatchers.IO).launch {
            val user: User? = db.UserDao().getUserById(spSession.getInt("id", 0))
            if(user != null){
                withContext(Dispatchers.Main){
                    binding.tilUpdProfNama.editText?.setText(user.nama)
                    binding.tilUpdProfUsername.editText?.setText(user.username)
                    binding.tilUpdProfEmail.editText?.setText(user.email)
                    binding.tilUpdProfTanggalLahir.editText?.setText(user.tanggalLahir)
                    binding.tilUpdProfNomorTelepon.editText?.setText(user.nomorTelepon)
                }
            }
        }

        binding.btnPickDate.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                // Display Selected date in TextView
                _binding?.tilUpdProfTanggalLahir?.editText?.setText(String.format("%02d", dayOfMonth) + "/" + String.format("%02d", month+1) + "/" + year)
            }, year, month, day)
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

            if(tanggalLahir.isEmpty()){
                binding.tilUpdProfTanggalLahir.error = "Tanggal lahir harus diisi"
                error = true
            } else{
                binding.tilUpdProfTanggalLahir.error = null
            }

            if(nomorTelepon.length == 12){
                binding.tilUpdProfNomorTelepon.error = null
            } else{
                binding.tilUpdProfNomorTelepon.error = "Nomor telepon harus 12 digit"
                error = true
            }

            CoroutineScope(Dispatchers.IO).launch {

                // all input is valid
                if (!error) {
                    // do update user
                    userDao.updateUser(
                        spSession.getInt("id", 0),
                        name,
                        email,
                        tanggalLahir,
                        nomorTelepon
                    )

                    // go back to previous activity
                    // send data to previous activity
//                    val intent = Intent(this@UpdateActivity, HomeActivity::class.java)
//                    intent.putExtra("fragment", "profile")
                    setResult(RESULT_OK)
                    finish()
//                    val intent = Intent(this@UpdateActivity, HomeActivity::class.java)
//                    intent.putExtra("fragment", "profile")
//                    startActivity(intent)
//                    finish()
                } else {
                    Snackbar.make(binding.root, "Update profil gagal. Cek ulang bagian yang ditandai.", Snackbar.LENGTH_LONG).show()
                }



            }
        }
    }

    // on navigation back button pressed
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    // on back pressed
//    override fun onBackPressed() {
//        val intent = Intent(this, HomeActivity::class.java)
//        intent.putExtra("fragment", "profile")
//        startActivity(intent)
//        finish()
//    }
}