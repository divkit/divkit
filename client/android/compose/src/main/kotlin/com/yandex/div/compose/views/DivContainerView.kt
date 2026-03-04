package com.yandex.div.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div2.Div
import com.yandex.div2.DivContainer

@Composable
internal fun DivContainerView(
    modifier: Modifier,
    data: DivContainer
) {
    when (data.orientation.evaluate()) {
        DivContainer.Orientation.HORIZONTAL ->
            HorizontalView(
                modifier = modifier,
                items = data.items.orEmpty()
            )

        DivContainer.Orientation.VERTICAL ->
            VerticalView(
                modifier = modifier,
                items = data.items.orEmpty()
            )

        DivContainer.Orientation.OVERLAP ->
            OverlapView(
                modifier = modifier,
                items = data.items.orEmpty()
            )
    }
}

@Composable
private fun HorizontalView(
    modifier: Modifier,
    items: List<Div>
) {
    Row(modifier = modifier) {
        items.forEach {
            DivBlockView(data = it)
        }
    }
}

@Composable
private fun VerticalView(
    modifier: Modifier,
    items: List<Div>
) {
    Column(modifier = modifier) {
        items.forEach {
            DivBlockView(data = it)
        }
    }
}

@Composable
private fun OverlapView(
    modifier: Modifier,
    items: List<Div>
) {
    Box(modifier = modifier) {
        items.forEach {
            DivBlockView(data = it)
        }
    }
}
