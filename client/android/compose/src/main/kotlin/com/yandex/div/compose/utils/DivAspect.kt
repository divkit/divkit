package com.yandex.div.compose.utils

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
