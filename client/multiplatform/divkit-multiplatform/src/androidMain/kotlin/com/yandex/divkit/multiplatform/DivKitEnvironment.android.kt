package com.yandex.divkit.multiplatform

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.picasso.PicassoDivImageLoader

class DivKitEnvironment private constructor(
    val baseContext: Context?,
    val imageLoaderFactory: (Context) -> DivImageLoader = { ctx -> PicassoDivImageLoader(ctx) },
    val lifecycleOwner: LifecycleOwner? = null
) {

    @Deprecated("Use DivKitEnvironment.Builder(Context).build() instead")
    constructor(
        imageLoaderFactory: (Context) -> DivImageLoader = { ctx -> PicassoDivImageLoader(ctx) },
        lifecycleOwner: LifecycleOwner? = null
    ) : this(null, imageLoaderFactory, lifecycleOwner)

    class Builder(
        private val baseContext: Context
    ) {

        private var imageLoaderFactory: ((Context) -> DivImageLoader)? = null
        private var lifecycleOwner: LifecycleOwner? = null

        fun imageLoaderFactory(factory: (Context) -> DivImageLoader): Builder {
            imageLoaderFactory = factory
            return this
        }

        fun lifecycleOwner(owner: LifecycleOwner): Builder {
            lifecycleOwner = owner
            return this
        }

        fun build(): DivKitEnvironment {
            return DivKitEnvironment(
                baseContext = baseContext,
                imageLoaderFactory = imageLoaderFactory ?: { ctx -> PicassoDivImageLoader(ctx) },
                lifecycleOwner = lifecycleOwner
            )
        }
    }
}

@Deprecated("DivKitAndroidEnvironment.set(DivKitEnvironment) can lead to memory leaks. Use inject(DivKitEnvironment) instead.")
object DivKitAndroidEnvironment {

    internal var divKitEnvironment: DivKitEnvironment = DivKitEnvironment()

    fun set(environment: DivKitEnvironment) {
        divKitEnvironment = environment
    }
}

val LocalDivKitEnvironment: ProvidableCompositionLocal<DivKitEnvironment> = staticCompositionLocalOf {
    noLocalDivKitEnvironment()
}

private fun noLocalDivKitEnvironment(): Nothing {
    error("LocalDivKitEnvironment not found. Make sure you called inject(DivKitEnvironment).")
}

@Composable
fun inject(environment: DivKitEnvironment, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDivKitEnvironment provides environment, content)
}
