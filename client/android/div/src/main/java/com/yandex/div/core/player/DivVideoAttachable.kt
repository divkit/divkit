package com.yandex.div.core.player

interface DivVideoAttachable {
    fun attach(player: DivPlayer) = Unit

    fun detach() = Unit

    fun getAttachedPlayer(): DivPlayer? = null
}
