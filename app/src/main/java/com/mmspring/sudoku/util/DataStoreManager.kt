package com.mmspring.sudoku.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mmspring.sudoku.util.Constant.USER_PREFERENCE_NAME
import kotlinx.coroutines.flow.first


class DataStoreManager(val context:Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = USER_PREFERENCE_NAME)
        val HINTS = intPreferencesKey(Constant.HINTS_KEY)
        val BOMBS = intPreferencesKey(Constant.BOMBS_KEY)
        val ARROWS = intPreferencesKey(Constant.ARROWS_KEY)
    }
    suspend fun readHints(): Int {
       return context.dataStore.data.first()[HINTS]!!
    }
    suspend fun readBombs(): Int{
        return context.dataStore.data.first()[BOMBS]!!
    }
    suspend fun readAArrows(): Int{
        return context.dataStore.data.first()[ARROWS]!!
    }

    suspend fun updateHints(update: Int){
            context.dataStore.edit {
                it[HINTS] = update
            }
        }
    suspend fun updateBombs(update:Int){
            context.dataStore.edit {
                it[BOMBS] = update
            }
    }
    suspend fun updateArrows(update:Int){
        context.dataStore.edit {
            it[ARROWS] = update
        }
    }
}