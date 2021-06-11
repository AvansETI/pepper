package com.pepper.care.feedback.presentation.viewmodels

import android.os.Build
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.common.usecases.GetPatientUseCaseUsingRepository
import com.pepper.care.core.services.robot.DynamicConcepts
import com.pepper.care.core.services.robot.RobotManager
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MAX_RANGE
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MIN_RANGE
import com.pepper.care.feedback.entities.FeedbackEntity
import com.ramotion.fluidslider.FluidSlider
import kotlinx.coroutines.launch

class FeedbackViewModelUsingUsecases(
    private val get: GetPatientUseCaseUsingRepository,
    private val appPreferences: AppPreferencesRepository
) : ViewModel(), FeedbackViewModel {

    override val sliderRange: Pair<Int, Int> = Pair(FEEDBACK_MIN_RANGE, FEEDBACK_MAX_RANGE)
    override val headerText: MutableLiveData<String> =
        MutableLiveData("Hoe voel je je op een schaal van 1 tot 10?")
    override val fluidSlider: MutableLiveData<FluidSlider> = MutableLiveData()
    private val fetchedName: MutableLiveData<String> = MutableLiveData("")

    override val givenFeedbackType: MutableLiveData<FeedbackEntity.FeedbackMessage> =
        MutableLiveData(FeedbackEntity.FeedbackMessage.GOOD)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        fetchPatientDetails()
        appPreferences.feedbackSliderFlow.asLiveData().observeForever {
            val newValue: Float = (it / 10.0).toFloat()
                Log.d(
                    FeedbackViewModelUsingUsecases::class.simpleName,
                    "New slider value: $newValue"
                )
                fluidSlider.value!!.position = newValue
                headerText.postValue("Waarom heb je voor dit cijfer gekozen?")
        }
    }

    override val imageListener: FeedbackCallback =
        object : FeedbackCallback {
            override fun onUpdate(view: View, type: FeedbackEntity.FeedbackMessage) {
                givenFeedbackType.postValue(type)
            }
        }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fetchPatientDetails() {
        viewModelScope.launch {
        val name = get.invoke().value
            fetchedName.apply { value = name }
            RobotManager.addDynamicContents(
                DynamicConcepts.NAME,
                listOf(Phrase(name))
            )
        }
    }
}