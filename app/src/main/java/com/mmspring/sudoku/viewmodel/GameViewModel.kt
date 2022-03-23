package com.mmspring.sudoku.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.apps4mm.quiz4myanmar.data.AppDatabase
import com.mmspring.sudoku.model.Board
import com.mmspring.sudoku.model.Cell
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.model.WeeklyGame
import com.mmspring.sudoku.repository.GameRepository
import com.mmspring.sudoku.util.DataStoreManager
import kotlinx.coroutines.launch

class GameViewModel (private val application: Application,private val game:Game): ViewModel() {

    private val repository = GameRepository(AppDatabase.getDatabase(application))
    //live data for selected cell
    private var _selectedCellLiveData = MutableLiveData<Pair<Int, Int>>()
    val selectedCellLiveData : LiveData<Pair<Int,Int>>
        get() = _selectedCellLiveData
    //livedata for cells
    private var _cellsLiveData = MutableLiveData<List<Cell>>()
    val cellsLiveData: LiveData<List<Cell>>
        get() = _cellsLiveData
    //livedata for taking notes
    private var _isTakingNotesLiveData = MutableLiveData<Boolean>()
    val isTakingNotesLiveData: LiveData<Boolean>
        get() = _isTakingNotesLiveData
    //livedata for highlightedKeys
    private var original: String =""
    private var gameState:String =""
    private var solutionString:String =""
    private var _highlightedKeysLiveData = MutableLiveData<Set<Int>>()
    val highlightedKeysLiveData : LiveData<Set<Int>>
        get() = _highlightedKeysLiveData

    // setting selected Row , col and istakingnotes
    private var selectedRow = -1
    private var selectedCol = -1
    private var isTakingNotes = false


    private lateinit var _board: Board

    val board : Board
        get() = _board

    // this one for game play
    private var _hints = MutableLiveData<Int>()
    val hints : LiveData<Int>
        get() = _hints
    private var _bombs = MutableLiveData<Int>()
    val bombs : LiveData<Int>
        get() = _bombs
    private var _arrows = MutableLiveData<Int>()
    val arrows : LiveData<Int>
        get() = _arrows
    //
    private var gridSol:ArrayList<Int> = arrayListOf()
    private var gridStates:ArrayList<Int> = arrayListOf()
    private val gridCells: ArrayList<Int> = arrayListOf()
    //initializing the board with

    private var _savingDone = MutableLiveData<Boolean>()
    val savingDone : LiveData<Boolean>
        get() = _savingDone
    init {

        original = game.gameQuiz
        gameState = game.currentState
        solutionString = game.solution
        initiateGame()
        initiateSpecials()
        _savingDone.value = false
    }

    private fun initiateSpecials() {
        viewModelScope.launch {
            _hints.value =  DataStoreManager(application).readHints()
            _bombs.value = DataStoreManager(application).readBombs()
            _arrows.value = DataStoreManager(application).readAArrows()
        }
    }

