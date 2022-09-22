package com.example.tugasbesarpbp.room.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val nama: String,
    val username: String,
    val password: String,
    val email: String,
    val tanggalLahir: String,
    val nomorTelepon: String
)