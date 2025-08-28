package com.yandex.div.multiplatform

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.picasso.PicassoDivImageLoader

data class DivKitEnvironment(
    val imageLoaderFactory: (Context) -> DivImageLoader = { ctx -> PicassoDivImageLoader(ctx) },
    val lifecycleOwner: LifecycleOwner? = null
)

object DivKitAndroidEnvironment {
    internal var divKitEnvironment: DivKitEnvironment = DivKitEnvironment()

    fun set(environment: DivKitEnvironment) {
        divKitEnvironment = environment
    }
}
