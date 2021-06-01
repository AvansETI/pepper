package com.pepper.care.order.presentation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Typeface
import android.os.Bundle
import android.util.Half.toFloat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.annotation.StyleRes
import androidx.recyclerview.widget.RecyclerView
import com.kv.popupimageview.PopupImageView
import com.pepper.care.R
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentOrderBinding
import com.pepper.care.order.common.view.MealSliderItem
import com.pepper.care.order.common.view.SliderAdapter
import com.pepper.care.order.common.view.SliderAdapterItem
import com.pepper.care.order.presentation.viewmodels.OrderViewModel
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import com.ramotion.cardslider.CardSliderLayoutManager
import com.ramotion.cardslider.CardSnapHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


@FlowPreview
@ExperimentalCoroutinesApi
class OrderFragment : BaseFragment() {

    private val viewModel: OrderViewModel by sharedViewModel<OrderViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentOrderBinding

    override val navigationDestinationId: Int = R.id.orderFragment

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: SliderAdapter
    private lateinit var layoutManger: CardSliderLayoutManager

    private lateinit var labelSwitcher: TextSwitcher
    private lateinit var mealSwitcher: TextSwitcher
    private lateinit var descriptionSwitcher: TextSwitcher
    private lateinit var allergiesSwitcher: TextSwitcher
    private lateinit var caloriesSwitcher: TextSwitcher

