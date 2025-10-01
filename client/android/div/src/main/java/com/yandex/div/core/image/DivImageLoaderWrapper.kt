package com.yandex.div.core.image

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.widget.ImageView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.yandex.div.core.Div2Context
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.images.DivImageDownloadCallback
import com.yandex.div.core.images.DivImageLoader
import com.yandex.div.core.images.LoadReference
import com.yandex.div.internal.util.makeIf
import com.yandex.div.svg.SvgDivImageLoader
import com.yandex.div.webp.WebPDivImageLoader
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

/**
 * A wrapper over DivImageLoader.
 * Applies modifiers to image URL.
 * Replaces chosen image loader by SVG Image Loader if image loader doesn't support svg images.
 * Replace chosen loader by Webp Image Loader to load webp images.
 */
@DivScope
@InternalApi
class DivImageLoaderWrapper @Inject constructor(
    private val providedImageLoader: DivImageLoader,
    divContext: Context,
) : DivImageLoader {
    private val modifiers: List<DivImageUrlModifier> = listOf(
        DivImageAssetUrlModifier(),
    )

    private val lifecycleOwnerScope: CoroutineScope? = divContext.findLifecycleOwner()?.lifecycleScope

    private val svgImageLoader: SvgDivImageLoader? =
        makeIf(!providedImageLoader.hasSvgSupport()) { SvgDivImageLoader(divContext, lifecycleOwnerScope) }

    private val webpImageLoader: WebPDivImageLoader? by lazy(LazyThreadSafetyMode.NONE) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && !providedImageLoader.hasWebPSupport()) {
            WebPDivImageLoader(divContext, lifecycleOwnerScope)
        } else {
            null
        }
    }

    override fun loadImage(imageUrl: String, callback: DivImageDownloadCallback): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadImage(modifiedUrl, callback)
    }

    override fun loadImage(imageUrl: String, imageView: ImageView): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadImage(modifiedUrl, imageView)
    }

    override fun loadImageBytes(
        imageUrl: String,
        callback: DivImageDownloadCallback
    ): LoadReference {
        val modifiedUrl = getModifiedUrl(imageUrl)
        return getProperLoader(modifiedUrl).loadImageBytes(modifiedUrl, callback)
    }

    private fun getModifiedUrl(initialUrl: String): String {
        var url = initialUrl
        modifiers.forEach { modifier ->
            url = modifier.modifyImageUrl(url)
        }
        return url
    }

    private fun getProperLoader(imageUrl: String): DivImageLoader {
        if (isWebp(imageUrl)) webpImageLoader?.let { return it }
        if (isSvg(imageUrl) && svgImageLoader != null) return svgImageLoader
        return providedImageLoader
    }

    private fun checkFileExtension(imageUrl: String, ext: String): Boolean {
        val queryStartIndex = imageUrl.indexOf('?')
        val end = if (queryStartIndex < 0) imageUrl.length else queryStartIndex
        return imageUrl.substring(0, end).endsWith(".${ext}", ignoreCase = true)
    }
    private fun isSvg(imageUrl: String)  = checkFileExtension(imageUrl, "svg")
    private fun isWebp(imageUrl: String) = checkFileExtension(imageUrl, "webp")

    private fun Context.findLifecycleOwner(): LifecycleOwner? {
        return generateSequence(this) { (it as? ContextWrapper)?.baseContext }
            .mapNotNull {
                when (it) {
                    is Div2Context -> it.lifecycleOwner
                    is LifecycleOwner -> it
                    else -> null
                }
            }
            .firstOrNull()
    }
}
