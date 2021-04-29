package com.pepper.care.core.services.time

interface TimeInterfaceCallbacks {
    fun onUpdateInterface(type: InterfaceTime)

    enum class InterfaceTime{
        DAY, NIGHT
    }
}