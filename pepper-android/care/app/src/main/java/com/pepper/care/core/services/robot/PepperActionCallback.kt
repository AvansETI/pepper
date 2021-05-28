package com.pepper.care.core.services.robot

interface PepperActionCallback {
    fun onRobotAction(action: PepperAction)
}

enum class PepperAction() {
    MOVE_TO_INTRO
}