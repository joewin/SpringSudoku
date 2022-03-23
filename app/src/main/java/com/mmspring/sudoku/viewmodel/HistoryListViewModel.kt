package com.mmspring.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.apps4mm.quiz4myanmar.data.AppDatabase
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.repository.GameRepository


class HistoryListViewModel(application: Application): ViewModel()  {
    
    private val dbFilter = MutableLiveData(QueryFilter.VERYEASY)
    private val repository = GameRepository(AppDatabase.getDatabase(application))
        val games = Transformations.switchMap(dbFilter) {
        when(it) {
            QueryFilter.VERYEASY -> repository.getGameHistoryByLevel(1).asLiveData()
            QueryFilter.EASY -> repository.getGameHistoryByLevel(2).asLiveData()
            QueryFilter.MEDIUM -> repository.getGameHistoryByLevel(3).asLiveData()
            QueryFilter.HARD -> repository.getGameHistoryByLevel(4).asLiveData()
            QueryFilter.WEEKLY -> repository.getGameHistoryByLevel(0).asLiveData()
            else ->  repository.getGameHistoryByLevel(1).asLiveData()
        }
    }
    // for navigation
    private val _navigateToGamePlay = MutableLiveData<Game?>()
    val navigateToGamePlay: LiveData<Game?>
        get() = _navigateToGamePlay

    fun onFilterChange(menuFilter: QueryFilter) {
        dbFilter.value = menuFilter
    }

    //navigation
    fun doneNavigating() {
        _navigateToGamePlay.value = null
    }
    fun onItemClick(item: Game){
        _navigateToGamePlay.value = item
    }
}
class HistoryListViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryListViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}