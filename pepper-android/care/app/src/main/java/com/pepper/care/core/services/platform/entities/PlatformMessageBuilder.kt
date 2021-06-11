package com.pepper.care.core.services.platform.entities

class PlatformMessageBuilder private constructor(
    private val sender: Sender?,
    private val senderId: String?,
    private val person: Person?,
    private val personId: String?,
    private val task: Task?,
    private val taskId: String?,
    private val data: String?) {

    enum class Sender {
        BOT,
        PLATFORM
    }

    enum class Person {
        NONE,
        PATIENT
    }

    enum class Task {
        UNDEFINED,

        FEEDBACK,
        FEEDBACK_ID,
        FEEDBACK_STATUS,
        FEEDBACK_EXPLANATION,
        FEEDBACK_TIMESTAMP,

        MEAL,
        MEAL_ID,
        MEAL_NAME,
        MEAL_DESCRIPTION,
        MEAL_CALORIES,
        MEAL_ALLERGIES,
        MEAL_IMAGE,

        MEAL_ORDER,
        MEAL_ORDER_ID,
        MEAL_ORDER_MEAL_ID,
        MEAL_ORDER_TIMESTAMP,

        ANSWER,
        ANSWER_ID,
        ANSWER_TEXT,
        ANSWER_QUESTION_ID,
        ANSWER_TIMESTAMP,

        QUESTION,
        QUESTION_ID,
        QUESTION_TEXT,
        QUESTION_TIMESTAMP,

        REMINDER,
        REMINDER_ID,
        REMINDER_THING,
        REMINDER_TIMESTAMP,

        PATIENT,
        PATIENT_ID,
        PATIENT_NAME,
        PATIENT_BIRTHDATE,
        PATIENT_ALLERGIES
    }

    data class Builder(
        private var sender: Sender? = null,
        private var senderId: String? = null,
        private var person: Person? = null,
        private var personId: String? = null,
        private var task: Task? = null,
        private var taskId: String? = null,
        private var data: String? = null) {

        fun sender(sender: Sender) = apply { this.sender = sender }
        fun senderId(senderId: String) = apply { this.senderId = senderId }
        fun person(person: Person) = apply { this.person = person }
        fun personId(personId: String) = apply { this.personId = personId }
        fun task(task: Task) = apply { this.task = task }
        fun taskId(taskId: String) = apply { this.taskId = taskId }
        fun data(data: String) = apply { this.data = data }

        fun build() = PlatformMessageBuilder(sender, senderId, person, personId, task, taskId, data).format()
    }

    fun format(): String {
        return "${sender?: Sender.BOT}:${senderId?: "1"}:${person?: Person.NONE}:${personId?: "-1"}:${task?: Task.UNDEFINED}:${taskId?: "-1"}#{${data?: ""}}"
    }
}