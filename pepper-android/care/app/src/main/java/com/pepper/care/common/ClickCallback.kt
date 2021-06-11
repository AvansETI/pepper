package com.pepper.care.common

import android.view.View
import com.pepper.care.order.common.view.SliderAdapterItem

interface ClickCallback <T> {
    fun onClicked(view: View, item: SliderAdapterItem)
}