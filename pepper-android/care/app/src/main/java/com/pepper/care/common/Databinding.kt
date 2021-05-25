package com.pepper.care.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.pepper.care.R
import com.pepper.care.dialog.FabType
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity
import com.ramotion.fluidslider.FluidSlider
import de.hdodenhof.circleimageview.CircleImageView


@BindingAdapter("items")
fun setItems(
    recyclerView: RecyclerView,
    items: List<Any>?
) {
    if (recyclerView.adapter != null) {
        val adapter = (recyclerView.adapter as ListAdapter<Any, RecyclerView.ViewHolder>)
        adapter.submitList(items)
    }
}

@BindingAdapter("imageSource")
fun setImageUrlSource(imageView: ImageView, url: String?) {
    if (url == null) return
    Glide.with(imageView.context)
        .load(url)
        .into(imageView)
}

@BindingAdapter("isVisible")
fun setIsVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
}

@BindingAdapter("setSpanCount")
fun setSpanCount(recyclerView: RecyclerView, amount: Int) {
    if (recyclerView.layoutManager != null) {
        recyclerView.layoutManager = GridLayoutManager(recyclerView.context, amount)
        Log.d("Databinding", "Recyclerview updated, columns $amount")
    }
}

@BindingAdapter("colorCardSource")
fun setColorSource(imageView: ImageView, type: FeedbackEntity.FeedbackMessage) {
    val image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawColor(
        imageView.resources.getColor(
            when (type) {
                FeedbackEntity.FeedbackMessage.BAD -> R.color.red
                FeedbackEntity.FeedbackMessage.OKAY -> R.color.yellow
                FeedbackEntity.FeedbackMessage.GOOD -> R.color.green
            }, imageView.context.theme
        )
    )

    Glide.with(imageView.context)
        .load(image)
        .into(imageView)
}

@BindingAdapter("colorMoodSource")
fun setMoodColorSource(circleImageView: CircleImageView, type: FeedbackEntity.FeedbackMessage) {
    val image = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(image)
    canvas.drawColor(
        circleImageView.resources.getColor(
            when (type) {
                FeedbackEntity.FeedbackMessage.BAD -> R.color.red
                FeedbackEntity.FeedbackMessage.OKAY -> R.color.yellow
                FeedbackEntity.FeedbackMessage.GOOD -> R.color.green
            }, circleImageView.context.theme
        )
    )

    Glide.with(circleImageView.context)
        .load(image)
        .into(circleImageView)
}

@BindingAdapter("iconSource")
fun setIconSource(imageView: ImageView, type: FeedbackEntity.FeedbackMessage) {
    Glide.with(imageView.context)
        .load(
            when (type) {
                FeedbackEntity.FeedbackMessage.BAD -> R.drawable.ic_feedback_bad
                FeedbackEntity.FeedbackMessage.OKAY -> R.drawable.ic_feedback_okay
                FeedbackEntity.FeedbackMessage.GOOD -> R.drawable.ic_feedback_good
            }
        )
        .into(imageView)
}

@BindingAdapter("fabIcon")
fun setIconSource(floatingActionButton: FloatingActionButton, type: FabType) {
    Glide.with(floatingActionButton.context)
        .load(
            when (type) {
                FabType.NEXT -> R.drawable.ic_baseline_chevron_right_24
                FabType.KEYBOARD -> R.drawable.ic_baseline_keyboard_24
            }
        )
        .into(floatingActionButton)
}

@BindingAdapter("isKeyboardVisible")
fun setKeyboardVisibility(editText: EditText, isVisible: Boolean) {
    if (isVisible) editText.showKeyboard() else editText.hideKeyboard()

}

private fun View.showKeyboard() {
    this.requestFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

private fun View.hideKeyboard() {
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

@BindingAdapter("isKeyboardNumeric")
fun setKeyboardType(editText: EditText, isNumeric: Boolean) {
    editText.inputType = if (isNumeric) InputType.TYPE_CLASS_NUMBER else InputType.TYPE_CLASS_TEXT

}

@BindingAdapter("keyListener")
fun bindKeyListener(editText: EditText, listener: View.OnKeyListener?) {
    editText.setOnKeyListener(listener)
}

@BindingAdapter("textChangedListener")
fun bindTextWatcher(editText: EditText, textWatcher: TextWatcher?) {
    editText.addTextChangedListener(textWatcher)
}

@BindingAdapter("textLength")
fun setTextLength(editText: EditText, length: Int) {
    editText.filters = arrayOf(InputFilter.LengthFilter(length))
}

@BindingAdapter("setSliderRange", "imageCallback")
fun setSliderRange(
    fluidSlider: FluidSlider,
    minMaxPair: Pair<Int, Int>,
    feedbackCallback: FeedbackCallback
) {
    val total = minMaxPair.second - minMaxPair.first
    fluidSlider.positionListener = { pos ->
        fluidSlider.bubbleText = "${minMaxPair.first + (total * pos).toInt()}"
        Log.d("Slider", pos.toString())
        feedbackCallback.onClicked(
            fluidSlider,
            when {
                pos >= 0.7 -> FeedbackEntity.FeedbackMessage.GOOD
                pos < 0.5 -> FeedbackEntity.FeedbackMessage.BAD
                else -> FeedbackEntity.FeedbackMessage.OKAY
            }
        )
    }
    fluidSlider.position = 0.75f
    fluidSlider.startText = "${minMaxPair.first}"
    fluidSlider.endText = "${minMaxPair.second}"
}