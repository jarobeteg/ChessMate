package com.example.chessmate.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.chessmate.database.dao.LessonCompletionDAO
import com.example.chessmate.database.dao.PuzzleCompletionDAO
import com.example.chessmate.database.dao.UserProfileDAO
import com.example.chessmate.database.entity.LessonCompletion
import com.example.chessmate.database.entity.PuzzleCompletion
import com.example.chessmate.database.entity.UserProfile

@Database(entities = [UserProfile::class, PuzzleCompletion::class, LessonCompletion::class], version = 6, exportSchema = false)
abstract class ChessMateDatabase: RoomDatabase() {
    abstract fun userProfileDAO(): UserProfileDAO
    abstract fun puzzleCompletionDAO(): PuzzleCompletionDAO
    abstract fun lessonCompletionDAO(): LessonCompletionDAO

    companion object{
        @Volatile
        private var INSTANCE: ChessMateDatabase? = null

        fun getDatabase(context: Context): ChessMateDatabase {
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ChessMateDatabase::class.java,
                    "chessmate_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}