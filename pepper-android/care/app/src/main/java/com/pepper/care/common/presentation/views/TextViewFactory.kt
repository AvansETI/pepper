package com.pepper.care.common.presentation.views

import android.content.Context
import android.view.View
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.annotation.StyleRes

class TextViewFactory constructor(
    val context: Context,
    @StyleRes val styleId: Int
) :
    ViewSwitcher.ViewFactory {
    override fun makeView(): View {
        val textView = TextView(context)
        textView.setTextAppearance(styleId)
        return textView
    }
}