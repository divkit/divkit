package com.yandex.div.lottie

import android.content.Context
import android.content.res.Configuration
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Loads `asset://` compositions resolved by [assetMapper].
 *
 * Claims a URL only when its scheme matches and [assetMapper] resolves it.
 * Resolved asset names are cached for reuse by [load].
 *
 * @param dispatcher dispatcher used for asset reading and JSON parsing; defaults to [Dispatchers.IO].
 */
public class DivLottieAssetResourceLoader(
    context: Context,
    private val assetMapper: (String) -> String?,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DivLottieResourceLoader {

    private val context = context.applicationContext.createConfigurationContext(
        Configuration(context.resources.configuration)
    )
    private val resolver = SchemeDivLottieResourceResolver(
        scheme = ASSET_SCHEME,
        resourceDescription = "asset file name",
        mapper = assetMapper,
    )

    public constructor(
        context: Context,
        assetMapper: (String) -> String?,
    ) : this(context, assetMapper, Dispatchers.IO)

    override fun canLoad(url: String): Boolean = resolver.canLoad(url)

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        val assetFile = resolver.resolve(url).getOrElse { return LottieResult(it) }
        return withContext(dispatcher) {
            LottieCompositionFactory.fromAssetSync(context, assetFile)
        }
    }
}

private const val ASSET_SCHEME = "asset"
