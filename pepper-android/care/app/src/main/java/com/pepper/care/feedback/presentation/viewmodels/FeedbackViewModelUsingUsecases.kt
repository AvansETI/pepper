package com.pepper.care.feedback.presentation.viewmodels

import android.app.Activity
import android.graphics.Typeface
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import androidx.core.os.bundleOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.awesomedialog.*
import com.pepper.care.R
import com.pepper.care.common.DialogCallback
import com.pepper.care.common.DialogUtil
import com.pepper.care.dialog.DialogConstants
import com.pepper.care.dialog.DialogRoutes
import com.pepper.care.dialog.FabType
import com.pepper.care.dialog.common.views.FabCallback
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MAX_RANGE
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MIN_RANGE
import com.pepper.care.feedback.FeedbackConstants.FEEDBACK_MOCK_EXPLANATION
import com.pepper.care.feedback.common.usecases.AddPatientGivenHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.common.usecases.AddPatientHealthFeedbackUseCaseUsingRepository
import com.pepper.care.feedback.entities.FeedbackEntity
import kotlinx.coroutines.launch
import java.lang.IllegalStateException
import java.util.*
import kotlin.concurrent.schedule

class FeedbackViewModelUsingUsecases(
    private val feedbackType: AddPatientHealthFeedbackUseCaseUsingRepository,
    private val feedbackExplain: AddPatientGivenHealthFeedbackUseCaseUsingRepository
) : ViewModel(), FeedbackViewModel {

    override val headerText: String = "Hoe voelt u zich momenteel?"
    override val sliderRange: Pair<Int, Int> = Pair(FEEDBACK_MIN_RANGE, FEEDBACK_MAX_RANGE)
    override val givenFeedbackType: MutableLiveData<FeedbackEntity.FeedbackMessage> =
        MutableLiveData(FeedbackEntity.FeedbackMessage.GOOD)

    override fun onStart() {
        setupKeyboardButton()
    }

    override val imageListener: FeedbackCallback =
        object : FeedbackCallback {
            override fun onClicked(view: View, type: FeedbackEntity.FeedbackMessage) {
                givenFeedbackType.postValue(type)
            }
        }

    private val dialogCallback: DialogCallback =
        object : DialogCallback {
            override fun onDialogConfirm(view: View) {
                viewModelScope.launch {
                    feedbackType.invoke(givenFeedbackType.value!!)
                    feedbackExplain.invoke( if (inputText.value!!.isNotBlank()) inputText.value!! else FEEDBACK_MOCK_EXPLANATION)
                }
                view.findNavController().navigate(
                    R.id.dialogFragment, bundleOf(
                        Pair<String, DialogRoutes>("ROUTE_TYPE", DialogRoutes.GOODBYE)
                    )
                )
            }
        }

    override val inputText: MutableLiveData<String> = MutableLiveData("")
    override val isNextButtonVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    override val isKeyboardVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    override val isKeyboardNumeric: MutableLiveData<Boolean> = MutableLiveData(false)
    override val inputTextLength: MutableLiveData<Int> = MutableLiveData(0)
    override val fabType: MutableLiveData<FabType> = MutableLiveData(FabType.KEYBOARD)

    override val fabCallback: FabCallback =
        object : FabCallback {
            override fun onClick(view: View) {
                when (fabType.value) {
                    FabType.KEYBOARD -> {
                        inputTextLength.apply { value = DialogConstants.DIALOG_MOCK_MSG_LENGTH }
                        isKeyboardNumeric.apply { value = false }
                        isKeyboardVisible.postValue(true)
                    }
                    else -> throw IllegalStateException("Not a valid option")
                }
            }
        }

    private fun setupKeyboardButton() {
        fabType.apply {
            value = FabType.KEYBOARD
        }
        isNextButtonVisible.postValue(true)
    }

    override val keyboardKeyListener: View.OnKeyListener =
        View.OnKeyListener { view, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                isKeyboardVisible.postValue(false)
                DialogUtil.buildDialog(
                    view,
                    if (inputText.value!!.isNotBlank()) "${givenFeedbackType.value!!.text}, ${inputText.value}." else "${givenFeedbackType.value!!.text}, $FEEDBACK_MOCK_EXPLANATION.",
                    DialogRoutes.FEEDBACK, dialogCallback
                )
                true
            } else false
        }

    override val inputTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Do nothing.
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            inputText.apply {
                value = s.toString()
            }
        }

        override fun afterTextChanged(s: Editable?) {
            // Do nothing.
        }
    }
}