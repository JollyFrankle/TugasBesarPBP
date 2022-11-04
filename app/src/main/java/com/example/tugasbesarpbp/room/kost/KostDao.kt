package com.example.tugasbesarpbp.room.kost

import androidx.room.*

@Dao
interface KostDao {
    @Insert
    suspend fun addKost(Kost: Kost): Long // agar bisa mendapatkan id dari data yang baru saja di insert

    @Update
    suspend fun updateKost(Kost: Kost)

    @Query("DELETE FROM kost WHERE id = :id")
    suspend fun deleteKost(id: Int)

    // https://stackoverflow.com/questions/44184769/android-room-select-query-with-like
    @Query("SELECT * FROM Kost WHERE namaKost LIKE '%' || :query || '%' OR fasilitas LIKE '%' || :query || '%' OR harga LIKE '%' || :query || '%';")
    fun getKost(query: String): List<Kost>

    @Query("SELECT * FROM Kost WHERE id = :id")
    fun getKostById(id: Int): Kost
}