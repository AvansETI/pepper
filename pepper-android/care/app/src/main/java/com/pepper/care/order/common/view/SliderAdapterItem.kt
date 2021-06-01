package com.pepper.care.order.common.view

abstract class SliderAdapterItem {

    enum class ViewTypes {
        MEAL
    }

    open fun getViewType() : ViewTypes {
        return ViewTypes.MEAL
    }
}

data class MealSliderItem(
    val id: Int,
    val name: String,
    val description: String,
    val allergies: String,
    val calories: Int,
    val source: String
) : SliderAdapterItem() {

    override fun getViewType(): ViewTypes {
        return ViewTypes.MEAL
    }
}