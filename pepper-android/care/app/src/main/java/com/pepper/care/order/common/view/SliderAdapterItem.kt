package com.pepper.care.order.common.view

abstract class SliderAdapterItem {

    enum class ViewTypes {
        MEAL, ERROR
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
    val source: String,
    var isFavorite: Boolean
) : SliderAdapterItem() {

    override fun getViewType(): ViewTypes {
        return ViewTypes.MEAL
    }
}

data class ErrorSliderItem(
    var errorType: ErrorText
) : SliderAdapterItem() {

    enum class ErrorText(val text: String) {
        NO_MEALS_RESULTS_FOUND("Geen maaltijden gevonden.")
    }

    override fun getViewType() : ViewTypes {
        return ViewTypes.ERROR
    }
}