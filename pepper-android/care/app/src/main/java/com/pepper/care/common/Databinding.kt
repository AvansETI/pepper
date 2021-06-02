package com.pepper.care.common

import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.pepper.care.R
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