/*
 * Copyright (c) 2023.
 * Richard Uzor
 * Under the authority of Devstrike Digital Limited
 */

package org.devstrike.app.medcareaidscheduler.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import org.devstrike.app.medcareaidscheduler.utils.Common.IS_FIRST_TIME_KEY

class SessionManager(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session_manager")

    suspend fun isFirstTime(isFirstTime: Boolean = true) {
        val isFirstTimeKey = booleanPreferencesKey(IS_FIRST_TIME_KEY)

        context.dataStore.edit { preferences ->
            preferences[isFirstTimeKey] = isFirstTime
        }
    }

    suspend fun getIsFirstTime(): Boolean? {
        val isFirstTimeKey = booleanPreferencesKey(IS_FIRST_TIME_KEY)
        val preferences = context.dataStore.data.first()

        return preferences[isFirstTimeKey]
    }


}