    private fun useHints(){
        viewModelScope.launch {
            _hints.value?.let {
                _hints.value = hints.value?.minus(1)
                DataStoreManager(application).updateHints( _hints.value!!)
               // Log.i("DATASTORE", DataStoreManager(application).readHints().toString())
            }

        }
    }
    private fun usedArrows(){
        viewModelScope.launch {
            _arrows.value?.let {
                _arrows.value = arrows.value?.minus(1)
                DataStoreManager(application).updateArrows( _arrows.value!!)
            }
        }
    }
    private fun usedBombs(){
        viewModelScope.launch {
            _bombs.value?.let {
                _bombs.value = bombs.value?.minus(1)
                DataStoreManager(application).updateBombs( _bombs.value!!)
            }
        }
    }
    fun addRewards(){
        val rnds = (0..2).random()
        viewModelScope.launch {
            when(rnds){
                0 -> {
                    _hints.value = hints.value?.plus(1)
                    DataStoreManager(application).updateHints( _hints.value!!)
                }
                1 -> {
                    _arrows.value = arrows.value?.plus(1)
                    DataStoreManager(application).updateArrows( _arrows.value!!)
                }
                2 -> {
                    _bombs.value = bombs.value?.plus(1)
                    DataStoreManager(application).updateBombs( _bombs.value!!)
                }
            }
        }
    }
    private fun initiateGame(){
        for(ws in solutionString){
            gridSol.add(ws.code-48)
        }
        for(w in original){
            gridCells.add(w.code-48)
        }
        for(gs in gameState){
            gridStates.add(gs.code-48)
        }
        val cells = List(9 * 9) {i -> Cell(i / 9, i % 9, gridCells[i])}
        for (i in 0 until gridCells.size-1){
            //Log.d("Initiate", cells[i].row.toString() + ", " + cells[i].col.toString())
            if(gridCells[i] > 0) {
                cells[i].isStartingCell = true
            }
            if(cells[i].row < 3){
                cells[i].gridRowIndex = 0
            }
            else if(cells[i].row >= 3 && cells[i].row <= 5){
                cells[i].gridRowIndex = 1
            }
            else{
                cells[i].gridRowIndex = 2
            }

            if(cells[i].col < 3){
                cells[i].gridColIndex = 0
            }
            else if(cells[i].col >= 3 && cells[i].col <= 5){
                cells[i].gridColIndex = 1
            }
            else{
                cells[i].gridColIndex = 2
            }
        }
        _selectedCellLiveData.postValue(Pair(selectedRow,selectedCol))
        _board = Board(9, cells)
        _cellsLiveData.postValue(board.cells)
        _isTakingNotesLiveData.postValue(isTakingNotes)
        handleInitGame()
    }
    //update the selected cell and adding value
    fun updateSelectedCell(row: Int, col: Int) {
        val cell = board.getCell(row, col)
        if (!cell.isStartingCell) {
            selectedRow = row
            selectedCol = col
            _selectedCellLiveData.postValue(Pair(row, col))

            if (isTakingNotes) {
                _highlightedKeysLiveData.postValue(cell.notes)
            }
        }
    }
    //handling number input
    fun handleInput(number:Int){
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return


        if (isTakingNotes) {
            if (cell.notes.contains(number)) {
                cell.notes.remove(number)
            } else {
                cell.notes.add(number)
            }
            _highlightedKeysLiveData.postValue(cell.notes)
        } else {
            cell.isInvalid = validate(number,cell)
            cell.value = number
        }
        _cellsLiveData.postValue(board.cells)

    }
    // starting a game
    private fun handleInitGame(){
        var i = 0
        for(cell in board.cells){
            cell.value = gridStates[i]
            i += 1
        }
        _cellsLiveData.postValue(board.cells)
    }
    //handle hint
    fun handleHint(){
        if (selectedRow == -1 || selectedCol == -1) {
            Log.d("Row colum", "-1")
            return
        }
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) {
            Log.d("Starting cell", "true")
            return
        }
        cell.value = getNumber(selectedRow,selectedCol,gridSol)
        _cellsLiveData.postValue(board.cells)
        useHints()
    }
    //handle arrow
    fun handleArrow(row:Boolean){
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return
        if(row){
            val myrow = cell.row
            for(i in 0..8){
                val myCell = board.getCell(myrow, i)
                if(!myCell.isStartingCell) {
                    myCell.value = getNumber(myCell.row, myCell.col, gridSol)
                }
            }
        }else{
            val col = cell.col
            for(i in 0..8){
                val myCell = board.getCell(i, col)
                if(!myCell.isStartingCell) {
                    myCell.value = getNumber(myCell.row, myCell.col, gridSol)
                }
            }
        }
        _cellsLiveData.postValue(board.cells)
        usedArrows()
    }
    //handle bomb
    fun handleBomb() {
        if (selectedRow == -1 || selectedCol == -1) return
        val cell = board.getCell(selectedRow, selectedCol)
        if (cell.isStartingCell) return
        for(myCell in board.cells){
            if(myCell.gridRowIndex == cell.gridRowIndex && myCell.gridColIndex == cell.gridColIndex){
                if(!myCell.isStartingCell){
                    myCell.value = getNumber(myCell.row, myCell.col, gridSol)
                }
            }
        }

        _cellsLiveData.postValue(board.cells)
        usedBombs()
    }
    // get solotion number of cells
    private fun getNumber(selectedRow: Int, selectedCol: Int, solList: ArrayList<Int>): Int {
        var index = selectedRow * 9
        index += selectedCol
        return solList[index]
    }
    //note taking
    fun changeNoteTakingState() {
        isTakingNotes = !isTakingNotes
        _isTakingNotesLiveData.postValue(isTakingNotes)

        val curNotes = if (isTakingNotes) {
            board.getCell(selectedRow, selectedCol).notes
        } else {
            setOf()
        }
        _highlightedKeysLiveData.postValue(curNotes)
    }
    //deleting cell
    fun delete() {
        val cell = board.getCell(selectedRow, selectedCol)
        //if (cell.isStartingCell) return
        if (isTakingNotes) {
            cell.notes.clear()
            _highlightedKeysLiveData.postValue(setOf())
            cell.value = 0
        } else {
            cell.value = 0
        }
        _cellsLiveData.postValue(board.cells)
    }
    //checking that number is at correct cell
    private fun validate(number:Int,myCell: Cell): Boolean{
        for(cell in board.cells){
            if(cell.value == number){
                if(selectedRow == cell.row || selectedCol == cell.col){
                    return false
                }
                if(myCell.gridRowIndex == cell.gridRowIndex && myCell.gridColIndex == cell.gridColIndex){
                    return false
                }
            }
        }
        return true
    }
    fun saveGame(timeInSeconds:Long){
        val currentGame = getCurrentGameGrids()
        viewModelScope.launch {
            val g = Game(game.id, game.level, timeInSeconds, original, currentGame, solutionString)
            repository.saveGame(g)
            _savingDone.value = true
        }
    }
    fun saveWeeklyGame(timeInSeconds: Long){
        val currentGame = getCurrentGameGrids()
        viewModelScope.launch {
           val g = WeeklyGame(game.id, original, solutionString, currentGame,timeInSeconds)
            repository.saveWeeklyGame(g)
            _savingDone.value = true
        }
    }
    fun insertHistory(timeInSeconds: Long){
        viewModelScope.launch {
            val g = Game(game.id, game.level, timeInSeconds, original, solutionString, solutionString)
            repository.saveGame(g)
            repository.insertHistory(g)
        }
    }
    fun setSavingValue(){
        _savingDone.value = false
    }
    private fun getCurrentGameGrids(): String {
        val currentList: ArrayList<Int> = ArrayList()
        cellsLiveData.value?.forEach {
            currentList.add(it.value)
        }
        return currentList.joinToString(separator = "")
    }
}
class GameViewModelFactory(private val application: Application,private val game:Game) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(application,game) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}