package com.pepper.care.feedback

import android.view.View
import com.pepper.care.feedback.entities.FeedbackEntity

interface FeedbackCallback {
    fun onClicked(type: FeedbackEntity.FeedbackTypes)
}