    private lateinit var meal1TextView: TextView
    private lateinit var meal2TextView: TextView
    private var mealOffset1 = 0
    private var mealOffset2 = 0
    private var mealAnimDuration: Long = 0
    private var currentPosition = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupDataBinding(inflater, container)
        setToolbarBackButtonVisibility(View.GONE)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindToEvents()
    }

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentOrderBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@OrderFragment.viewLifecycleOwner
            viewModel = this@OrderFragment.viewModel
        }
    }

    private fun bindToEvents() {
        viewModel.onStart()
        initRecyclerView()
        initMealText()
        initSwitchers()

        viewModel.recyclerList.observeInLifecycleScope {
            adapter.submitList(it)
        }
    }

    private fun initRecyclerView() {
        recyclerView = this@OrderFragment.viewBinding.mealRecyclerView
        adapter = SliderAdapter(adapterClickedListener)
        recyclerView.adapter = adapter
        recyclerView.setHasFixedSize(true)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onCardChange()
                }
            }
        })

        layoutManger = recyclerView.layoutManager as CardSliderLayoutManager
        CardSnapHelper().attachToRecyclerView(recyclerView)
    }

    private fun onCardChange() {
        val pos: Int = layoutManger.activeCardPosition
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) return

        onChange(pos)
    }

    private fun onChange(pos: Int) {
        val animH = intArrayOf(R.anim.slide_in_right, R.anim.slide_out_left)
        val animV = intArrayOf(R.anim.slide_in_top, R.anim.slide_out_bottom)

        val left2right: Boolean = pos < currentPosition
        if (left2right) {
            animH[0] = R.anim.slide_in_left
            animH[1] = R.anim.slide_out_right
            animV[0] = R.anim.slide_in_bottom
            animV[1] = R.anim.slide_out_top
        }

        val list = viewModel.recyclerList.value!!
        val currentItem = (list[pos % list.size] as MealSliderItem)

        setMealText(currentItem, left2right)

        labelSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        labelSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        labelSwitcher.setText("ID: ${currentItem.id}")

        mealSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        mealSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        mealSwitcher.setText(currentItem.name)

        descriptionSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        descriptionSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        descriptionSwitcher.setText(currentItem.description)

        allergiesSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        allergiesSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        allergiesSwitcher.setText(currentItem.allergies)

        caloriesSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        caloriesSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        caloriesSwitcher.setText("${currentItem.calories} kilocalorieën")

        currentPosition = pos
    }

    private fun initMealText() {
        mealAnimDuration = resources.getInteger(R.integer.labels_animation_duration).toLong()
        mealOffset1 = resources.getDimensionPixelSize(R.dimen.left_offset)
        mealOffset2 = resources.getDimensionPixelSize(R.dimen.card_width)
        meal1TextView = viewBinding.mealTitle1
        meal2TextView = viewBinding.mealTitle2

        meal1TextView.x = mealOffset1.toFloat()
        meal2TextView.x = mealOffset2.toFloat()
        meal2TextView.alpha = 0f
    }

    private fun setMealText(currentItem: MealSliderItem, left2right: Boolean) {
        val invisibleText: TextView
        val visibleText: TextView
        if (meal1TextView.alpha > meal2TextView.alpha) {
            visibleText = meal1TextView
            invisibleText = meal2TextView
        } else {
            visibleText = meal1TextView
            invisibleText = meal2TextView
        }

        val vOffset: Int
        if (left2right) {
            invisibleText.x = 0f
            vOffset = mealOffset2
        } else {
            invisibleText.x = mealOffset2.toFloat()
            vOffset = 0
        }

        invisibleText.text = currentItem.name

        val iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f)
        val vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f)
        val iX = ObjectAnimator.ofFloat(invisibleText, "x", mealOffset1.toFloat())
        val vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset.toFloat())

        val animSet = AnimatorSet()
        animSet.playTogether(iAlpha, vAlpha, iX, vX)
        animSet.duration = mealAnimDuration
        animSet.start()
    }

    private fun initSwitchers() {
        labelSwitcher = viewBinding.labelText
        labelSwitcher.setFactory(TextViewFactory(R.style.Pepper_Care_Label_text, true))

        mealSwitcher = viewBinding.mealType
        mealSwitcher.setFactory(TextViewFactory(R.style.Pepper_Care_Title_Text, false))

        descriptionSwitcher = viewBinding.mealDescription
        descriptionSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        descriptionSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        descriptionSwitcher.setFactory(TextViewFactory(R.style.Pepper_Care_Body_Text, false))

        allergiesSwitcher = viewBinding.mealAllergies
        allergiesSwitcher.setFactory(TextViewFactory(R.style.Pepper_Care_Title_Text, false))

        caloriesSwitcher = viewBinding.mealCalories
        caloriesSwitcher.setFactory(TextViewFactory(R.style.Pepper_Care_Title_Text, false))
    }

    private val adapterClickedListener: ClickCallback<SliderAdapterItem> =
        object : ClickCallback<SliderAdapterItem> {

            override fun onClicked(view: View, item: SliderAdapterItem) {
                when (item.getViewType()) {
                    SliderAdapterItem.ViewTypes.MEAL -> {
                        Log.d(OrderViewModelUsingUsecases::class.simpleName, "Clicked on Item")

                        val lm = recyclerView.layoutManager as CardSliderLayoutManager?

                        if (lm!!.isSmoothScrolling) {
                            return
                        }

                        val activeCardPosition = lm.activeCardPosition
                        if (activeCardPosition == RecyclerView.NO_POSITION) {
                            return
                        }

                        val clickedPosition = recyclerView.getChildAdapterPosition(view)
                        if (clickedPosition == activeCardPosition) {
                            val list = viewModel.recyclerList.value!!
                            val currentItem = (list[clickedPosition] as MealSliderItem)
                            PopupImageView(this@OrderFragment.context, view, currentItem.source)
                        } else if (clickedPosition > activeCardPosition) {
                            recyclerView.smoothScrollToPosition(clickedPosition)
                            onChange(clickedPosition)
                        }
                    }
                }
            }
        }

    inner class TextViewFactory constructor(
        @StyleRes val styleId: Int,
        val center: Boolean
    ) :
        ViewSwitcher.ViewFactory {
        override fun makeView(): View {
            val textView = TextView(this@OrderFragment.context)
            if (center) {
                textView.gravity = Gravity.CENTER
            }
            textView.setTextAppearance(styleId)
            return textView
        }
    }
}