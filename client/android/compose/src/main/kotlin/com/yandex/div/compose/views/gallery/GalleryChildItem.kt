package com.yandex.div.compose.views.gallery

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.observedValue
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div2.Div
import com.yandex.div2.DivGallery

@Composable
internal fun GalleryChildItem(
    childDiv: Div,
    isHorizontal: Boolean,
    crossContentAlignment: DivGallery.CrossContentAlignment,
) {
    val childBase = childDiv.value()

    val childCrossAlignment = if (isHorizontal) {
        childBase.alignmentVertical?.observedValue()?.toGalleryCrossAlignment()
    } else {
        childBase.alignmentHorizontal?.observedValue()?.toGalleryCrossAlignment()
    } ?: crossContentAlignment

    val modifier = if (isHorizontal) {
        Modifier.fillMaxHeight()
    } else {
        Modifier.fillMaxWidth()
    }

    Box(
        modifier = modifier,
        contentAlignment = childCrossAlignment.toBoxAlignment(isHorizontal),
    ) {
        DivBlockView(childDiv)
    }
}
