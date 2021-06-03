package com.pepper.care.common.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppPreferencesRepository(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCES_NAME)

    suspend fun updatePublishMessage(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PUBLISH_MESSAGE] = value
        }
    }

    val publishMessageFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[PUBLISH_MESSAGE] ?: "NONE"
        }

    suspend fun updateFeedbackSlider(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[FEEDBACK_SLIDER] = value
        }
    }

    val feedbackSliderFlow: Flow<Int> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[FEEDBACK_SLIDER] ?: 0
        }

    companion object {
        private val PUBLISH_MESSAGE = stringPreferencesKey("publish_message")
        private val FEEDBACK_SLIDER = intPreferencesKey("feedback_slider")
        private const val APP_PREFERENCES_NAME = "app_preferences"
    }

}