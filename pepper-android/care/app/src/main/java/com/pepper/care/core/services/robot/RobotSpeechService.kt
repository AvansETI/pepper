package com.pepper.care.core.services.robot

import com.aldebaran.qi.Future
import androidx.lifecycle.MutableLiveData
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.Chat
import com.aldebaran.qi.sdk.`object`.conversation.EditablePhraseSet
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.aldebaran.qi.sdk.`object`.conversation.QiChatbot
import java.util.*

object RobotSpeechService {

    private val liveChatBot: MutableLiveData<QiChatbot> = MutableLiveData()
    private val liveChat: MutableLiveData<Chat> = MutableLiveData()
    private var dynamicMeals: EditablePhraseSet? = null

    fun setChat(chat: Chat) {
        liveChat.postValue(chat)
    }

    fun setChatBot(chatBot: QiChatbot?) {
        liveChatBot.postValue(chatBot)
    }

    fun onLost() {
        liveChat.value!!.removeAllOnStartedListeners()
    }

    fun getDynamicConcept(chatBot: QiChatbot) {
        dynamicMeals = chatBot.dynamicConcept("meals")
    }

    fun addDynamicContent(phrase: String) {
        dynamicMeals?.async()?.addPhrases(Collections.singletonList(Phrase(phrase)))
    }

    fun addDynamicContents(list: List<Phrase>) {
        dynamicMeals?.async()?.addPhrases(list)
    }

    fun removeDynamicContent(phrase: String) {
        dynamicMeals?.async()?.removePhrases(Collections.singletonList(Phrase(phrase)))
    }

    fun removeDynamicContents(list: List<Phrase>) {
        dynamicMeals?.async()?.removePhrases(list)
    }
}
