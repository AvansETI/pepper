package com.pepper.care.common.entities

abstract class RecyclerAdapterItem {

    enum class ViewTypes {
        MEAL, INFORM
    }

    open fun getType() : ViewTypes {
        return ViewTypes.MEAL
    }
}

data class InformUserRecyclerItem(
    var informType: InformText
) : RecyclerAdapterItem() {

    enum class InformText(val text: String) {
        NO_RESULTS_FOUND("Geen resultaten gevonden."),
        NO_MEALS_RESULTS_FOUND("Er zijn geen maaltijden gevonden.")
    }

    override fun getType() : ViewTypes {
        return ViewTypes.INFORM
    }
}