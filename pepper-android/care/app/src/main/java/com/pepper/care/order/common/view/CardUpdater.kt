package com.pepper.care.order.common.view

import android.view.View
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.DefaultViewUpdater
import kotlin.math.abs

class CardUpdater : DefaultViewUpdater() {

    private lateinit var lm: CardSliderLayoutManager
    private lateinit var previewView: View

    private var cardWidth = 0
    private var activeCardLeft = 0
    private var activeCardRight = 0
    private var activeCardCenter = 0
    private var cardsGap = 0f

    private var transitionEnd = 0
    private var transitionDistance = 0
    private var transitionRight2Center = 0f

    private var overlapGap = 30f

    override fun onLayoutManagerInitialized(layoutManager: CardSliderLayoutManager) {
        this.cardWidth = layoutManager.cardWidth
        this.activeCardLeft = layoutManager.activeCardLeft
        this.activeCardRight = layoutManager.activeCardRight
        this.activeCardCenter = layoutManager.activeCardCenter
        this.cardsGap = layoutManager.cardsGap

        this.transitionEnd = activeCardCenter
        this.transitionDistance = activeCardRight - transitionEnd

        val centerBorder = (cardWidth - cardWidth * SCALE_CENTER) / 2f
        val rightBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2f
        val right2centerDistance = activeCardRight + centerBorder - (activeCardRight - rightBorder)
        this.transitionRight2Center = right2centerDistance - cardsGap

        this.lm = layoutManager
    }

    override fun updateView(view: View, position: Float) {
        val scale: Float
        val alpha: Float
        val z: Float
        val x: Float

        when {
            position < 0 -> {
                val ratio = lm.getDecoratedLeft(view).toFloat() / activeCardLeft
                scale = SCALE_LEFT + SCALE_CENTER_TO_LEFT * ratio
                alpha = 0.1f + ratio
                z = Z_CENTER_1 * ratio
                x = 0f - overlapGap
            }
            position < 0.5f -> {
                scale = SCALE_CENTER
                alpha = 1f
                z = Z_CENTER_1.toFloat()
                x = 0f
            }
            position < 1f -> {
                val viewLeft = lm.getDecoratedLeft(view)
                val ratio =
                    (viewLeft - activeCardCenter).toFloat() / (activeCardRight - activeCardCenter)
                scale = SCALE_CENTER - SCALE_CENTER_TO_RIGHT * ratio
                alpha = 1f
                z = Z_CENTER_2.toFloat()
                x =
                    if (abs(transitionRight2Center) < abs(transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance)) {
                        -transitionRight2Center
                    } else {
                        -transitionRight2Center * (viewLeft - transitionEnd) / transitionDistance
                    } - overlapGap
            }
            else -> {
                scale = SCALE_RIGHT
                alpha = 1f
                z = Z_RIGHT.toFloat()

                val prevViewScale: Float
                val prevTransition: Float
                val prevRight: Int
                val isFirstRight = lm.getDecoratedRight(previewView) <= activeCardRight
                if (isFirstRight) {
                    prevViewScale = SCALE_CENTER
                    prevRight = activeCardRight
                    prevTransition = 0f
                } else {
                    prevViewScale = previewView.scaleX
                    prevRight = lm.getDecoratedRight(previewView)
                    prevTransition = previewView.translationX
                }
                val prevBorder = (cardWidth - cardWidth * prevViewScale) / 2
                val currentBorder = (cardWidth - cardWidth * SCALE_RIGHT) / 2
                val distance =
                    lm.getDecoratedLeft(view) + currentBorder - (prevRight - prevBorder + prevTransition)
                val transition = distance - cardsGap
                x = -transition - overlapGap
            }
        }

        view.scaleX = scale
        view.scaleY = scale
        view.z = z
        view.translationX = x
        view.alpha = alpha

        previewView = view
    }
}