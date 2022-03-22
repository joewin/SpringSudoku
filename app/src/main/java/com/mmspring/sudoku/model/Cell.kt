package com.mmspring.sudoku.model

class Cell (val row: Int,
val col: Int,
var value: Int,
var isStartingCell: Boolean = false,
var notes: MutableSet<Int> = mutableSetOf(),
var isInvalid:Boolean = true,
var gridRowIndex: Int = -1,
var gridColIndex: Int = -1)