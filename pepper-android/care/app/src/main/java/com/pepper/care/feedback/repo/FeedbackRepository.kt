package com.pepper.care.feedback.repo

import android.content.SharedPreferences
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_PUBLISH_MSG_KEY
import com.pepper.care.feedback.entities.FeedbackEntity


interface FeedbackRepository {
    suspend fun addFeedbackType(type: FeedbackEntity.FeedbackMessage)
    suspend fun addFeedbackDescription(string: String)
}

class FeedbackRepositoryImpl(
    private val sharedPreferencesEditor: SharedPreferences.Editor
) : FeedbackRepository {

    override suspend fun addFeedbackType(typeObj: FeedbackEntity.FeedbackMessage) {
        val type = "BOT:3:PATIENT:3:FEEDBACK_OVERAL#{${typeObj.name}}"
        sharedPreferencesEditor.putString(COMMON_SHARED_PREF_PUBLISH_MSG_KEY, type)
            .commit()
    }

    override suspend fun addFeedbackDescription(message: String) {
        sharedPreferencesEditor.putString(COMMON_SHARED_PREF_PUBLISH_MSG_KEY, message).commit()
    }
}