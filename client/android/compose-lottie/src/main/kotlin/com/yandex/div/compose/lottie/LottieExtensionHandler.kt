package com.yandex.div.compose.lottie

import android.content.Context
import android.util.LruCache
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieResult
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.images.asImageBase
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.internal.extensions.lottie.LottieExtensionParams
import com.yandex.div.internal.extensions.lottie.LottieExtensionParamsParser
import com.yandex.div.internal.extensions.lottie.LottieRepeatMode
import com.yandex.div.internal.extensions.lottie.parseLottieUrl
import com.yandex.div.lottie.CompositeDivLottieResourceLoader
import com.yandex.div.lottie.DivLottieAssetResourceLoader
import com.yandex.div.lottie.DivLottieRawResResourceLoader
import com.yandex.div.lottie.DivLottieResourceLoader
import com.yandex.div.lottie.loadComposition
import org.json.JSONObject
import java.util.WeakHashMap

/**
 * [DivExtensionHandler] that allows to use Lottie animations inside
 * [com.yandex.div.compose.DivView]s.
 */
class LottieExtensionHandler private constructor(
    private val resourceLoaderFactory: (Context) -> DivLottieResourceLoader,
    private val networkCache: LottieNetworkCache,
    private val preloadResourceLoader: DivLottieResourceLoader?,
) : DivExtensionHandler {

    private val resourceLoaders = WeakHashMap<Context, DivLottieResourceLoader>()
    private val compositionCache = LruCache<String, LottieComposition>(COMPOSITION_CACHE_SIZE)

    /** Retains the JVM no-argument constructor used by Java and compiled clients. */
    constructor() : this(assetMapper = { null })

    /**
     * Creates the preferred Lottie extension handler with a host-provided [resourceLoader].
     * URLs not claimed by the loader keep the existing cache and network behavior.
     * Use [CompositeDivLottieResourceLoader] to combine custom, asset and raw-resource loaders.
     */
    constructor(
        resourceLoader: DivLottieResourceLoader,
        networkCache: LottieNetworkCache = LottieNetworkCache.STUB,
    ) : this(
        { resourceLoader },
        networkCache,
        resourceLoader,
    )

    /**
     * Creates a backward-compatible handler using the legacy asset and raw-resource mappers.
     * Local loaders are created with the rendering context; preloading retains the legacy network cache path.
     */
    constructor(
        assetMapper: (String) -> String? = { null },
        rawResMapper: (String) -> Int? = { null },
        networkCache: LottieNetworkCache = LottieNetworkCache.STUB,
    ) : this(
        { context ->
            CompositeDivLottieResourceLoader(
                DivLottieAssetResourceLoader(context, assetMapper),
                DivLottieRawResResourceLoader(context, rawResMapper),
            )
        },
        networkCache,
        null,
    )

    @Composable
    override fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        val image = environment.data.asImageBase()
        if (image == null) {
            environment.reportError("Lottie extension must be applied to image or gif element")
            return
        }

        val context = LocalContext.current
        val loader = remember(context) { getResourceLoader(context) }
        val paramsJson = environment.extension.params
        val params = remember(paramsJson, loader) { parseParams(paramsJson, environment, loader) } ?: return
        val composition = rememberComposition(params.data, environment, loader)
        LottieAnimation(
            modifier = modifier,
            alignment = image.observedAlignment(),
            composition = composition,
            contentScale = image.observedContentScale(),
            isPlaying = params.isPlaying.observedValue(true) && environment.animationsEnabled,
            iterations = params.iterations,
            restartOnPlay = false,
            reverseOnRepeat = params.repeatMode == LottieRepeatMode.REVERSE,
            safeMode = params.safeMode
        )
    }

    override suspend fun preload(environment: DivExtensionEnvironment) {
        val url = environment.extension.params?.let {
            parseLottieUrl(
                it,
                environment.expressionResolver,
                environment.reporter::reportError,
            )
        } ?: return
        preloadResourceLoader?.let { loader ->
            val urlString = url.toString()
            if (loader.canLoad(urlString)) {
                getCachedComposition(urlString)?.let { return }
                val result = cacheComposition(urlString, loader.loadComposition(urlString))
                result.exception?.let { environment.reporter.reportError(it.loadErrorMessage(urlString)) }
                return
            }
        }
        networkCache.save(url.toString())
    }

    @Composable
    private fun rememberComposition(
        data: LottieData,
        environment: DivExtensionEnvironment,
        loader: DivLottieResourceLoader,
    ): LottieComposition? {
        val useResourceLoader = remember(data, loader) {
            data is LottieData.Url && loader.canLoad(data.url)
        }
        if (useResourceLoader) {
            val url = (data as LottieData.Url).url
            getCachedComposition(url)?.let { return it }
            return key(url, loader) {
                val result by produceState<LottieResult<LottieComposition>?>(null) {
                    value = cacheComposition(url, loader.loadComposition(url))
                }
                val error = result?.exception
                LaunchedEffect(url, error) {
                    error?.let { environment.reporter.reportError(it.loadErrorMessage(url)) }
                }
                result?.value
            }
        }

        val compositionSpec = remember(data, networkCache) {
            data.toCompositionSpec(networkCache)
        }
        val composition by rememberLottieComposition(compositionSpec)
        return composition
    }

    private fun parseParams(
        params: JSONObject?,
        environment: DivExtensionEnvironment,
        loader: DivLottieResourceLoader,
    ): LottieExtensionParams? {
        val reporter = environment.reporter
        if (params == null) {
            reporter.reportError("Params required for Lottie extension handler")
            return null
        }

        val parser = createParamsParser(loader) { reporter.reportError(it) }
        return parser.parse(params, environment.expressionResolver)
    }

    private fun createParamsParser(
        loader: DivLottieResourceLoader?,
        reportError: (String) -> Unit,
    ): LottieExtensionParamsParser {
        return LottieExtensionParamsParser(
            assetMapper = { null },
            rawResMapper = { null },
            reportError = reportError,
            urlFilter = { url -> loader?.canLoad(url) == true },
        )
    }

    @Synchronized
    private fun getResourceLoader(context: Context): DivLottieResourceLoader {
        return resourceLoaders.getOrPut(context) { resourceLoaderFactory(context) }
    }

    @Synchronized
    private fun getCachedComposition(url: String): LottieComposition? = compositionCache[url]

    @Synchronized
    private fun cacheComposition(
        url: String,
        result: LottieResult<LottieComposition>,
    ): LottieResult<LottieComposition> {
        result.value?.let { compositionCache.put(url, it) }
        return result
    }
}

private fun Throwable.loadErrorMessage(url: String): String =
    "Failed to load Lottie composition from $url: ${message ?: this::class.java.simpleName}"

private val LottieExtensionParams.iterations: Int
    get() {
        return when {
            repeatCount == -1 -> LottieConstants.IterateForever
            repeatCount <= 0 -> 1
            else -> repeatCount
        }
    }

private fun LottieData.toCompositionSpec(cache: LottieNetworkCache): LottieCompositionSpec {
    return when (this) {
        is LottieData.Asset -> LottieCompositionSpec.Asset(assetName)
        is LottieData.Json -> LottieCompositionSpec.JsonString(json)
        is LottieData.RawRes -> LottieCompositionSpec.RawRes(id)
        is LottieData.Url -> cache.get(url)?.let { LottieCompositionSpec.JsonString(it) }
            ?: LottieCompositionSpec.Url(url)
    }
}

private const val COMPOSITION_CACHE_SIZE = 20
