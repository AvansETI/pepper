package com.pepper.care.common.presentation.views

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.annotation.StyleRes

class TextViewFactory constructor(
    val context: Context,
    @StyleRes val styleId: Int,
    val center: Boolean
) :
    ViewSwitcher.ViewFactory {
    override fun makeView(): View {
        val textView = TextView(context)
        if (center) {
            textView.gravity = Gravity.CENTER
        }
        textView.setTextAppearance(styleId)
        return textView
    }
}