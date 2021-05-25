package com.pepper.care.feedback.presentation.viewmodels

import android.app.Activity
import android.graphics.Typeface
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.awesomedialog.*
import com.pepper.care.R
import com.pepper.care.common.DialogCallback
import com.pepper.care.common.DialogUtil
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.views.FabCallback
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MAX_RANGE
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MIN_RANGE
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MOCK_EXPLANATION
import com.pepper.care.feedback.common.usecases.AddPatientGivenHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.common.usecases.AddPatientHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.entities.FeedbackEntity
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.*
import kotlin.concurrent.schedule

class FeedbackViewModelUsingUsecases(
    private val feedbackType: AddPatientHealthFeedbackUseCaseUsingRepository,
    private val feedbackExplain: AddPatientGivenHealthFeedbackUseCaseUsingRepository
) : ViewModel(), FeedbackViewModel {

    override val headerText: String = "Hoe voelt u zich momenteel?"
    override val sliderRange: Pair<Int, Int> = Pair(FEEDBACK_MIN_RANGE, FEEDBACK_MAX_RANGE)
    override val givenFeedbackType: MutableLiveData<FeedbackEntity.FeedbackMessage> =
        MutableLiveData(FeedbackEntity.FeedbackMessage.GOOD)


    override fun onStart() {
        setupNextButton()
    }

    override val imageListener: FeedbackCallback =
        object : FeedbackCallback {
            override fun onClicked(view: View, type: FeedbackEntity.FeedbackMessage) {
                givenFeedbackType.postValue(type)
            }
        }

    private val dialogCallback: DialogCallback =
        object : DialogCallback {
            override fun onDialogConfirm(view: View) {
                viewModelScope.launch {
                    feedbackType.invoke(givenFeedbackType.value!!)
                    feedbackExplain.invoke(FEEDBACK_MOCK_EXPLANATION)
                }
                view.findNavController().navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.GOODBYE)
                    )
                )
            }
        }

    override val isNextButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    override val fabType: MutableLiveData<FabType> = MutableLiveData(FabType.NEXT)

    override val fabCallback: FabCallback =
        object : FabCallback {
            override fun onClick(view: View) {
                when (fabType.value) {
                    FabType.NEXT -> {
                        DialogUtil.buildDialog(
                            view, "${givenFeedbackType.value!!.text}, $FEEDBACK_MOCK_EXPLANATION.",
                            DialogRoutes.FEEDBACK, dialogCallback
                        )
                    }
                    else -> throw IllegalStateException("Not a valid option")
                }
            }
        }

    private fun setupNextButton() {
        fabType.apply {
            value = FabType.NEXT
        }
        Timer().schedule(1000) {
            isNextButtonVisible.postValue(true)
        }
    }
}