package com.example.tugasbesarpbp.room.kost

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Kost (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val namaKost: String,
    val alamat: String,
    val fasilitas: String
)