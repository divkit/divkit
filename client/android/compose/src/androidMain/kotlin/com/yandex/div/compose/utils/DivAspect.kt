package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import com.yandex.div.compose.expressions.observedFloatValue
import com.yandex.div2.DivAspect
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivImage
import com.yandex.div2.DivVideo

internal val DivBase.aspect: DivAspect?
    get() = when (this) {
        is DivContainer -> aspect
        is DivImage -> aspect
        is DivGifImage -> aspect
        is DivVideo -> aspect
        else -> null
    }

@Composable
internal fun DivBase.observedAspectRatio(): Float? {
    val aspectRatio = aspect?.ratio?.observedFloatValue() ?: return null
    return if (aspectRatio > 0f) aspectRatio else null
}
