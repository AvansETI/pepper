package com.pepper.care.common.utility

import androidx.navigation.NavOptions
import com.pepper.care.R

object AnimationUtil {

    fun getDefaultAnimation() : NavOptions {
        return NavOptions.Builder()
            .build()
    }
}