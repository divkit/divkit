package com.yandex.div.compose.images

import androidx.compose.runtime.mutableStateMapOf
import coil3.request.ImageRequest
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
 *
 * Also holds the [ImageAppearanceAnimation] of each `DivImage` with `appearance_animation`,
 * shared by the element modifiers and the image content.
 */
@DivViewScope
internal class ImageStateStorage @Inject constructor() {
    private val loadedElements = mutableStateMapOf<DivBase, ImageRequest>()
    private val appearanceAnimations = mutableMapOf<DivBase, ImageAppearanceAnimation>()

    fun isLoaded(data: DivBase): Boolean {
        return loadedElements.containsKey(data)
    }

    fun isLoaded(data: DivBase, owner: ImageRequest): Boolean {
        return loadedElements[data] === owner
    }

    fun markLoaded(data: DivBase, owner: ImageRequest) {
        loadedElements[data] = owner
    }

    fun reset(data: DivBase, owner: ImageRequest) {
        if (loadedElements[data] === owner) {
            loadedElements.remove(data)
        }
    }

    fun getAppearanceAnimation(data: DivBase): ImageAppearanceAnimation {
        return appearanceAnimations.getOrPut(data) { ImageAppearanceAnimation() }
    }
}
