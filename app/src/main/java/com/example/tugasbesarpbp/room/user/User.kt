package com.example.tugasbesarpbp.room.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true) val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val tanggalLahir: String,
    val nomorTelepon: String
)