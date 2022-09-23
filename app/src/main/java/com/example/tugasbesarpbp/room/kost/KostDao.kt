package com.example.tugasbesarpbp.room.kost

import androidx.room.*
import com.example.tugasbesarpbp.room.user.User

@Dao
interface KostDao {
    @Insert
    suspend fun addKost(Kost: Kost)

    @Update
    suspend fun updateKost(Kost: Kost)

    @Delete
    suspend fun deleteKost(Kost: Kost)

    @Query("SELECT * FROM Kost;")
    fun getKost(): ArrayList<Kost>

//    @Query("SELECT * FROM User WHERE username = :username AND password = :password;")
//    suspend fun getUserByCred(username: String, password: String): User?
//
//    @Query("SELECT * FROM User WHERE id = :id;")
//    suspend fun getUserById(id: Int): User?
//
//    @Query("SELECT * FROM User WHERE username = :username OR email = :email;")
//    suspend fun getUserByUsernameOrEmail(username: String, email: String): User?
}