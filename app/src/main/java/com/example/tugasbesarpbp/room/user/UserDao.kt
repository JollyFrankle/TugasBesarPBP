package com.example.tugasbesarpbp.room.user

import androidx.room.*

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(User: User)

    @Update
    suspend fun updateUser(User: User)

    @Delete
    suspend fun deleteUser(User: User)

    @Query("SELECT * FROM User")
    suspend fun getUsers(): List<User>

    @Query("SELECT * FROM User WHERE username = :username AND password = :password;")
    suspend fun getUser(username: String, password: String): List<User>
}