package com.pepper.care.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.ImageView
import android.widget.TextSwitcher
import androidx.annotation.StyleRes
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.pepper.care.R
import com.pepper.care.common.presentation.views.TextViewFactory
import com.pepper.care.feedback.FeedbackCallback
import com.pepper.care.feedback.entities.FeedbackEntity
import com.ramotion.cardslider.CardSnapHelper
import com.ramotion.fluidslider.FluidSlider
import de.hdodenhof.circleimageview.CircleImageView


@BindingAdapter("items", "notifier")
fun setItems(
    recyclerView: RecyclerView,
    items: List<Any>?,
    notifier: UpdateNotifierCallback
) {
    if (recyclerView.adapter != null) {
        val adapter = (recyclerView.adapter as ListAdapter<Any, RecyclerView.ViewHolder>)
        adapter.submitList(items, Runnable {
            notifier.onUpdate()
        })
    }
}

@BindingAdapter("scrollListener")
fun setScrollListener(recyclerView: RecyclerView?, listener: RecyclerView.OnScrollListener) {
    recyclerView?.addOnScrollListener(listener)
}

@BindingAdapter("cardHelper")
fun setCardSnapHelperSource(recyclerView: RecyclerView?, helper: CardSnapHelper) {
    helper.attachToRecyclerView(recyclerView)
}

@BindingAdapter("switcherResource")
fun setTextSwitcherFactory(textSwitcher: TextSwitcher, @StyleRes styleId: Int) {
    textSwitcher.setOutAnimation(textSwitcher.context, R.anim.leave_text)
    textSwitcher.setFactory(TextViewFactory(textSwitcher.context, styleId))
}

@BindingAdapter("switcherText", "isOpposite")
fun setSwitcherText(textSwitcher: TextSwitcher?, pair: Pair<String, Boolean>, isOpposite: Boolean) {
    val text = pair.first
    if (text.isEmpty() || textSwitcher == null) return

    if (isOpposite) {
        textSwitcher.setInAnimation(textSwitcher.context, R.anim.enter_text_clockwise)
    } else {
        textSwitcher.setInAnimation(textSwitcher.context, R.anim.enter_text)
    }

    val changeCurrent = pair.second
    if (changeCurrent) textSwitcher.setCurrentText(text) else textSwitcher.setText(text)
}

@BindingAdapter("imageSource")
fun setImageUrlSource(imageView: ImageView, url: String?) {
    if (url == null) return
    Glide.with(imageView.context)
        .load(url)
        .into(imageView)
}

@BindingAdapter("photoSource")
fun setPhotoUrlSource(photoView: PhotoView, url: String?) {
    if (url == null) return
    Glide.with(photoView.context)
        .load(url)
        .into(photoView)
}

@BindingAdapter("isVisible")
fun setIsVisible(view: View, isVisible: Boolean) {
    view.isVisible = isVisible
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

@BindingAdapter("setSliderRange", "imageCallback")
fun setSliderRange(
    fluidSlider: FluidSlider,
    minMaxPair: Pair<Int, Int>,
    feedbackCallback: FeedbackCallback
) {
    val total = minMaxPair.second - minMaxPair.first
    fluidSlider.positionListener = { pos ->
        fluidSlider.bubbleText = "${minMaxPair.first + (total * pos).toInt()}"
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