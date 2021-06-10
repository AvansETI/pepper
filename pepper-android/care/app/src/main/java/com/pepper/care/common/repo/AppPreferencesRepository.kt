package com.pepper.care.common.repo

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.pepper.care.core.services.platform.entities.PlatformMeal
import com.pepper.care.core.services.platform.entities.PlatformQuestion
import com.pepper.care.core.services.platform.entities.PlatformReminder
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


    private val _mealsState = MutableStateFlow<List<PlatformMeal>>(emptyList())
    val mealsState: StateFlow<List<PlatformMeal>> = _mealsState

    fun updateMealsState(meals: List<PlatformMeal>) {
        _mealsState.value = meals
    }


    private val _mealOrderIdState = MutableStateFlow<String>("-2")
    val mealOrderIdState: StateFlow<String> = _mealOrderIdState

    fun updateMealOrderIdState(id: String) {
        _mealOrderIdState.value = id
    }


    private val _questionsState = MutableStateFlow<List<PlatformQuestion>>(emptyList())
    val questionsState: StateFlow<List<PlatformQuestion>> = _questionsState

    fun updateQuestionsState(questions: List<PlatformQuestion>) {
        _questionsState.value = questions
    }


    private val _remindersState = MutableStateFlow<List<PlatformReminder>>(emptyList())
    val remindersState: StateFlow<List<PlatformReminder>> = _remindersState

    fun updateRemindersState(reminders: List<PlatformReminder>) {
        _remindersState.value = reminders
    }


    suspend fun updatePublishMessage(value: String) {
        context.dataStore.edit { preferences ->
            preferences[PUBLISH_MESSAGE] = value
        }
    }


    private val _answerIdState = MutableStateFlow<String>("-2")
    val answerIdState: StateFlow<String> = _answerIdState

    fun updateAnswerIdState(id: String) {
        _answerIdState.value = id
    }


    private val _feedbackIdState = MutableStateFlow<String>("-2")
    val feedbackIdState: StateFlow<String> = _feedbackIdState

    fun updateFeedbackIdState(id: String) {
        _feedbackIdState.value = id
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
        private val PUBLISH_MESSAGE = stringPreferencesKey("publish_message")
        private val FEEDBACK_SLIDER = intPreferencesKey("feedback_slider")
        private const val APP_PREFERENCES_NAME = "app_preferences"
    }
}