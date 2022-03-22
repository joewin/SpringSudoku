package com.mmspring.sudoku

import android.app.Application
import android.util.Log
import com.mmspring.sudoku.util.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication :Application(){
    private val uiScope = CoroutineScope(Dispatchers.IO)
    override fun onCreate() {
        super.onCreate()
        val storeManager = DataStoreManager(this)
        Log.i("Calling","calling this")
        uiScope.launch {
           storeManager.updateArrows(5)
           storeManager.updateBombs(5)
           storeManager.updateHints(5)
       }
    }
}