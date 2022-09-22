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

    @Query("SELECT * FROM User;")
    suspend fun getUsers(): List<User>

    @Query("SELECT * FROM User WHERE username = :username AND password = :password;")
    suspend fun getUserByCred(username: String, password: String): User?

    @Query("SELECT * FROM User WHERE id = :id;")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM User WHERE username = :username OR email = :email;")
    suspend fun getUserByUsernameOrEmail(username: String, email: String): User?
}