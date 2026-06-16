package com.yandex.div.compose.images

import androidx.compose.runtime.mutableStateSetOf
import com.yandex.div.compose.dagger.DivViewScope
import com.yandex.div2.DivBase
import javax.inject.Inject

/**
 * Snapshot-backed storage that tracks per-image loading state inside a single
 * `DivView`. Each image element in the tree (a `DivImage` or `DivGifImage`)
 * reports whether its content has finished loading, keyed by its [DivBase] instance.
 *
 * Reads happen during composition via [isLoaded] and are reactive: when an entry
 * flips from `false` to `true`, any composable that previously read it will be
 * invalidated and recomposed.
 */
@DivViewScope
internal class ImageStateStorage @Inject constructor() {
    private val loadedElements = mutableStateSetOf<DivBase>()

    fun isLoaded(data: DivBase): Boolean {
        return loadedElements.contains(data)
    }

    fun setIsLoaded(data: DivBase, isLoaded: Boolean) {
        if (isLoaded) {
            loadedElements.add(data)
        } else {
            loadedElements.remove(data)
        }
    }
}
