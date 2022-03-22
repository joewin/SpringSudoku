package com.mmspring.sudoku.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "game")
class Game (
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    @ColumnInfo(name="level") val level:Int,
    @ColumnInfo(name="totalTimes") val totalTimes:Long,
    @ColumnInfo(name="gameQuiz") val gameQuiz:String,
    @ColumnInfo(name="currentState") val currentState:String,
    @ColumnInfo(name="solution") val solution:String,
    ):Parcelable{
    var rank:Int = 1
    var finished:Boolean = false
}