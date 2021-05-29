package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.BaseQiChatExecutor
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_CF_DLG
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IF_EXP
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IF_NUM
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IP_ID
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IQ_EXP
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_NAV
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_NAV_CH
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_SEL_MEAL

class PepperQiChatExecutor (context: QiContext, val callback: PepperActionCallback) : BaseQiChatExecutor(context) {

    override fun runWith(params: List<String>) {
        Log.d(PepperQiChatExecutor::class.simpleName, "Exe: ${params[0]}, info: ${params[1]}")

        when(params[0]){
            EXE_NAV -> callback.onRobotAction(PepperAction.NAVIGATE_TO, params[1])
            EXE_IP_ID -> callback.onRobotAction(PepperAction.SELECT_PATIENT_ID, params[1])
            EXE_IF_NUM -> callback.onRobotAction(PepperAction.SELECT_FEEDBACK_NUMBER, params[1])
            EXE_IF_EXP -> callback.onRobotAction(PepperAction.INPUT_EXPLAIN_FEEDBACK, params[1])
            EXE_CF_DLG -> callback.onRobotAction(PepperAction.CONFIRM_DIALOG_SELECT, params[1])
            EXE_SEL_MEAL -> callback.onRobotAction(PepperAction.SELECT_MEAL_ITEM, params[1])
            EXE_NAV_CH -> callback.onRobotAction(PepperAction.NAVIGATE_TO_CHOICE, params[1])
            EXE_IQ_EXP -> callback.onRobotAction(PepperAction.INPUT_EXPLAIN_QUESTION, params[1])
        }
    }

    override fun stop() {
        Log.d(PepperQiChatExecutor::class.simpleName, "Execute stopped...")
    }
}

object ExecuteConstants {
    const val EXE_NAV = "nav"
    const val EXE_NAV_CH = "navch"
    const val EXE_IP_ID = "pid"
    const val EXE_IF_NUM = "fnum"
    const val EXE_IF_EXP = "fexp"
    const val EXE_IQ_EXP = "qexp"
    const val EXE_CF_DLG = "cfdlg"
    const val EXE_SEL_MEAL = "sel"
}