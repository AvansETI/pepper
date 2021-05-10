package com.pepper.care.feedback.presentation.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity

class FeedbackViewModelUsingUsecases : ViewModel(), FeedbackViewModel {

    override val headerText: String = "Hoe gaat het met uw gezondheid?"
    override val badFeedbackEntity: FeedbackEntity =
        FeedbackEntity(FeedbackEntity.FeedbackMessage.BAD)
    override val mediumFeedbackEntity: FeedbackEntity =
        FeedbackEntity(FeedbackEntity.FeedbackMessage.OKAY)
    override val goodFeedbackEntity: FeedbackEntity =
        FeedbackEntity(FeedbackEntity.FeedbackMessage.GOOD)

    override val cardClickedListener: FeedbackCallback =
        object : FeedbackCallback {
            override fun onClicked(type: FeedbackEntity.FeedbackMessage) {
                Log.d(FeedbackViewModelUsingUsecases::class.simpleName, type.toString())
            }
        }
}