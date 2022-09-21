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

    @Query("SELECT * FROM User WHERE tilRegisUsername =:User_username")
    suspend fun getUser(User_username: String): List<User>
}