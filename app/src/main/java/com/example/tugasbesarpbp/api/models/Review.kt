package com.example.tugasbesarpbp.api.models

class Review (
    val id_kost: Long,
    val id_user: Long,
    val rating: Float,
    val review: String
){
    val id: Long? = null
    val users: User? = null
    val kosts: Kost? = null
    val created_at: String? = null
    val updated_at: String? = null
}