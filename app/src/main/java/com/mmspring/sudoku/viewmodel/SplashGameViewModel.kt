package com.mmspring.sudoku.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.apps4mm.quiz4myanmar.data.AppDatabase
import com.mmspring.sudoku.repository.GameRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SplashGameViewModel(private val application: Application):ViewModel() {
    private val database = AppDatabase.getDatabase(application)
    private val repository = GameRepository(database)
    private var _loading = MutableLiveData<Boolean>()
    val loading : LiveData<Boolean>
        get() = _loading
    init {
        _loading.value = true
        checkDBandInsert()
    }
    private fun checkDBandInsert(){
        viewModelScope.launch(){
            repository.checkAndInsert(application)
            Log.i("Loading","loading")
            _loading.value = false

        }
    }
    fun doneNavigation(){
        _loading.value = true
    }
}

class SplashGameViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashGameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SplashGameViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}