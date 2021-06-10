package com.pepper.care.common.repo

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.*
import java.io.IOException

class AppPreferencesRepository(val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = APP_PREFERENCES_NAME)

    private val _patientIdState = MutableStateFlow<String>("-2")
    val patientIdState: StateFlow<String> = _patientIdState

    fun updatePatientIdState(id: String) {
        _patientIdState.value = id
    }

    private val _patientNameState = MutableStateFlow<String>("NONE")
    val patientNameState: StateFlow<String> = _patientNameState

    fun updatePatientNameState(name: String) {
        _patientNameState.value = name
    }


    suspend fun updatePatientId(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PATIENT_ID] = value
        }
    }

    val patientIdFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[PATIENT_ID] ?: "-2"
        }

    suspend fun updatePatientName(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PATIENT_NAME] = value
        }
    }

    val patientNameFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[PATIENT_NAME] ?: "NONE"
        }

    suspend fun updateMeals(value: String) {
        context.dataStore.edit { preferences ->
            preferences[MEALS] = value
        }
    }

    val mealsFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[MEALS] ?: "NONE"
        }

    suspend fun updateReminders(value: String) {
        context.dataStore.edit { preferences ->
            preferences[REMINDERS] = value
        }
    }

    val remindersFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[REMINDERS] ?: "NONE"
        }

    suspend fun updateQuestions(value: String) {
        context.dataStore.edit { preferences ->
            preferences[QUESTIONS] = value
        }
    }

    val questionsFlow: Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map {
            it[QUESTIONS] ?: "NONE"
        }

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
            it[FEEDBACK_SLIDER] ?: 8
        }

    companion object {
        private val PATIENT_ID = stringPreferencesKey("patient_id")
        private val PATIENT_NAME = stringPreferencesKey("patient_name")
        private val MEALS = stringPreferencesKey("meals")
        private val REMINDERS = stringPreferencesKey("reminders")
        private val QUESTIONS = stringPreferencesKey("questions")
        private val PUBLISH_MESSAGE = stringPreferencesKey("publish_message")
        private val FEEDBACK_SLIDER = intPreferencesKey("feedback_slider")
        private const val APP_PREFERENCES_NAME = "app_preferences"
    }
}