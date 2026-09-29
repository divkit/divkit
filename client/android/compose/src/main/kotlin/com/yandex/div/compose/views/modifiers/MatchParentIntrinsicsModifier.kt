package com.yandex.div.compose.views.modifiers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.utils.isMatchParent
import com.yandex.div.compose.utils.observedAspectRatio
import com.yandex.div2.DivBase

/**
 * Excludes match-parent content and size bounds from intrinsic sizing, matching the View grid's first pass.
 * Apply after margins and before size.
 */
@Composable
internal fun Modifier.suppressMatchParentIntrinsics(data: DivBase): Modifier {
    val suppressWidth = data.width.isMatchParent
    val suppressHeight = data.height.isMatchParent && data.observedAspectRatio() == null
    if (!suppressWidth && !suppressHeight) return this
    val intrinsicModifier = remember(suppressWidth, suppressHeight) {
        Modifier.fixedIntrinsics(
            width = if (suppressWidth) 0.dp else null,
            height = if (suppressHeight) 0.dp else null,
        )
    }
    return then(intrinsicModifier)
}
