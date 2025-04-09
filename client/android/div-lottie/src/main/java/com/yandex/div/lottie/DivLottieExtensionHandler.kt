package com.yandex.div.lottie

import android.net.Uri
import android.view.View
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.parser.ANY_TO_BOOLEAN
import com.yandex.div.internal.parser.ANY_TO_URI
import com.yandex.div.internal.parser.JsonExpressionParser
import com.yandex.div.internal.parser.NUMBER_TO_INT
import com.yandex.div.internal.parser.TYPE_HELPER_BOOLEAN
import com.yandex.div.internal.parser.TYPE_HELPER_INT
import com.yandex.div.internal.parser.TYPE_HELPER_STRING
import com.yandex.div.internal.parser.TYPE_HELPER_URI
import com.yandex.div.internal.util.mapNotNull
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.lottie.DivLottieExtensionHandler.Companion.EXTENSION_ID
import com.yandex.div2.DivBase
import com.yandex.div2.DivGifImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

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
        repo.preloadLottieComposition(lottieUrl.evaluate(expressionResolver))
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
        val params = div.extensions?.find { it.id == EXTENSION_ID }?.params
            ?: return logger.fail("failed to get extension params from extension $EXTENSION_ID")
        val lottieView = view as? LoadableImageView
            ?: return logger.fail("view is not instance of DivGifImageView")
        val lottieController = lottieView.delegate as? LottieController
            ?: return logger.fail("DivGifImageView delegate not instance of lottieController!")
        val lottieData = params.getLottieData(expressionResolver)
            ?: return KAssert.fail { "Neither $LOTTIE_PARAM_URL nor $LOTTIE_PARAM_JSON found" }

        if (lottieData == lottieController.data) return

        lottieController.clearComposition()
        lottieController.data = lottieData

        lottieView.launchOnAttachedToWindow {
            val result = withContext(Dispatchers.IO) {
                repo.receiveLottieComposition(lottieData, view.context)
            }
            withContext(Dispatchers.Main) {
                lottieController.bind(result, params, expressionResolver, lottieData.description)
            }
        }
    }

    private fun LottieController.bind(
        result: LottieResult<LottieComposition>,
        params: JSONObject,
        resolver: ExpressionResolver,
        description: String,
    ) {
        val composition = result.value ?: return logger.fail(
            "failed to receive lotte composition on $description",
            result.exception
        )

        logger.log("successfully received lotte composition for $description")
        setComposition(composition)

        params.getRepeatList(composition, resolver)?.let {
            setupRepeatList(it)
        } ?: run {
            setRepeatCount(params.getRepeatCount(resolver))
            setRepeatMode(params.getRepeatMode(resolver))
        }

        playAnimation()

        params.isPlaying?.let { expression ->
            addSubscription(expression.observe(resolver) { playOrPauseAnimation(it) })
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

    private companion object {
        private const val EXTENSION_ID = "lottie"
        private const val LOTTIE_PARAM_URL = "lottie_url"
        private const val LOTTIE_PARAM_JSON = "lottie_json"
        private const val LOTTIE_PARAM_REPEATS = "repeats"
        private const val LOTTIE_PARAM_REPEAT_MODE = "repeat_mode"
        private const val LOTTIE_PARAM_REPEAT_MODE_REVERSE = "reverse"
        private const val LOTTIE_PARAM_REPEAT_COUNT = "repeat_count"
        private const val LOTTIE_PARAM_MIN_FRAME = "min_frame"
        private const val LOTTIE_PARAM_MAX_FRAME = "max_frame"
        private const val LOTTIE_PARAM_PLAYING = "is_playing"

        val parsingLogger: ParsingErrorLogger = ParsingErrorLogger.LOG
        val environment = DivParsingEnvironment(parsingLogger)

        fun JSONObject.getLottieData(resolver: ExpressionResolver): LottieData? {
            return lottieUrl?.let { LottieData.External(it.evaluate(resolver).toString()) }
                ?: lottieJson?.let { LottieData.Embedded(it) }
        }

        val JSONObject.lottieUrl: Expression<Uri>? get() {
            return JsonExpressionParser.readOptionalExpression(
                environment,
                this,
                LOTTIE_PARAM_URL,
                TYPE_HELPER_URI,
                ANY_TO_URI
            )
        }

        val JSONObject.lottieJson: JSONObject? get() = optJSONObject(LOTTIE_PARAM_JSON)

        fun JSONObject.getRepeatList(
            composition: LottieComposition,
            resolver: ExpressionResolver,
        ): List<LottieRepeat>? {
            return optJSONArray(LOTTIE_PARAM_REPEATS)
                ?.mapNotNull { it as? JSONObject }
                ?.map { repeat ->
                    LottieRepeat(
                        repeat.getRepeatCount(resolver),
                        repeat.getRepeatMode(resolver),
                        repeat.minFrame?.toInt(resolver) ?: composition.startFrame.toInt(),
                        repeat.maxFrame?.toInt(resolver) ?: composition.endFrame.toInt()
                    )
                }
        }

        fun JSONObject.getRepeatCount(resolver: ExpressionResolver): Int {
            return JsonExpressionParser.readOptionalExpression(
                environment,
                this,
                LOTTIE_PARAM_REPEAT_COUNT,
                TYPE_HELPER_INT,
                NUMBER_TO_INT
            )?.toInt(resolver) ?: 1
        }

        val JSONObject.minFrame: Expression<Long>? get() {
            return JsonExpressionParser.readOptionalExpression(
                environment,
                this,
                LOTTIE_PARAM_MIN_FRAME,
                TYPE_HELPER_INT,
                NUMBER_TO_INT
            )
        }

        val JSONObject.maxFrame: Expression<Long>? get() {
            return JsonExpressionParser.readOptionalExpression(
                environment,
                this,
                LOTTIE_PARAM_MAX_FRAME,
                TYPE_HELPER_INT,
                NUMBER_TO_INT
            )
        }

        val JSONObject.isPlaying: Expression<Boolean>? get() {
            return JsonExpressionParser.readOptionalExpression(
                environment,
                this,
                LOTTIE_PARAM_PLAYING,
                TYPE_HELPER_BOOLEAN,
                ANY_TO_BOOLEAN
            )
        }

        fun Expression<Long>.toInt(resolver: ExpressionResolver) = evaluate(resolver).toIntSafely()

        @LottieDrawable.RepeatMode
        fun JSONObject.getRepeatMode(resolver: ExpressionResolver): Int {
            val repeatMode = JsonExpressionParser.readOptionalExpression(
                environment,
                this,
                LOTTIE_PARAM_REPEAT_MODE,
                TYPE_HELPER_STRING
            )
            return if (repeatMode?.evaluate(resolver) == LOTTIE_PARAM_REPEAT_MODE_REVERSE) {
                LottieDrawable.REVERSE
            } else {
                LottieDrawable.RESTART
            }
        }
    }
}
