package com.pepper.care.core.services.robot

import com.aldebaran.qi.sdk.`object`.conversation.Phrase

object RobotManager {
    lateinit var robot: PepperRobot

    fun addDynamicContents(concept: DynamicConcepts, list: List<Phrase>) {
        robot.addContents(concept, list)
    }
}