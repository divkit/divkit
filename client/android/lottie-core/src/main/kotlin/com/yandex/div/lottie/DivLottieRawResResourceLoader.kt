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
 * Loads `res://` compositions resolved by [rawResMapper].
 *
 * Claims a URL only when its scheme matches and [rawResMapper] resolves it.
 * Resolved resource ids are cached for reuse by [load].
 *
 * @param dispatcher dispatcher used for resource reading and JSON parsing; defaults to [Dispatchers.IO].
 */
public class DivLottieRawResResourceLoader(
    context: Context,
    private val rawResMapper: (String) -> Int?,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : DivLottieResourceLoader {

    private val context = context.applicationContext.createConfigurationContext(
        Configuration(context.resources.configuration)
    )
    private val resolver = SchemeDivLottieResourceResolver(
        scheme = RAW_RES_SCHEME,
        resourceDescription = "resource id",
        mapper = rawResMapper,
    )

    public constructor(
        context: Context,
        rawResMapper: (String) -> Int?,
    ) : this(context, rawResMapper, Dispatchers.IO)

    override fun canLoad(url: String): Boolean = resolver.canLoad(url)

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        val rawRes = resolver.resolve(url).getOrElse { return LottieResult(it) }
        return withContext(dispatcher) {
            LottieCompositionFactory.fromRawResSync(context, rawRes)
        }
    }
}

private const val RAW_RES_SCHEME = "res"
