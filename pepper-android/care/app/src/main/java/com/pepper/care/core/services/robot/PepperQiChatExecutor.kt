package com.pepper.care.core.services.robot

import android.util.Log
import com.aldebaran.qi.sdk.QiContext
import com.aldebaran.qi.sdk.`object`.conversation.BaseQiChatExecutor
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_CF_DLG
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IF_NUM
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_IP_ID
import com.pepper.care.core.services.robot.ExecuteConstants.EXE_NAV

class PepperQiChatExecutor (context: QiContext, val callback: PepperActionCallback) : BaseQiChatExecutor(context) {

    override fun runWith(params: List<String>) {
        Log.d(PepperQiChatExecutor::class.simpleName, "Exe: ${params[0]}, info: ${params[1]}")

        when(params[0]){
            EXE_NAV -> callback.onRobotAction(PepperAction.NAVIGATE_TO, params[1])
            EXE_IP_ID -> callback.onRobotAction(PepperAction.SELECT_PATIENT_ID, params[1])
            EXE_IF_NUM -> callback.onRobotAction(PepperAction.SELECT_FEEDBACK_NUMBER, params[1])
            EXE_CF_DLG -> callback.onRobotAction(PepperAction.CONFIRM_DIALOG_SELECT, params[1])
        }
    }

    override fun stop() {
        Log.d(PepperQiChatExecutor::class.simpleName, "Execute stopped...")
    }
}

object ExecuteConstants {
    const val EXE_NAV: String = "nav"
    const val EXE_IP_ID = "pid"
    const val EXE_IF_NUM = "fnum"
    const val EXE_CF_DLG = "cfdlg"
}