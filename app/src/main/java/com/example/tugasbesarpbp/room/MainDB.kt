package com.example.tugasbesarpbp.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        com.example.tugasbesarpbp.room.user.User::class
               ],
    version = 1
)
abstract class MainDB: RoomDatabase() {
    abstract fun UserDao() : com.example.tugasbesarpbp.room.user.UserDao
    companion object {
        @Volatile private var instance: MainDB? = null
        private val LOCK = Any()
        operator fun invoke(context: Context) = instance ?:
        synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                MainDB::class.java,
                "main.db"
            ).build()
    }
}