package com.mmspring.sudoku.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "simpleGameHistory")
class GameHistory(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name = "level") val level: Int,
    @ColumnInfo(name= "game_id") val game_id:Int,
    @ColumnInfo(name = "best_time") val best_time: Long,
    @ColumnInfo(name="played_date") val played_date: String

    ) {
    var rank:Int = 1
}