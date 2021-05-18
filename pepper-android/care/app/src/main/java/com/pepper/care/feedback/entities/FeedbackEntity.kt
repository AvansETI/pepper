package com.pepper.care.feedback.entities

import android.view.View
import com.pepper.care.feedback.FeedbackCallback

class FeedbackEntity(
    val givenType: FeedbackMessage
) {
    fun onClick(view: View, type: FeedbackMessage, callback: FeedbackCallback) {
        callback.onClicked(view, type)
    }

    enum class FeedbackMessage(
        var text: String
    ) {
        GOOD("Het gaat goed"),
        OKAY("Er is niks aan de hand"),
        BAD("Het gaat slecht")
    }
}