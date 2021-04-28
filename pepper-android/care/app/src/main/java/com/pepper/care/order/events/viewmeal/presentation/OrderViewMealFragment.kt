package com.pepper.care.order.events.viewmeal.presentation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.pepper.care.R
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentOrderDetailBinding
import com.pepper.care.order.presentation.viewmodels.OrderViewModel
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class OrderViewMealFragment : BaseFragment() {

    private val viewModel: OrderViewModel by sharedViewModel<OrderViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentOrderDetailBinding

    override val navigationDestinationId: Int = R.id.orderViewMealFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackPress(view!!)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupDataBinding(inflater, container)
        setToolbarBackButtonVisibility(View.VISIBLE)
        return viewBinding.root
    }

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentOrderDetailBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@OrderViewMealFragment.viewLifecycleOwner
            viewModel = this@OrderViewMealFragment.viewModel
        }
    }
}