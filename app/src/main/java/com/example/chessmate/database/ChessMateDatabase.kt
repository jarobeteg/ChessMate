package com.example.chessmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chessmate.database.dao.UserProfileDAO
import com.example.chessmate.database.entity.UserProfile

@Database(entities = [UserProfile::class], version = 1, exportSchema = false)
abstract class ChessMateDatabase: RoomDatabase() {
    abstract fun userProfileDAO(): UserProfileDAO

    companion object{
        @Volatile
        private var INSTANCE: ChessMateDatabase? = null

        fun getDatabase(context: Context): ChessMateDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChessMateDatabase::class.java,
                    "chessmate_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}