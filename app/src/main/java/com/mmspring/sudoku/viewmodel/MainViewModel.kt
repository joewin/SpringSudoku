package com.mmspring.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.apps4mm.quiz4myanmar.data.AppDatabase
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.repository.GameRepository
import com.mmspring.sudoku.util.DataStoreManager
import com.mmspring.sudoku.util.Utility
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : ViewModel() {
    private val repository = GameRepository(AppDatabase.getDatabase(application))
    private var _gameLiveData = MutableLiveData<Game?>()
    val gameLiveData : LiveData<Game?>
        get() = _gameLiveData

    private var _hintLiveData = MutableLiveData<Int>()
    val hintLiveData :LiveData<Int>
        get() = _hintLiveData
    private var _bombLiveData = MutableLiveData<Int>()
    val bombLiveData :LiveData<Int>
        get() = _hintLiveData
    private var _arrowLiveData = MutableLiveData<Int>()
    val arrowLiveData :LiveData<Int>
        get() = _hintLiveData
    init {
        viewModelScope.launch {
            _hintLiveData.value = DataStoreManager(application).readHints()
            _bombLiveData.value = DataStoreManager(application).readBombs()
            _arrowLiveData.value = DataStoreManager(application).readAArrows()
        }
    }
    fun doneNavigating() {
        _gameLiveData.value = null
    }
    fun getWeeklyGame(){
        viewModelScope.launch {
            _gameLiveData.value = repository.getWeekGame(Utility.getWeek())
        }
    }

}
class MainViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}