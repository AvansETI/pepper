package com.pepper.care.core.services.platform.entities

class PlatformMessageBuilder private constructor(
    private val botId: String?,
    private val personType: PersonType?,
    private val generalId: String?,
    private val messageType: MessageType?,
    private val data: String?) {

    enum class PersonType {
        GENERAL,
        PATIENT
    }

    enum class MessageType {
        UNDEFINED,
        FETCH_USERNAME,
        FETCH_MEALS,
        PUSH_MEAL,
        FETCH_REMINDERS,
        FETCH_QUESTION,
        PUSH_QUESTION_EXPLANATION,
        PUSH_FEEDBACK_STATE,
        PUSH_FEEDBACK_EXPLANATION,
    }

    data class Builder(
        private var botId: String? = null,
        private var personType: PersonType? = null,
        private  var generalId: String? = null,
        private var messageType: MessageType? = null,
        private var data: String? = null) {

        fun bot(bot: String) = apply { this.botId = bot }
        fun person(person: PersonType) = apply { this.personType = person }
        fun identification(id: String) = apply { this.generalId = id }
        fun message(message: MessageType) = apply { this.messageType = message }
        fun data(data: String) = apply { this.data = data }

        fun build() = PlatformMessageBuilder(botId, personType, generalId, messageType, data)
    }

    fun format(): String {
        return "BOT:${botId?: 0}:${personType?: PersonType.GENERAL}:${generalId?: 0}:${messageType ?: MessageType.UNDEFINED}#{${data?: ""}}"
    }
}