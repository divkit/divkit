package com.yandex.div.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun DivSeparatorLayout(
    modifier: Modifier,
    color: Color,
    vertical: Boolean,
) {
    Box(modifier = modifier) {
        val lineSize = if (vertical) {
            Modifier.width(1.dp).fillMaxHeight()
        } else {
            Modifier.fillMaxWidth().height(1.dp)
        }
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .then(lineSize)
                .background(color)
        )
    }
}
