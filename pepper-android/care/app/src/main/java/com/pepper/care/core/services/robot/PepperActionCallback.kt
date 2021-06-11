package com.pepper.care.core.services.robot

interface PepperActionCallback {
    fun onRobotAction(action: PepperAction, string: String?)
}

enum class PepperAction {
    NAVIGATE_TO,
    SELECT_PATIENT_BIRTHDAY,
    SELECT_PATIENT_NAME,
    SELECT_FEEDBACK_NUMBER,
    INPUT_EXPLAIN_FEEDBACK,
    INPUT_EXPLAIN_QUESTION,
    CONFIRM_DIALOG_SELECT,
    SELECT_MEAL_ITEM
}