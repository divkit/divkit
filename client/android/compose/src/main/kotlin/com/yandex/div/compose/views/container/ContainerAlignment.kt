package com.yandex.div.compose.views.container

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
import com.yandex.div.compose.utils.toDp
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical

internal fun DivContentAlignmentHorizontal.toHorizontalArrangement(itemSpacing: Long): Arrangement.Horizontal {
    val spacing = itemSpacing.toDp()
    return when (this) {
        DivContentAlignmentHorizontal.CENTER ->
            if (itemSpacing > 0) Arrangement.spacedBy(spacing, Alignment.CenterHorizontally)
            else Arrangement.Center
        DivContentAlignmentHorizontal.RIGHT, DivContentAlignmentHorizontal.END ->
            if (itemSpacing > 0) Arrangement.spacedBy(spacing, Alignment.End)
            else Arrangement.End
        DivContentAlignmentHorizontal.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentHorizontal.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentHorizontal.SPACE_EVENLY -> Arrangement.SpaceEvenly
        else -> if (itemSpacing > 0) Arrangement.spacedBy(spacing) else Arrangement.Start
    }
}

internal fun DivContentAlignmentVertical.toVerticalArrangement(itemSpacing: Long): Arrangement.Vertical {
    val spacing = itemSpacing.toDp()
    return when (this) {
        DivContentAlignmentVertical.CENTER ->
            if (itemSpacing > 0) Arrangement.spacedBy(spacing, Alignment.CenterVertically)
            else Arrangement.Center
        DivContentAlignmentVertical.BOTTOM ->
            if (itemSpacing > 0) Arrangement.spacedBy(spacing, Alignment.Bottom)
            else Arrangement.Bottom
        DivContentAlignmentVertical.SPACE_BETWEEN -> Arrangement.SpaceBetween
        DivContentAlignmentVertical.SPACE_AROUND -> Arrangement.SpaceAround
        DivContentAlignmentVertical.SPACE_EVENLY -> Arrangement.SpaceEvenly
        else -> if (itemSpacing > 0) Arrangement.spacedBy(spacing) else Arrangement.Top
    }
}

internal fun DivContentAlignmentVertical.toCrossAxisVerticalAlignment(): Alignment.Vertical =
    when (this) {
        DivContentAlignmentVertical.CENTER -> Alignment.CenterVertically
        DivContentAlignmentVertical.BOTTOM -> Alignment.Bottom
        else -> Alignment.Top
    }

internal fun DivContentAlignmentHorizontal.toCrossAxisHorizontalAlignment(): Alignment.Horizontal =
    when (this) {
        DivContentAlignmentHorizontal.CENTER -> Alignment.CenterHorizontally
        DivContentAlignmentHorizontal.RIGHT, DivContentAlignmentHorizontal.END -> Alignment.End
        else -> Alignment.Start
    }
