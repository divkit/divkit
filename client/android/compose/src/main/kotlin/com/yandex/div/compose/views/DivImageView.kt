package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import com.yandex.div2.DivImage

@Composable
internal fun DivImageView(
    modifier: Modifier,
    data: DivImage
) {
    AsyncImage(
        modifier = modifier,
        model = data.imageUrl.evaluate(),
        contentDescription = null
    )
}
