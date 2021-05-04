package com.pepper.care.feedback.entities

import com.pepper.care.feedback.FeedbackCallback

class FeedbackEntity(
    val givenType: FeedbackTypes
) {
    fun onClick(type: FeedbackTypes, callback: FeedbackCallback) {
        callback.onClicked(type)
    }

    enum class FeedbackTypes(
        var text: String
    ) {
        GOOD("Het gaat goed"),
        OKAY("Er is niks aan de hand"),
        BAD("Het gaat slecht")
    }
}