package com.pepper.care.feedback.presentation.viewmodels

import android.content.SharedPreferences
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.pepper.care.common.AppResult
import com.pepper.care.common.CommonConstants
import com.pepper.care.common.usecases.GetPatientNameUseCaseUsingRepository
import com.pepper.care.core.services.robot.DynamicConcepts
import com.pepper.care.core.services.robot.RobotManager
import com.pepper.care.dialog.DialogConstants
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MAX_RANGE
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MIN_RANGE
import com.pepper.care.feedback.common.usecases.AddPatientGivenHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.common.usecases.AddPatientHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.entities.FeedbackEntity
import com.ramotion.fluidslider.FluidSlider
import kotlinx.coroutines.launch

class FeedbackViewModelUsingUsecases(
    private val getName: GetPatientNameUseCaseUsingRepository,
    private val feedbackType: AddPatientHealthFeedbackUseCaseUsingRepository,
    private val feedbackExplain: AddPatientGivenHealthFeedbackUseCaseUsingRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel(), FeedbackViewModel {

    override val headerText: String = "Hoe voelt u zich momenteel?"
    override val sliderRange: Pair<Int, Int> = Pair(FEEDBACK_MIN_RANGE, FEEDBACK_MAX_RANGE)
    override val inputText: MutableLiveData<String> = MutableLiveData("")
    override val fluidSlider: MutableLiveData<FluidSlider> = MutableLiveData()
    private val fetchedName: MutableLiveData<String> = MutableLiveData(DialogConstants.DIALOG_MOCK_NAME)

    override val givenFeedbackType: MutableLiveData<FeedbackEntity.FeedbackMessage> =
        MutableLiveData(FeedbackEntity.FeedbackMessage.GOOD)

    override fun onStart() {
        fetchPatientDetails()
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener)
    }

    override val imageListener: FeedbackCallback =
        object : FeedbackCallback {
            override fun onClicked(view: View, type: FeedbackEntity.FeedbackMessage) {
                givenFeedbackType.postValue(type)
            }
        }

    private val sharedPreferenceChangeListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            when (key) {
                CommonConstants.COMMON_SHARED_PREF_UPDATE_FEEDBACK_SLIDER -> {
                    val newValue: Float = (sharedPreferences.getInt(key,7)/10.0).toFloat()
                    Log.d(FeedbackViewModelUsingUsecases::class.simpleName, "New slider value: $newValue")
                    fluidSlider.value!!.position = newValue
                }
            }
        }

    private fun fetchPatientDetails() {
        viewModelScope.launch {
            when (val result = getName.invoke()) {
                is AppResult.Success -> {
                    fetchedName.apply { value = result.successData }
                    RobotManager.addDynamicContents(
                        DynamicConcepts.NAME,
                        listOf(Phrase(result.successData))
                    )
                }
                is AppResult.Error -> result.exception.message
            }
        }
    }
}