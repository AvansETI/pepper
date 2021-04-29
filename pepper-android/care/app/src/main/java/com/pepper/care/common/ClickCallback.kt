package com.pepper.care.common

import android.view.View
import com.pepper.care.common.entities.RecyclerAdapterItem

interface ClickCallback <T> {
    fun onClicked(view: View, item: RecyclerAdapterItem)
}