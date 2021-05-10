package com.pepper.care.feedback.repo

import android.content.SharedPreferences
import com.pepper.care.common.CommonConstants.COMMON_SHARED_PREF_PUBLISH_MSG_KEY
import com.pepper.care.feedback.entities.FeedbackType


interface FeedbackRepository {
    suspend fun addFeedbackType(type: FeedbackType)
    suspend fun addFeedbackDescription(string: String)
}

class FeedbackRepositoryImpl(
    private val sharedPreferencesEditor: SharedPreferences.Editor
) : FeedbackRepository {

    override suspend fun addFeedbackType(type: FeedbackType) {
        sharedPreferencesEditor.putString(COMMON_SHARED_PREF_PUBLISH_MSG_KEY, type.toString())
            .commit()
    }

    override suspend fun addFeedbackDescription(msg: String) {
        sharedPreferencesEditor.putString(COMMON_SHARED_PREF_PUBLISH_MSG_KEY, msg).commit()
    }
}