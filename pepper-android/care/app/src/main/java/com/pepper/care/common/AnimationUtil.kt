package com.pepper.care.common

import androidx.navigation.NavOptions
import com.pepper.care.R

object AnimationUtil {

    fun getDefaultAnimation() : NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.fade_out)
            .setPopEnterAnim(R.anim.fade_in)
            .setPopExitAnim(R.anim.fade_out)
            .build()
    }

    fun getFullscreenImageAnimation() : NavOptions {
        return NavOptions.Builder()
            .setEnterAnim(R.anim.scale_up)
            .setExitAnim(R.anim.scale_down)
            .setPopEnterAnim(R.anim.scale_up)
            .setPopExitAnim(R.anim.scale_down)
            .build()
    }
}