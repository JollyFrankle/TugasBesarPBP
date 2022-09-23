package com.example.tugasbesarpbp.room.kost

import androidx.room.*
import com.example.tugasbesarpbp.room.user.User

@Dao
interface KostDao {
    @Insert
    suspend fun addKost(Kost: Kost)

    @Update
    suspend fun updateKost(Kost: Kost)

    @Query("DELETE FROM kost WHERE id = :id")
    suspend fun deleteKost(id: Int)

    @Query("SELECT * FROM Kost;")
    fun getKost(): List<Kost>

    @Query("SELECT * FROM Kost WHERE id = :id")
    fun getKostById(id: Int): Kost
//    @Query("SELECT * FROM User WHERE username = :username AND password = :password;")
//    suspend fun getUserByCred(username: String, password: String): User?
//
//    @Query("SELECT * FROM User WHERE id = :id;")
//    suspend fun getUserById(id: Int): User?
//
//    @Query("SELECT * FROM User WHERE username = :username OR email = :email;")
//    suspend fun getUserByUsernameOrEmail(username: String, email: String): User?
}