package com.pepper.care.feedback.repo

import com.pepper.care.common.repo.AppPreferencesRepository
import com.pepper.care.core.services.platform.entities.PlatformMessageBuilder
import com.pepper.care.feedback.entities.FeedbackEntity

interface FeedbackRepository {
    suspend fun addState(type: FeedbackEntity)
    suspend fun addExplanation(string: String)
}

class FeedbackRepositoryImpl(
    private val appPreferences: AppPreferencesRepository
) : FeedbackRepository {

    override suspend fun addState(type: FeedbackEntity) {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.PersonType.PATIENT)
                .message(PlatformMessageBuilder.MessageType.PUSH_FEEDBACK_STATE)
                .data(type.message.name)
                .build()
                .format()
        )
    }

    override suspend fun addExplanation(string: String) {
        appPreferences.updatePublishMessage(
            PlatformMessageBuilder.Builder()
                .person(PlatformMessageBuilder.PersonType.PATIENT)
                .message(PlatformMessageBuilder.MessageType.PUSH_FEEDBACK_EXPLANATION)
                .data(string)
                .build()
                .format()
        )
    }
}