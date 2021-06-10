package com.pepper.care.order.presentation.viewmodels

import android.view.View
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.aldebaran.qi.sdk.`object`.conversation.Phrase
import com.pepper.care.R
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.UpdateNotifierCallback
import com.pepper.care.common.usecases.GetPatientUseCaseUsingRepository
import com.pepper.care.core.services.platform.entities.Allergy
import com.pepper.care.core.services.robot.DynamicConcepts
import com.pepper.care.core.services.robot.RobotManager
import com.pepper.care.dialog.DialogConstants.DIALOG_MOCK_ERROR
import com.pepper.care.order.common.usecases.GetPlatformMealsUseCaseUsingRepository
import com.pepper.care.order.common.view.ErrorSliderItem
import com.pepper.care.order.common.view.MealSliderItem
import com.pepper.care.order.common.view.SliderAdapterItem
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

class OrderViewModelUsingUsecases(
    private val get: GetPatientUseCaseUsingRepository,
    private val getPlatformMealsUseCaseUsingRepository: GetPlatformMealsUseCaseUsingRepository
) : ViewModel(), OrderViewModel {

    override val recyclerList = MutableLiveData<ArrayList<SliderAdapterItem>>(arrayListOf())
    override val recyclerVisibility: MutableLiveData<Boolean> = MutableLiveData(false)
    override val isLoadedSuccessfully: MutableLiveData<Boolean> = MutableLiveData(false)
    override val progressVisibility: MutableLiveData<Boolean> = MutableLiveData(true)
    override val hintVisibility: MutableLiveData<Boolean> = MutableLiveData(true)
    override val isOpposite: MutableLiveData<Boolean> = MutableLiveData(false)

    override val meal: MutableLiveData<MealSliderItem> = MutableLiveData()
    private val fetchedName: MutableLiveData<String> = MutableLiveData("")

    override val currentPosition: MutableLiveData<Int> = MutableLiveData(-1)
    override val cardSnapHelper: CardSnapHelper = CardSnapHelper()

    override val titleText: MutableLiveData<Pair<String, Boolean>> =
        MutableLiveData(Pair("", false))
    override val labelText: MutableLiveData<Pair<String, Boolean>> =
        MutableLiveData(Pair("", false))
    override val descriptionText: MutableLiveData<Pair<String, Boolean>> =
        MutableLiveData(Pair("", false))
    override val allergiesText: MutableLiveData<Pair<String, Boolean>> =
        MutableLiveData(Pair("", false))

    override val switcherTitleResource: Int = R.style.Pepper_Care_Title_Text_Order
    override val switcherLabelResource: Int = R.style.Pepper_Care_Label_text
    override val switcherDescriptionResource: Int = R.style.Pepper_Care_Body_Text_Order
    override val switcherAllergiesResource: Int = R.style.Pepper_Care_Body_Text_Order

    override fun onStart() {
        fetchPatientDetails()
        showProgressView(true)
        showElements(false)
        viewModelScope.launch {
            getPlatformMealsUseCaseUsingRepository.invoke().asLiveData().observeForever{
                if (it.isNotEmpty()) {
                    val newList: ArrayList<SliderAdapterItem> = ArrayList()

                    it.forEach { item ->
                        newList.add(
                            MealSliderItem(
                                item.id!!,
                                item.name!!,
                                item.description!!,
                                item.allergies!!,
                                item.calories!!,
                                item.image!!,
                                false
                            )
                        )
                    }

                    val randomItem = newList.random() as MealSliderItem
                    randomItem.isFavorite = true
                    RobotManager.addDynamicContents(DynamicConcepts.FAV, Collections.singletonList(Phrase(randomItem.name)))

                    addDynamicContents(newList)
                    recyclerList.postValue(newList)
                    showElements(true)
                } else {
                    recyclerList.value =
                        arrayListOf(ErrorSliderItem(ErrorSliderItem.ErrorText.NO_MEALS_RESULTS_FOUND))
                    showElements(false)
                }
                showProgressView(false)
            }
        }
    }

    private fun showProgressView(boolean: Boolean) {
        this@OrderViewModelUsingUsecases.progressVisibility.apply { value = boolean }
        this@OrderViewModelUsingUsecases.recyclerVisibility.apply { value = !boolean }
    }

    private fun showElements(boolean: Boolean) {
        this@OrderViewModelUsingUsecases.isLoadedSuccessfully.apply { value = boolean }
    }

    override fun onBackPress(view: View) {
        view.findNavController().popBackStack()
    }

    private fun fetchPatientDetails() {
        viewModelScope.launch {
        val name = get.invoke().value
            fetchedName.apply { value = name }
            RobotManager.addDynamicContents(
                DynamicConcepts.NAME,
                listOf(Phrase(name))
            )
        }
    }

    private fun addDynamicContents(list: List<SliderAdapterItem>) {
        val mealPhrases: ArrayList<Phrase> = ArrayList()
        list.forEach {
            if (it.getViewType() == SliderAdapterItem.ViewTypes.MEAL) {
                it as MealSliderItem
                mealPhrases.add(Phrase(it.name))
            }
        }
        RobotManager.addDynamicContents(DynamicConcepts.MEALS, mealPhrases)
    }

    override val notifyCallback: UpdateNotifierCallback = object : UpdateNotifierCallback {
        override fun onUpdate() {
            showProgressView(false)
            if (!recyclerList.value.isNullOrEmpty()) updateTextSwitchers()
        }
    }

    private fun updateTextSwitchers() {
        val firstItem: SliderAdapterItem = recyclerList.value!![0]

        if (firstItem is ErrorSliderItem) {
            titleText.apply { value = Pair(DIALOG_MOCK_ERROR, true) }
            return
        }

        changeSwitchersText(firstItem as MealSliderItem, true)
    }

    override val recyclerScrollListener: RecyclerView.OnScrollListener =
        object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val pos =
                        (recyclerView.layoutManager as CardSliderLayoutManager).activeCardPosition
                    if (pos == RecyclerView.NO_POSITION || pos == currentPosition.value) return
                    onChange(pos)
                }
            }
        }

    override val adapterClickedListener: ClickCallback<SliderAdapterItem> =
        object : ClickCallback<SliderAdapterItem> {

            override fun onClicked(view: View, item: SliderAdapterItem) {
                when (item.getViewType()) {
                    SliderAdapterItem.ViewTypes.MEAL -> {
                        this@OrderViewModelUsingUsecases.meal.apply {
                            value = item as MealSliderItem
                        }
                        val recyclerView =
                            view.rootView.findViewById<RecyclerView>(R.id.meal_recycler_view)
                        val layoutManager = recyclerView.layoutManager as CardSliderLayoutManager?

                        if (layoutManager!!.isSmoothScrolling) {
                            return
                        }

                        val activeCardPosition = layoutManager.activeCardPosition
                        if (activeCardPosition == RecyclerView.NO_POSITION) {
                            return
                        }

                        val clickedPosition = recyclerView.getChildAdapterPosition(view)
                        if (clickedPosition != activeCardPosition) {
                            recyclerView.smoothScrollToPosition(clickedPosition)
                            onChange(clickedPosition)
                        }
                    }
                    else -> {
                    }
                }
            }
        }

    private fun onChange(pos: Int) {
        val animH = intArrayOf(R.anim.slide_in_right, R.anim.slide_out_left)
        val animV = intArrayOf(R.anim.slide_in_top, R.anim.slide_out_bottom)

        val left2right: Boolean = pos < currentPosition.value!!
        if (left2right) {
            animH[0] = R.anim.slide_in_left
            animH[1] = R.anim.slide_out_right
            animV[0] = R.anim.slide_in_bottom
            animV[1] = R.anim.slide_out_top
        }

        val list = recyclerList.value!!
        val currentItem: SliderAdapterItem = list[pos % list.size]

        if (currentItem is ErrorSliderItem) {
            titleText.apply { value = Pair(DIALOG_MOCK_ERROR, true) }
            return
        }

        isOpposite.apply { value = pos < currentPosition.value!! }
        changeSwitchersText(currentItem as MealSliderItem, false)
        currentPosition.apply { value = pos }
    }

    private fun changeSwitchersText(item: MealSliderItem, changeCurrent: Boolean) {
        titleText.apply { value = Pair(item.name, changeCurrent) }
        labelText.apply { value = Pair("${item.calories} Kcal", changeCurrent) }
        descriptionText.apply { value = Pair(item.description, changeCurrent) }
        allergiesText.apply { value = Pair(formatAllergies(item.allergies), changeCurrent) }
    }

    private fun formatAllergies(allergies: Set<Allergy>): String {
        var text = ""
        allergies.forEach { allergy ->
            text += "${allergy.text} "
        }
        return text
    }
}