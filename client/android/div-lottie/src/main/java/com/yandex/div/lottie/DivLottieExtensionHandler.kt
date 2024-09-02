package com.yandex.div.lottie

import android.view.View
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.parser.JsonParser
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.util.mapNotNull
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivGifImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

private const val EXTENSION_ID = "lottie"
private const val LOTTIE_PARAM_URL = "lottie_url"
private const val LOTTIE_PARAM_JSON = "lottie_json"
private const val LOTTIE_PARAM_REPEATS = "repeats"
private const val LOTTIE_PARAM_REPEAT_MODE = "repeat_mode"
private const val LOTTIE_PARAM_REPEAT_MODE_REVERSE = "reverse"
private const val LOTTIE_PARAM_REPEAT_MODE_RESTART = "restart"
private const val LOTTIE_PARAM_REPEAT_COUNT = "repeat_count"
private const val LOTTIE_PARAM_MIN_FRAME = "min_frame"
private const val LOTTIE_PARAM_MAX_FRAME = "max_frame"
private const val LOTTIE_PARAM_PLAYING = "is_playing"

/**
 * An extension handler for [EXTENSION_ID] div gif image. Important thing is you can use this extension
 * over existing [DivGifImage] divs and not worry about backward compatibility, as this extension inherit all
 * [DivGifImage] attributes and use [DivGifImage.gifUrl] as fallback.
 */
open class DivLottieExtensionHandler(
    private val rawResProvider: DivLottieRawResProvider = DivLottieRawResProvider.STUB,
    private val logger: DivLottieLogger = DivLottieLogger.STUB,
    cache: DivLottieNetworkCache = DivLottieNetworkCache.STUB,
) : DivExtensionHandler, ExpressionSubscriber {

    private val repo = DivLottieCompositionRepository(rawResProvider, cache)

    override val subscriptions: MutableList<Disposable> = mutableListOf()

    override fun preprocess(div: DivBase, expressionResolver: ExpressionResolver) {
        val lottieUrl = div.extensions
            ?.find { extension -> extension.id == EXTENSION_ID }
            ?.params?.lottieUrl ?: return
        repo.preloadLottieComposition(lottieUrl)
    }

    override fun beforeBindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        val divGifImageView = view as? LoadableImageView ?: return
        if (divGifImageView.delegate !is LottieController) {
            divGifImageView.delegate = LottieController(divGifImageView).apply {
                enableMergePathsForKitKatAndAbove(true)
                setImageAssetsFolder(rawResProvider.provideAssetFolder())
            }
        }
    }

    override fun matches(div: DivBase): Boolean {
        if (div !is DivGifImage) {
            return false
        }

        return div.extensions?.any { extension ->
            extension.id == EXTENSION_ID && extension.params?.run {
                has(LOTTIE_PARAM_URL) || has(LOTTIE_PARAM_JSON)
            } ?: false
        } ?: false
    }

    override fun bindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        val params: JSONObject = div.extensions?.find { extension ->
            return@find extension.id == EXTENSION_ID
        }?.params ?: return logger.fail("failed to get extension params from extension $EXTENSION_ID")
        val lottieView = view as? LoadableImageView
            ?: return logger.fail("view is not instance of DivGifImageView")
        val lottieController = lottieView.delegate as? LottieController
            ?: return logger.fail("DivGifImageView delegate not instance of lottieController!")

        val lottieData = params.lottieData ?: run {
            KAssert.fail { "Neither $LOTTIE_PARAM_URL nor $LOTTIE_PARAM_JSON found" }
            return
        }

        if (lottieData == lottieController.data) {
            return
        }
        lottieController.clearComposition()
        lottieController.data = lottieData

        lottieView.launchOnAttachedToWindow {
            val result: LottieResult<LottieComposition>
            withContext(Dispatchers.IO) {
                result = repo.receiveLottieComposition(lottieData, view.context)
            }
            withContext(Dispatchers.Main) {
                val composition: LottieComposition? = result.value
                if (composition != null) {
                    logger.log("successfully received lotte composition for ${lottieData.description}")
                    lottieController.setComposition(composition)

                    val repeatList = params.getRepeatList(composition)

                    if (repeatList != null) {
                        lottieController.setupRepeatList(repeatList)
                    } else {
                        lottieController.setRepeatCount(params.repeatCount)
                        lottieController.setRepeatMode(params.repeatMode)
                    }

                    lottieController.playAnimation()

                    params.isPlaying?.let { expression ->
                        addSubscription(expression.observe(expressionResolver) { isPlaying ->
                            lottieController.playOrPauseAnimation(isPlaying)
                        })
                    }
                } else {
                    logger.fail("failed to receive lotte composition on ${lottieData.description}", result.exception)
                }
            }
        }
    }

    private fun LottieController.playOrPauseAnimation(isPlaying: Boolean) {
        if (!isPlaying) {
            if (isAnimating()) {
                pauseAnimation()
            }
            return
        }
        if (isAnimating()) {
            return
        }
        if (getProgress() < 1f || getRepeatCount() == LottieDrawable.INFINITE) {
            resumeAnimation()
        }
    }

    private fun LottieController.setupRepeatList(
        repeatList: List<LottieRepeat>
    ) {
        var repeatIndex = 0
        var currentRepeat = repeatList.getOrNull(repeatIndex)

        val update: (LottieRepeat) -> Unit = { lottieRepeat ->
            setRepeatCount(lottieRepeat.repeatCount)
            setRepeatMode(lottieRepeat.repeatMode)
            setMinFrame(lottieRepeat.minFrame)
            setMaxFrame(lottieRepeat.maxFrame)
        }

        currentRepeat?.let(update)

        setEndListener {
            repeatIndex++

            currentRepeat = repeatList.getOrNull(repeatIndex)

            currentRepeat?.let(update)

            resumeAnimation()
        }
    }

    private fun JSONObject.getRepeatList(composition: LottieComposition): List<LottieRepeat>? {
        return optJSONArray(LOTTIE_PARAM_REPEATS)
            ?.mapNotNull { it as? JSONObject }
            ?.map { repeat ->
                val from = repeat.optInt(LOTTIE_PARAM_MIN_FRAME, -1)
                val to = repeat.optInt(LOTTIE_PARAM_MAX_FRAME, -1)

                LottieRepeat(
                    repeat.repeatCount,
                    repeat.repeatMode,
                    if (from != -1) from else composition.startFrame.toInt(),
                    if (to != -1) to else composition.endFrame.toInt()
                )
            }
    }

    override fun unbindView(divView: Div2View, expressionResolver: ExpressionResolver, view: View, div: DivBase) {
        val lottieView = view as? LoadableImageView
            ?: return logger.fail("view is not instance of DivGifImageView")
        val lottieController = lottieView.delegate as? LottieController
            ?: return logger.fail("DivGifImageView delegate not instance of lottieController!")
        lottieView.clearOnAttachedToWindowScope()
        lottieController.clearComposition()
        lottieController.data = null
        release()
    }
}

