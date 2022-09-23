package com.example.tugasbesarpbp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.example.tugasbesarpbp.room.kost.KostDao
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateActivity : AppCompatActivity() {

    private lateinit var btnTambah: Button
    private lateinit var btnEdit: Button
    private lateinit var btnDelete: Button
    private lateinit var tilTambahNama: TextInputLayout
    private lateinit var tilTambahFasilitas: TextInputLayout
    private lateinit var tilTambahHarga: TextInputLayout

    companion object {
        const val CREATE = 1
        const val EDIT = 2
        const val READ = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        setTitle("Data Kost")

        // get action from intent
        var action = intent.getIntExtra("action", 3)
        val id = intent.getIntExtra("id", 1)

        btnTambah = findViewById(R.id.btnTambah)
        btnEdit = findViewById(R.id.btnEdit)
        btnDelete = findViewById(R.id.btnDelete)
        tilTambahNama = findViewById(R.id.tilTambahNama)
        tilTambahFasilitas = findViewById(R.id.tilTambahFasilitas)
        tilTambahHarga = findViewById(R.id.tilTambahHarga)

        val db by lazy { MainDB(this) }
        val kostDao = db.KostDao()

        this.setInputElements(action, id, kostDao);

        btnEdit.setOnClickListener {
            if(action == EDIT) {
                action = READ
            } else {
                action = EDIT
            }
            setInputElements(action, id, kostDao)
        }

        btnDelete.setOnClickListener {
            // confirm alert
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Konfirmasi")
            dialog.setMessage("Apakah anda yakin ingin menghapus data ini?")
            dialog.setPositiveButton("Ya") { dialog, which ->
                CoroutineScope(Dispatchers.IO).launch {
                    kostDao.deleteKost(id)
                    withContext(Dispatchers.Main) {
                        val intent = Intent(this@CreateActivity, HomeActivity::class.java)
                        intent.putExtra("fragment", "list")
                        startActivity(intent)
                        finish()
                    }
                }
            }
            dialog.setNegativeButton("Tidak") { dialog, which ->
                dialog.dismiss()
            }
            dialog.show()
        }

        btnTambah.setOnClickListener {
            val nama = tilTambahNama.editText?.text.toString()
            val fasilitas = tilTambahFasilitas.editText?.text.toString()
            var harga = 0.0
            try {
                harga = tilTambahHarga.editText?.text.toString().toDouble()
            } catch (e: Exception) {
                Log.d("ERROR", e.message.toString())
            }

            var error = 0

            if(nama.isEmpty()) {
                tilTambahNama.error = "Nama tidak boleh kosong!"
                error++
            } else {
                tilTambahNama.error = null
            }

            if(fasilitas.isEmpty()) {
                tilTambahFasilitas.error = "Fasilitas tidak boleh kosong!"
                error++
            } else {
                tilTambahFasilitas.error = null
            }

            if(harga <= 0.0) {
                tilTambahHarga.error = "Harga tidak boleh kosong!"
                error++
            } else {
                tilTambahHarga.error = null
            }

            CoroutineScope(Dispatchers.IO).launch {
                if(error == 0) {
                    if (action == CREATE) {
                        kostDao.addKost(Kost(0, nama, fasilitas, harga))
                    } else if (action == EDIT) {
                        kostDao.updateKost(Kost(id, nama, fasilitas, harga))
                    }

                    val intent = Intent(this@CreateActivity, HomeActivity::class.java)
                    intent.putExtra("fragment", "list")
                    startActivity(intent)
                    finish()
                } else {
                    // do nothing
                }
//                Log.d("MASUK DB", "DAO")
//                val kost: Kost = Kost(0, tilTambahNama.editText?.text.toString(), tilTambahAlamat.editText?.text.toString(), tilTambahFasilitas.editText?.text.toString())
//                kostDao.addKost(kost)
            }
        }
    }

    private fun setInputElements(action: Int, id: Int, kostDao: KostDao) {
        if(action == EDIT || action == READ) {
            CoroutineScope(Dispatchers.IO).launch {
                val kost = kostDao.getKostById(id)

                withContext(Dispatchers.Main) {
                    tilTambahNama.editText?.setText(kost.namaKost)
                    tilTambahFasilitas.editText?.setText(kost.fasilitas)
                    tilTambahHarga.editText?.setText(kost.harga.toString())

                    // set btnTambah text
                    btnTambah.text = "Update"
                }
            }
        }

        if(action == CREATE) {
            btnEdit.isEnabled = false
            btnDelete.isEnabled = false
        } else {
            btnEdit.isEnabled = true
            btnDelete.isEnabled = true
        }

        if(action == READ) {
            tilTambahNama.isEnabled = false
            tilTambahFasilitas.isEnabled = false
            tilTambahHarga.isEnabled = false
            btnTambah.isEnabled = false

            // hide btnTambah
            btnTambah.alpha = 0f
            btnTambah.isClickable = false
        } else {
            tilTambahNama.isEnabled = true
            tilTambahFasilitas.isEnabled = true
            tilTambahHarga.isEnabled = true
            btnTambah.isEnabled = true

            // show btnTambah
            btnTambah.alpha = 1f
            btnTambah.isClickable = true

            if(action == CREATE) {
                btnTambah.text = "Create"
            }
        }
    }


}