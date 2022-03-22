package com.apps4mm.quiz4myanmar.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mmspring.sudoku.database.GameDao
import com.mmspring.sudoku.database.GameHistoryDao
import com.mmspring.sudoku.database.WeeklyGameDao

import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.model.GameHistory
import com.mmspring.sudoku.model.WeeklyGame


@Database(entities = [Game::class, GameHistory::class, WeeklyGame::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun gameDao(): GameDao
    abstract fun gameHistoryDao(): GameHistoryDao
    abstract fun weeklyGameDao(): WeeklyGameDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(
            context: Context
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "game.db"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }


}