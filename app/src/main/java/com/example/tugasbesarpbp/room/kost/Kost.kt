package com.example.tugasbesarpbp.room.kost

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Kost (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val namaKost: String,
    val fasilitas: String,
    val harga: Double
)