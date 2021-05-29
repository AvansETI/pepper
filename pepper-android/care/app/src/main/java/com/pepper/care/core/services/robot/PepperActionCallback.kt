package com.pepper.care.core.services.robot

interface PepperActionCallback {
    fun onRobotAction(action: PepperAction, string: String?)
}

enum class PepperAction {
    NAVIGATE_TO,
    NAVIGATE_TO_CHOICE,
    SELECT_PATIENT_ID,
    SELECT_FEEDBACK_NUMBER,
    CONFIRM_DIALOG_SELECT,
    SELECT_MEAL_ITEM
}