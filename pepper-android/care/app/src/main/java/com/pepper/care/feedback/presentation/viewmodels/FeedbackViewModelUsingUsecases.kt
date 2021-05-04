package com.pepper.care.feedback.presentation.viewmodels

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.pepper.care.R
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.entities.RecyclerAdapterItem
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity

class FeedbackViewModelUsingUsecases : ViewModel(), FeedbackViewModel {

    override val headerText: String = "Hoe gaat het met uw gezondheid?"
    override val badFeedbackEntity: FeedbackEntity =
        FeedbackEntity(FeedbackEntity.FeedbackTypes.BAD)
    override val mediumFeedbackEntity: FeedbackEntity =
        FeedbackEntity(FeedbackEntity.FeedbackTypes.OKAY)
    override val goodFeedbackEntity: FeedbackEntity =
        FeedbackEntity(FeedbackEntity.FeedbackTypes.GOOD)

    override val cardClickedListener: FeedbackCallback =
        object : FeedbackCallback {
            override fun onClicked(type: FeedbackEntity.FeedbackTypes) {
                Log.d(FeedbackViewModelUsingUsecases::class.simpleName, type.toString())
            }
        }
}