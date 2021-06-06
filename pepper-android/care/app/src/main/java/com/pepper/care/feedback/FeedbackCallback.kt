package com.pepper.care.feedback

import android.view.View
import com.pepper.care.feedback.entities.FeedbackEntity

interface FeedbackCallback {
    fun onUpdate(view: View, type: FeedbackEntity.FeedbackMessage)
}