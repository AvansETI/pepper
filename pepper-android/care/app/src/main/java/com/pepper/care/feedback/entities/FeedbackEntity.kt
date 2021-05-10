package com.pepper.care.feedback.entities

import com.pepper.care.feedback.FeedbackCallback

class FeedbackEntity(
    val givenType: FeedbackMessage
) {
    fun onClick(type: FeedbackMessage, callback: FeedbackCallback) {
        callback.onClicked(type)
    }

    enum class FeedbackMessage(
        var text: String
    ) {
        GOOD("Het gaat goed"),
        OKAY("Er is niks aan de hand"),
        BAD("Het gaat slecht")
    }
}