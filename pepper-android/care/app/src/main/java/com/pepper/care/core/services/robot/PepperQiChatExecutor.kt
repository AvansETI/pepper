package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.BaseQiChatExecutor
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_CFM_DLG
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IP_ID
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_NAV
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_NAV_CH
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_SEL_MEAL

class PepperQiChatExecutor (context: QiContext, val callback: PepperActionCallback) : BaseQiChatExecutor(context) {

    override fun runWith(params: List<String>) {
        Log.d(PepperQiChatExecutor::class.simpleName, "Exe: ${params[0]}, info: ${params[1]}")

        when(params[0]){
            EXE_NAV -> callback.onRobotAction(PepperAction.NAVIGATE_TO, params[1])
            EXE_NAV_CH -> callback.onRobotAction(PepperAction.NAVIGATE_TO_CHOICE, params[1])
            EXE_CFM_DLG -> callback.onRobotAction(PepperAction.SHOW_CONFIRM_DIALOG, params[1])
            EXE_SEL_MEAL -> callback.onRobotAction(PepperAction.SELECT_MEAL_ITEM, params[1])
            EXE_IP_ID -> callback.onRobotAction(PepperAction.SELECT_ID, params[1])
        }
    }

    override fun stop() {
        Log.d(PepperQiChatExecutor::class.simpleName, "Execute stopped...")
    }
}

object ExecuteConstants {
    const val EXE_NAV: String = "nav"
    const val EXE_NAV_CH: String = "navch"
    const val EXE_CFM_DLG: String = "dlg"
    const val EXE_SEL_MEAL = "sel"
    const val EXE_IP_ID = "ipid"
}