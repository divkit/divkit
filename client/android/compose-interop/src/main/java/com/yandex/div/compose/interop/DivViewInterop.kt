@file:Suppress("FunctionName")

package com.yandex.div.compose.interop

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.view2.Div2View
import com.yandex.div2.DivData

private val noopReset: (View) -> Unit = {}

@Composable
public fun DivView(
    data: DivData,
    tag: DivDataTag,
    modifier: Modifier = Modifier
) {
    val context: Div2Context = LocalDivContext.current
    AndroidView(
        modifier = modifier,
        factory = {
            Div2View(context)
        },
        update = { view ->
            view.setData(data, tag)
        },
        onReset = noopReset,
        onRelease = { view ->
            view.cleanup()
        }
    )
}
