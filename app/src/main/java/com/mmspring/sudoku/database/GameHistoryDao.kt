package com.mmspring.sudoku.database

import androidx.room.*
import com.mmspring.sudoku.model.GameHistory
import kotlinx.coroutines.flow.Flow


@Dao
interface GameHistoryDao {
    @Insert
    suspend fun insertGame(game: GameHistory)

    @Delete
    suspend fun deleteGameHIstory(gameHistory: GameHistory)

    @Query("SELECT * FROM simpleGameHistory" +
            " WHERE level = :level ORDER BY best_time ASC")
    fun findByLevel(level:Int): Flow<List<GameHistory>>

    @Query("DELETE FROM simpleGameHistory")
    suspend fun deleteAll()

}