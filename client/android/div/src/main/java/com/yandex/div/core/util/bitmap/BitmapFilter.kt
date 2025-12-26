package com.yandex.div.core.util.bitmap

internal sealed class BitmapFilter {
    data class Blur(val radius: Int): BitmapFilter()
    class RtlMirror: BitmapFilter()
}
