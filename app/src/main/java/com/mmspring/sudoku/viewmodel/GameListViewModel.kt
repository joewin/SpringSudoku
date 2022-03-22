package com.mmspring.sudoku.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.apps4mm.quiz4myanmar.data.AppDatabase
import com.mmspring.sudoku.model.Game
import com.mmspring.sudoku.repository.GameRepository

enum class QueryFilter{VERYEASY,EASY,MEDIUM,HARD}
class GameListViewModel(application: Application): ViewModel()  {
    
    private val dbFilter = MutableLiveData(QueryFilter.VERYEASY)
    private val repository = GameRepository(AppDatabase.getDatabase(application))
        val games = Transformations.switchMap(dbFilter) {
        when(it) {
            QueryFilter.VERYEASY -> repository.getGameByLevel(1).asLiveData()
            QueryFilter.EASY -> repository.getGameByLevel(2).asLiveData()
            QueryFilter.MEDIUM -> repository.getGameByLevel(3).asLiveData()
            QueryFilter.HARD -> repository.getGameByLevel(4).asLiveData()
            else ->  repository.getGameByLevel(1).asLiveData()
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
class GameListViewModelFactory(val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameListViewModel(application) as T
        }
        throw IllegalArgumentException("Unable to construct viewmodel")
    }

}