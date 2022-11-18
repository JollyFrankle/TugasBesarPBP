package com.example.tugasbesarpbp.api_models

class User(
    var nama: String,
    var username: String,
    var password: String? = null,
    var email: String,
    var tanggalLahir: String,
    var nomorTelepon: String
) {
    var id: Long? = null
}