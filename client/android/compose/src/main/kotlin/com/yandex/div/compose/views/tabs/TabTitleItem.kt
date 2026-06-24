package com.yandex.div.compose.views.tabs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import coil3.compose.rememberAsyncImagePainter
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.images.ImageRequestParams
import com.yandex.div.compose.images.rememberImageRequest
import com.yandex.div.compose.utils.observeInsets
import com.yandex.div.compose.utils.observedValue
import com.yandex.div2.DivTabs

@Composable
internal fun TabTitleItem(
    index: Int,
    item: DivTabs.Item,
    isSelected: Boolean,
    background: Color,
    style: DivTabs.TabTitleStyle,
    titleDelimiter: DivTabs.TabTitleDelimiter?,
    itemSpacing: Dp,
    onClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (index > 0 && titleDelimiter != null) {
            TabTitleDelimiter(titleDelimiter)
        }
        Box(
            modifier = Modifier
                .clip(style.observeTabShape())
                .background(background)
                .clickable(interactionSource = null, indication = null, onClick = onClick)
                .padding(style.paddings.observeInsets()),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(text = item.title.observedValue(), style = style.observeTextStyle(isSelected))
        }
    }
}

@Composable
private fun TabTitleDelimiter(delimiter: DivTabs.TabTitleDelimiter) {
    val request = rememberImageRequest(
        ImageRequestParams(
            data = delimiter.imageUrl.observedValue(),
            transformations = emptyList(),
        )
    )
    val painter = rememberAsyncImagePainter(
        model = request,
        imageLoader = divContext.component.imageLoader
    )
    Image(
        modifier = Modifier.size(
            width = delimiter.width.observedValue(),
            height = delimiter.height.observedValue()
        ),
        painter = painter,
        contentDescription = null,
    )
}
