package com.yandex.div.internal.lottie

import android.net.Uri
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.parser.ANY_TO_URI
import com.yandex.div.internal.parser.JsonExpressionParser
import com.yandex.div.internal.parser.NUMBER_TO_INT
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TYPE_HELPER_URI
import com.yandex.div.internal.util.mapNotNull
import com.yandex.div.json.expressions.ExpressionResolver
import org.json.JSONObject

@InternalApi
class LottieExtensionParamsParser(
    private val assetMapper: (String) -> String?,
    private val rawResMapper: (String) -> Int?,
    private val reportError: (String) -> Unit
) {
    private val parsingEnvironment = DivParsingEnvironment(
        logger = { e -> reportError(e.message ?: e.toString()) }
    )

    fun parse(
        json: JSONObject,
        expressionResolver: ExpressionResolver
    ): LottieExtensionParams? {
        val data = parseData(json, expressionResolver) ?: return null
        val isPlaying = JsonExpressionParser.readOptionalExpression(
            parsingEnvironment,
            json,
            "is_playing",
            TYPE_HELPER_BOOLEAN,
            ANY_TO_BOOLEAN
        )
        return LottieExtensionParams(
            data = data,
            isPlaying = isPlaying,
            repeatCount = getRepeatCount(json, expressionResolver),
            repeatMode = getRepeatMode(json, expressionResolver),
            repeats = getRepeats(json, expressionResolver)
        )
    }

    fun parseUrl(json: JSONObject, expressionResolver: ExpressionResolver): Uri? {
        return JsonExpressionParser.readOptionalExpression(
            parsingEnvironment,
            json,
            "lottie_url",
            TYPE_HELPER_URI,
            ANY_TO_URI
        )?.evaluate(expressionResolver)
    }

    private fun parseData(
        json: JSONObject,
        expressionResolver: ExpressionResolver
    ): LottieData? {
        val url = parseUrl(json, expressionResolver)
        if (url == null) {
            return json.optJSONObject("lottie_json")?.let { LottieData.Json(it.toString()) }
        }


        when (url.scheme) {
            "asset" -> {
                val urlString = url.toString()
                val assetFileName = assetMapper(urlString)
                if (assetFileName == null) {
                    reportError("Failed to resolve asset file name for: $urlString")
                    return null
                }
                return LottieData.Asset(assetFileName)
            }

            "divkit-asset" -> {
                var assetName = url.toString().removePrefix("divkit-asset://")
                if (!assetName.startsWith("divkit/")) {
                    assetName = "divkit/${assetName.removePrefix("/")}"
                }
                return LottieData.Asset(assetName)
            }

            "res" -> {
                val urlString = url.toString()
                val rawRes = rawResMapper(urlString)
                if (rawRes == null) {
                    reportError("Failed to resolve resource id for: $urlString")
                    return null
                }
                return LottieData.RawRes(id = rawRes, url = urlString)
            }
        }

        return LottieData.Url(url.toString())
    }

    private fun getRepeats(
        json: JSONObject,
        expressionResolver: ExpressionResolver,
    ): List<LottieRepeat> {
        return json.optJSONArray("repeats")
            ?.mapNotNull { it as? JSONObject }
            ?.map {
                LottieRepeat(
                    count = getRepeatCount(it, expressionResolver),
                    mode = getRepeatMode(it, expressionResolver),
                    minFrame = getMinFrame(it, expressionResolver),
                    maxFrame = getMaxFrame(it, expressionResolver)
                )
            }
            ?: emptyList()
    }

    private fun getRepeatCount(
        json: JSONObject,
        expressionResolver: ExpressionResolver
    ): Int {
        return JsonExpressionParser.readOptionalExpression(
            parsingEnvironment,
            json,
            "repeat_count",
            TYPE_HELPER_INT,
            NUMBER_TO_INT
        )?.evaluate(expressionResolver)?.toInt() ?: 1
    }

    private fun getRepeatMode(
        json: JSONObject,
        expressionResolver: ExpressionResolver
    ): LottieRepeatMode {
        val mode = JsonExpressionParser.readOptionalExpression(
            parsingEnvironment,
            json,
            "repeat_mode",
            TYPE_HELPER_STRING
        )?.evaluate(expressionResolver)
        return if (mode == "reverse") LottieRepeatMode.REVERSE else LottieRepeatMode.RESTART
    }

    private fun getMinFrame(
        json: JSONObject,
        expressionResolver: ExpressionResolver
    ): Int? {
        return JsonExpressionParser.readOptionalExpression(
            parsingEnvironment,
            json,
            "min_frame",
            TYPE_HELPER_INT,
            NUMBER_TO_INT
        )?.evaluate(expressionResolver)?.toInt()
    }

    private fun getMaxFrame(
        json: JSONObject,
        expressionResolver: ExpressionResolver
    ): Int? {
        return JsonExpressionParser.readOptionalExpression(
            parsingEnvironment,
            json,
            "max_frame",
            TYPE_HELPER_INT,
            NUMBER_TO_INT
        )?.evaluate(expressionResolver)?.toInt()
    }
}
