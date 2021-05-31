package com.pepper.care.feedback.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pepper.care.R
import com.pepper.care.common.presentation.views.BaseFragment
import com.pepper.care.databinding.FragmentFeedbackBinding
import com.pepper.care.feedback.presentation.viewmodels.FeedbackViewModel
import com.pepper.care.feedback.presentation.viewmodels.FeedbackViewModelUsingUsecases
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.androidx.viewmodel.ext.android.viewModel

@FlowPreview
@ExperimentalCoroutinesApi
class FeedbackFragment : BaseFragment() {

    private val viewModel: FeedbackViewModel by viewModel<FeedbackViewModelUsingUsecases>()
    private lateinit var viewBinding: FragmentFeedbackBinding

    override val navigationDestinationId: Int = R.id.feedbackFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setupDataBinding(inflater, container)
        setToolbarBackButtonVisibility(View.GONE)
        viewModel.fluidSlider.apply { value = viewBinding.feedbackSlider }
        return viewBinding.root
    }

    private fun setupDataBinding(inflater: LayoutInflater, container: ViewGroup?) {
        viewBinding = FragmentFeedbackBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = this@FeedbackFragment.viewLifecycleOwner
            viewModel = this@FeedbackFragment.viewModel
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindToEvents()
    }

    private fun bindToEvents() {
        viewModel.onStart()
    }
}