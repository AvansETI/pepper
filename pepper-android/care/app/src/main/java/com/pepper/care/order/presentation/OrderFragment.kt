package com.pepper.care.order.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextSwitcher
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.pepper.care.R
import com.pepper.care.common.ClickCallback
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.common.presentation.views.TextViewFactory
import com.pepper.care.common.AnimationUtil
import com.pepper.care.databinding.FragmentOrderBinding
import com.pepper.care.order.common.view.ErrorSliderItem
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
    private var currentPosition = -1

    private lateinit var titleSwitcher: TextSwitcher
    private lateinit var labelSwitcher: TextSwitcher
    private lateinit var descriptionSwitcher: TextSwitcher
    private lateinit var allergiesSwitcher: TextSwitcher

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
        initSwitchers()

        viewModel.recyclerList.observeInLifecycleScope {
            Log.d(OrderFragment::class.simpleName, "List received")

            if (it.isNotEmpty()){
                adapter.submitList(it)
                onCardChange()
            }
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

    private fun initSwitchers() {
        titleSwitcher = viewBinding.mealTitle
        titleSwitcher.setFactory(TextViewFactory(this@OrderFragment.requireContext(), R.style.Pepper_Care_Title_Text_Order, false))

        labelSwitcher = viewBinding.labelText
        labelSwitcher.setFactory(TextViewFactory(this@OrderFragment.requireContext(), R.style.Pepper_Care_Label_text, true))

        descriptionSwitcher = viewBinding.mealDescription
        descriptionSwitcher.setInAnimation(this@OrderFragment.requireContext(), android.R.anim.fade_in)
        descriptionSwitcher.setOutAnimation(this@OrderFragment.requireContext(), android.R.anim.fade_out)
        descriptionSwitcher.setFactory(TextViewFactory(this@OrderFragment.requireContext(), R.style.Pepper_Care_Body_Text_Order, false))

        allergiesSwitcher = viewBinding.mealAllergies
        allergiesSwitcher.setFactory(TextViewFactory(this@OrderFragment.requireContext(), R.style.Pepper_Care_Body_Text_Order, false))
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
        val currentItem: SliderAdapterItem = list[pos % list.size]

        if (currentItem is ErrorSliderItem){
            titleSwitcher.setText("Ohnee er is iets fout gegaan...")
            return
        }
        currentItem as MealSliderItem

        titleSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        titleSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        titleSwitcher.setCurrentText(currentItem.name)

        labelSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        labelSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        labelSwitcher.setText("${currentItem.calories} Kcal")

        descriptionSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        descriptionSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        descriptionSwitcher.setText(currentItem.description)

        allergiesSwitcher.setInAnimation(this@OrderFragment.context, android.R.anim.fade_in)
        allergiesSwitcher.setOutAnimation(this@OrderFragment.context, android.R.anim.fade_out)
        allergiesSwitcher.setText(currentItem.allergies)

        currentPosition = pos
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
                            viewModel.meal.apply { value = item as MealSliderItem }
                            view.findNavController().navigate(
                                R.id.fullscreenImageFragment,
                                null,
                                AnimationUtil.getFullscreenImageAnimation()
                            )
                        } else if (clickedPosition > activeCardPosition) {
                            recyclerView.smoothScrollToPosition(clickedPosition)
                            onChange(clickedPosition)
                        }
                    }
                }
            }
        }
}