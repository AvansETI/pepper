package com.pepper.care.feedback

import com.pepper.care.feedback.entities.FeedbackEntity

interface FeedbackCallback {
    fun onClicked(type: FeedbackEntity.FeedbackMessage)
}