package com.pepper.care.order.common.view

import android.view.View
import android.widget.ImageView
import com.google.android.material.card.MaterialCardView
import com.pepper.care.R
import com.ramotion.cardslider.DefaultViewUpdater
import kotlin.math.max

class CardUpdater : DefaultViewUpdater() {

    override fun updateView(view: View, position: Float) {
        super.updateView(view, position)

        val card = view.findViewById(R.id.clickable_card) as MaterialCardView
        val imageView = card.findViewById<ImageView>(R.id.meal_img_src)
        val alphaView = card.findViewById<View>(R.id.alpha_view)

        if (position < 0) {
            val alpha = view.alpha
            view.alpha = 1f
            alphaView.alpha = 0.9f - alpha
            imageView.alpha = 0.3f + alpha
        } else {
            alphaView.alpha = 0f
            imageView.alpha = 1f
        }

        val ratio = layoutManager.getDecoratedLeft(view).toFloat() / layoutManager.activeCardLeft
        val z: Float = when {
            position < 0 -> {
                Z_CENTER_1 * ratio
            }
            position < 0.5f -> {
                Z_CENTER_1.toFloat()
            }
            position < 1f -> {
                Z_CENTER_2.toFloat()
            }
            else -> {
                Z_RIGHT.toFloat()
            }
        }

        card.cardElevation = max(0f, z)
    }
}