package com.abdulkadirkara.paginationsimple.util

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation

enum class ImageShape {
    CIRCLE,
    ROUNDED,
    NONE
}

fun ImageView.loadImage(
    url: String?,
    shape: ImageShape = ImageShape.NONE,
    cornerRadius: Float = 0f,
    @DrawableRes placeholderRes: Int? = null,
    @DrawableRes errorRes: Int? = null
) {
    this.load(url) {
        crossfade(true)
        crossfade(300)

        when (shape) {
            ImageShape.CIRCLE -> transformations(CircleCropTransformation())
            ImageShape.ROUNDED -> transformations(RoundedCornersTransformation(cornerRadius))
            ImageShape.NONE -> {} // No transformation
        }

        placeholderRes?.let { placeholder(it) }
        errorRes?.let { error(it) }
    }
}