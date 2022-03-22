package com.mmspring.sudoku.database

import androidx.room.*
import com.mmspring.sudoku.model.Game
import kotlinx.coroutines.flow.Flow


@Dao
interface GameDao {
    @Insert
    suspend fun insertGame(game: Game)

    @Query("select * from game")
    fun getAll(): Flow<List<Game>>

    @Query("SELECT COUNT(*) FROM game")
    suspend fun getCount():Int

    @Query("select * from game where level= :level")
    fun getGamesByLevel(level:Int): Flow<List<Game>>

    @Query("select * from game where id= :id")
    fun getGamesById(id:Int): Flow<Game>

    @Update
    suspend fun update(game:Game)

    @Delete
    suspend fun delete(game:Game)

    @Query("DELETE FROM game")
    suspend fun deleteAll()
}