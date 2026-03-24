package com.yandex.div.compose.views.gallery

import androidx.compose.ui.Alignment
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivGallery

internal fun DivGallery.CrossContentAlignment.toVerticalAlignment(): Alignment.Vertical =
    when (this) {
        DivGallery.CrossContentAlignment.START -> Alignment.Top
        DivGallery.CrossContentAlignment.CENTER -> Alignment.CenterVertically
        DivGallery.CrossContentAlignment.END -> Alignment.Bottom
    }

internal fun DivGallery.CrossContentAlignment.toHorizontalAlignment(): Alignment.Horizontal =
    when (this) {
        DivGallery.CrossContentAlignment.START -> Alignment.Start
        DivGallery.CrossContentAlignment.CENTER -> Alignment.CenterHorizontally
        DivGallery.CrossContentAlignment.END -> Alignment.End
    }

internal fun DivGallery.CrossContentAlignment.toBoxAlignment(isHorizontal: Boolean): Alignment =
    if (isHorizontal) {
        when (this) {
            DivGallery.CrossContentAlignment.START -> Alignment.TopCenter
            DivGallery.CrossContentAlignment.CENTER -> Alignment.Center
            DivGallery.CrossContentAlignment.END -> Alignment.BottomCenter
        }
    } else {
        when (this) {
            DivGallery.CrossContentAlignment.START -> Alignment.CenterStart
            DivGallery.CrossContentAlignment.CENTER -> Alignment.Center
            DivGallery.CrossContentAlignment.END -> Alignment.CenterEnd
        }
    }

internal fun DivAlignmentVertical.toGalleryCrossAlignment(): DivGallery.CrossContentAlignment =
    when (this) {
        DivAlignmentVertical.TOP,
        DivAlignmentVertical.BASELINE -> DivGallery.CrossContentAlignment.START
        DivAlignmentVertical.CENTER -> DivGallery.CrossContentAlignment.CENTER
        DivAlignmentVertical.BOTTOM -> DivGallery.CrossContentAlignment.END
    }

internal fun DivAlignmentHorizontal.toGalleryCrossAlignment(): DivGallery.CrossContentAlignment =
    when (this) {
        DivAlignmentHorizontal.LEFT,
        DivAlignmentHorizontal.START -> DivGallery.CrossContentAlignment.START
        DivAlignmentHorizontal.CENTER -> DivGallery.CrossContentAlignment.CENTER
        DivAlignmentHorizontal.RIGHT,
        DivAlignmentHorizontal.END -> DivGallery.CrossContentAlignment.END
    }
