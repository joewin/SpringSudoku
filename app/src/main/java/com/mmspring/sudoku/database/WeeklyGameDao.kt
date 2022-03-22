package com.mmspring.sudoku.database

import androidx.room.*
import com.mmspring.sudoku.model.WeeklyGame
import kotlinx.coroutines.flow.Flow


@Dao
interface WeeklyGameDao {
    @Insert
    suspend fun insertGame(game: WeeklyGame)

    @Query("select * from weekly_game")
    fun getAll(): Flow<List<WeeklyGame>>

    @Query("SELECT COUNT(*) FROM weekly_game")
    suspend fun getCount():Int

    @Query("select * from weekly_game where id= :id")
    suspend fun getGamesById(id:Int): WeeklyGame

    @Update
    suspend fun update(game:WeeklyGame)

    @Delete
    suspend fun delete(game:WeeklyGame)

    @Query("DELETE FROM weekly_game")
    suspend fun deleteAll()


}