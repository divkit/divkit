package com.yandex.div.core.player

import com.yandex.div2.DivVideoScale

interface DivVideoAttachable {
    fun attach(player: DivPlayer) = Unit

    fun detach() = Unit

    fun getAttachedPlayer(): DivPlayer? = null

    fun setVisibleOnScreen(visible: Boolean) = Unit

    fun setScale(videoScale: DivVideoScale) = Unit
}
