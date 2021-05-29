package com.pepper.care.core.services.robot

interface PepperActionCallback {
    fun onRobotAction(action: PepperAction, string: String?)
}

enum class PepperAction {
    NAVIGATE_TO,
    NAVIGATE_TO_CHOICE,
    SHOW_CONFIRM_DIALOG,
    SELECT_MEAL_ITEM,
    SELECT_ID
}