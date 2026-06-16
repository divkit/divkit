package com.yandex.div.compose.lottie

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.images.asImageBase
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.internal.extensions.lottie.LottieExtensionParams
import com.yandex.div.internal.extensions.lottie.LottieExtensionParamsParser
import com.yandex.div.internal.extensions.lottie.LottieRepeatMode
import org.json.JSONObject

/**
 * [DivExtensionHandler] that allows to use Lottie animations inside
 * [com.yandex.div.compose.DivView]s.
 */
@ExperimentalApi
class LottieExtensionHandler(
    private val assetMapper: (String) -> String? = { null },
    private val rawResMapper: (String) -> Int? = { null }
) : DivExtensionHandler {

    @Composable
    override fun Content(
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    ) {
        val image = environment.data.asImageBase()
        if (image == null) {
            environment.reportError("Lottie extension must be applied to image or gif element")
            return
        }

        val paramsJson = environment.extension.params
        val params = remember(paramsJson) { parseParams(paramsJson, environment) } ?: return
        val composition by rememberLottieComposition(params.data.toCompositionSpec())
        LottieAnimation(
            modifier = environment.modifier,
            alignment = image.observedAlignment(),
            composition = composition,
            contentScale = image.observedContentScale(),
            isPlaying = params.isPlaying.observedValue(true),
            iterations = params.iterations,
            restartOnPlay = false,
            reverseOnRepeat = params.repeatMode == LottieRepeatMode.REVERSE
        )
    }

    private fun parseParams(
        params: JSONObject?,
        environment: DivExtensionEnvironment
    ): LottieExtensionParams? {
        val reporter = environment.reporter
        if (params == null) {
            reporter.reportError("Params required for Lottie extension handler")
            return null
        }

        val parser = LottieExtensionParamsParser(
            assetMapper = assetMapper,
            rawResMapper = rawResMapper,
            reportError = { reporter.reportError(it) }
        )
        return parser.parse(params, environment.expressionResolver)
    }
}

private val LottieExtensionParams.iterations: Int
    get() {
        return when {
            repeatCount == -1 -> LottieConstants.IterateForever
            repeatCount <= 0 -> 1
            else -> repeatCount
        }
    }

private fun LottieData.toCompositionSpec(): LottieCompositionSpec {
    return when (this) {
        is LottieData.Asset -> LottieCompositionSpec.Asset(assetName)
        is LottieData.Json -> LottieCompositionSpec.JsonString(json)
        is LottieData.RawRes -> LottieCompositionSpec.RawRes(id)
        is LottieData.Url -> LottieCompositionSpec.Url(url)
    }
}
