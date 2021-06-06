package com.pepper.care.core.services.platform

import com.pepper.care.core.services.platform.entities.PlatformEntity

interface PlatformActionCallback {
    fun onCall(platformEntity: PlatformEntity)
}