package com.yandex.div.compose.utils.scroll

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
internal fun OrientedLazyList(
    isHorizontal: Boolean,
    modifier: Modifier,
    listState: LazyListState,
    contentPadding: PaddingValues,
    itemSpacing: Dp,
    crossAxisAlignment: CrossAxisAlignment,
    flingBehavior: FlingBehavior,
    content: LazyListScope.() -> Unit,
) {
    if (isHorizontal) {
        LazyRow(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            verticalAlignment = crossAxisAlignment.toVerticalAlignment(),
            flingBehavior = flingBehavior,
            content = content,
        )
    } else {
        LazyColumn(
            modifier = modifier,
            state = listState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
            horizontalAlignment = crossAxisAlignment.toHorizontalAlignment(),
            flingBehavior = flingBehavior,
            content = content,
        )
    }
}
