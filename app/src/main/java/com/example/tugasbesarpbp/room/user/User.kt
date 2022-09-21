package com.example.tugasbesarpbp.room.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey(autoGenerate = true)
    val tilRegisUsername: String,
    val tilRegisEmail: String,
    val tilRegisPassword: String,
    val tilRegisTanggalLahir: String,
    val tilRegisNomorTelepon: String
)