private val JSONObject.lottieData: LottieData?
    get() {
        lottieUrl?.let { url ->
            return LottieData.External(url)
        }
        lottieJson?.let { json ->
            return LottieData.Embedded(json)
        }
        return null
    }

private val JSONObject.lottieUrl: String?
    get() = opt(LOTTIE_PARAM_URL) as? String

private val JSONObject.lottieJson: JSONObject?
    get() = optJSONObject(LOTTIE_PARAM_JSON)

private val JSONObject.repeatCount: Int
    get() {
        if (this.has(LOTTIE_PARAM_REPEAT_COUNT)) {
            return optInt(LOTTIE_PARAM_REPEAT_COUNT)
        }
        return 1
    }

private val JSONObject.isPlaying: Expression<Boolean>?
    get() {
        val logger = ParsingErrorLogger.LOG
        return JsonParser.readOptionalExpression(
            this,
            LOTTIE_PARAM_PLAYING,
            ANY_TO_BOOLEAN,
            JsonParser.alwaysValid(),
            logger,
            DivParsingEnvironment(logger),
            TYPE_HELPER_BOOLEAN
        )
    }

@LottieDrawable.RepeatMode
private val JSONObject.repeatMode: Int
    get() = when (this.optString(LOTTIE_PARAM_REPEAT_MODE)) {
        LOTTIE_PARAM_REPEAT_MODE_REVERSE -> LottieDrawable.REVERSE
        LOTTIE_PARAM_REPEAT_MODE_RESTART -> LottieDrawable.RESTART
        else -> LottieDrawable.RESTART
    }
