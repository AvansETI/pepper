package com.pepper.care.common.presentation.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.pepper.care.R
import com.pepper.care.databinding.FragmentFullscreenImageBinding
import com.pepper.care.order.presentation.viewmodels.OrderViewModel
import com.pepper.care.order.presentation.viewmodels.OrderViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

@FlowPreview
@ExperimentalCoroutinesApi
class FullscreenImageFragment : BaseFragment() {

    private val viewModel: OrderViewModel by sharedViewModel<OrderViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentFullscreenImageBinding

    override val navigationDestinationId: Int = R.id.fullscreenImageFragment

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
        viewBinding = FragmentFullscreenImageBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@FullscreenImageFragment.viewLifecycleOwner
            viewModel = this@FullscreenImageFragment.viewModel
        }
    }
}