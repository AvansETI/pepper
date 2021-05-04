package com.pepper.care.feedback.presentation.viewmodels

import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity

interface FeedbackViewModel {
    val headerText: String
    val badFeedbackEntity: FeedbackEntity
    val mediumFeedbackEntity: FeedbackEntity
    val goodFeedbackEntity: FeedbackEntity

    val cardClickedListener: FeedbackCallback
}