package com.example.tugasbesarpbp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.tugasbesarpbp.databinding.FragmentRegisterBinding
import com.example.tugasbesarpbp.room.MainDB
import com.example.tugasbesarpbp.room.kost.Kost
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateActivity : AppCompatActivity() {

    private lateinit var btnTambah: Button
    private lateinit var tilTambahNama: TextInputLayout
    private lateinit var tilTambahAlamat: TextInputLayout
    private lateinit var tilTambahFasilitas: TextInputLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        btnTambah = findViewById(R.id.btnTambah)
        tilTambahNama = findViewById(R.id.tilTambahNama)
        tilTambahAlamat = findViewById(R.id.tilTambahAlamat)
        tilTambahFasilitas = findViewById(R.id.tilTambahFasilitas)
        val db by lazy { MainDB(this) }
        val kostDao = db.KostDao()

        btnTambah.setOnClickListener(){
            CoroutineScope(Dispatchers.IO).launch {
                Log.d("MASUK DB", "DAO")
                val kost: Kost = Kost(0, tilTambahNama.editText?.text.toString(), tilTambahAlamat.editText?.text.toString(), tilTambahFasilitas.editText?.text.toString())
                kostDao.addKost(kost)
            }
        }
    }


}