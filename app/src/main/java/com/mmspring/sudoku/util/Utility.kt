package com.mmspring.sudoku.util


import java.util.*
import java.util.concurrent.TimeUnit

object Utility {

    fun getFormattedStopWatch(ms: Long): String {
        var milliseconds = ms
        val hours = TimeUnit.MILLISECONDS.toHours(milliseconds)
        milliseconds -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds)
        milliseconds -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds)
        return "${if (hours < 10) "0" else ""}$hours:" +
                "${if (minutes < 10) "0" else ""}$minutes:" +
                "${if (seconds < 10) "0" else ""}$seconds"
    }
    fun alreadyWOn(game:String, solution:String):Boolean{
        return game == solution
    }
    fun getLevel(level: Int): String{
        return when(level){
            1-> "Ver Easy"
            2-> "Easy"
            3-> "Medium"
            4-> "Hard"
            else -> "Easy"
        }
    }
    fun getGameId(id:Int,level: Int): String{
        return when(level){
            1-> "[veasy]$id"
            2-> "[easy]$id"
            3-> "[medium]$id"
            4-> "[hard]$id"
            else -> "[easy]$id"
        }
    }
    fun getWeek(): Int{
        var weeks =  Calendar.getInstance().get(Calendar.WEEK_OF_YEAR)
        return weeks
    }
    fun getEmptyCells(level: Int): Int{
        return when(level){
            in 1..20  -> 10
            in 21..51 -> 20
            in 52..100 -> 30
            in 101..200 -> 35
            in 201..300 -> 40
            in 301..400 -> 45
            in 401..500 -> 48
            in 501..600 -> 50
            in 601..1000 -> 55
            in 1001..1500 -> 65
            else -> 65
        }
    }


}