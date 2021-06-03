package com.pepper.care.feedback.repo

import com.pepper.care.feedback.entities.FeedbackEntity

interface FeedbackRepository {
    suspend fun addFeedbackType(type: FeedbackEntity.FeedbackMessage)
    suspend fun addFeedbackDescription(string: String)
}

class FeedbackRepositoryImpl(
) : FeedbackRepository {

    override suspend fun addFeedbackType(msg: FeedbackEntity.FeedbackMessage) {
        val formatted = "BOT:3:PATIENT:3:FEEDBACK_OVERAL#{${msg.name}}"
    }

    override suspend fun addFeedbackDescription(string: String) {
        val formatted = "BOT:3:PATIENT:3:FEEDBACK_GIVEN#{${string}}"
    }
}