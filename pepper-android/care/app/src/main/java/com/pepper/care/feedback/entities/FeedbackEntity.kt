package com.pepper.care.feedback.entities

class FeedbackEntity(
    val message: FeedbackMessage
) {
    enum class FeedbackMessage(
        var text: String
    ) {
        GOOD("Het gaat goed"),
        OKAY("Er is niks aan de hand"),
        BAD("Het gaat slecht")
    }
}