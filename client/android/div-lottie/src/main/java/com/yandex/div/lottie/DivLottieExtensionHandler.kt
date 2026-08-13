package com.yandex.div.lottie

import android.view.View
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.LottieResult
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.preload.PreloadingRegistry
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.extensions.lottie.LottieExtensionParams
import com.yandex.div.internal.extensions.lottie.LottieExtensionParamsParser
import com.yandex.div.internal.extensions.lottie.LottieRepeat
import com.yandex.div.internal.extensions.lottie.LottieRepeatMode
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivExtension
import com.yandex.div2.DivGifImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Handler for `lottie` extension.
 *
 * You can use this extension for `gif` element and not worry about backward compatibility, as this
 * extension inherit all [DivGifImage] attributes and use [DivGifImage.gifUrl] as fallback.
 *
 * @param asyncUpdatesEnabled enables background preparation of animations: Lottie async frame
 * updates and preload of inline `lottie_json` compositions.
 * @param preloadScope scope for inline composition preload coroutines. Provide your own scope
 * and cancel it to stop preload work when the handler is no longer needed; by default preloads
 * run in an internal scope that lives as long as the handler.
 */
open class DivLottieExtensionHandler(
    private val rawResProvider: DivLottieRawResProvider = DivLottieRawResProvider.STUB,
    private val logger: DivLottieLogger = DivLottieLogger.STUB,
    cache: DivLottieNetworkCache = DivLottieNetworkCache.STUB,
    private val asyncUpdatesEnabled: Boolean,
    private val preloadScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) : DivExtensionHandler, ExpressionSubscriber {

    constructor(
        rawResProvider: DivLottieRawResProvider = DivLottieRawResProvider.STUB,
        logger: DivLottieLogger = DivLottieLogger.STUB,
        cache: DivLottieNetworkCache = DivLottieNetworkCache.STUB,
    ) : this(rawResProvider, logger, cache, asyncUpdatesEnabled = true)

    private val parser = LottieExtensionParamsParser(
        assetMapper = rawResProvider::provideAssetFile,
        rawResMapper = rawResProvider::provideRes,
        reportError = logger::fail
    )

    private val repo = DivLottieCompositionRepository(cache, logger)
    private val playbackRecords = mutableMapOf<String, PlaybackStateController>()

    override val subscriptions: MutableList<Disposable> = mutableListOf()

    override fun preprocess(div: DivBase, expressionResolver: ExpressionResolver) {
        preprocessInternal(div, expressionResolver, null)
    }

    override fun preprocess(
        div: DivBase,
        expressionResolver: ExpressionResolver,
        preloadingRegistry: PreloadingRegistry,
    ) {
        preprocessInternal(div, expressionResolver, preloadingRegistry)
    }

    private fun preprocessInternal(
        div: DivBase,
        expressionResolver: ExpressionResolver,
        preloadingRegistry: PreloadingRegistry?,
    ) {
        val params = div.lottieExtension?.params ?: return
        val url = parser.parseUrl(params, expressionResolver)
        if (url != null) {
            val preloading = preloadingRegistry?.registerPreloading("lottie")
            repo.preloadLottieComposition(url) { result ->
                preloading?.onCompleted(result)
            }
            return
        }
        if (!asyncUpdatesEnabled) return
        val jsonData = parser.parseInlineJson(params) ?: return
        val preloading = preloadingRegistry?.registerPreloading("lottie")
        if (preloading == null) {
            preloadScope.launch { repo.preloadInlineComposition(jsonData) {} }
            return
        }
        val completed = AtomicBoolean(false)
        val job = preloadScope.launch {
            repo.preloadInlineComposition(jsonData) { result ->
                if (completed.compareAndSet(false, true)) preloading.onCompleted(result)
            }
        }
        job.invokeOnCompletion { cause ->
            // If the preload coroutine is cancelled (e.g. the host cancelled preloadScope),
            // report the registered preload as failed so DivPreloader is not stranded.
            if (cause != null && completed.compareAndSet(false, true)) {
                preloading.onCompleted(UriPreloadResult(jsonData.inlinePreloadUri, cause))
            }
        }
    }

    override fun beforeBindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val divGifImageView = view as? LoadableImageView ?: return
        if (divGifImageView.delegate !is LottieController) {
            divGifImageView.delegate = LottieController(divGifImageView, asyncUpdatesEnabled).apply {
                enableMergePathsForKitKatAndAbove(true)
                setImageAssetsFolder(rawResProvider.provideAssetFolder())
            }
        }
    }

    override fun matches(div: DivBase): Boolean {
        if (div !is DivGifImage) {
            return false
        }
        return div.lottieExtension != null
    }

    override fun bindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val params = div.lottieExtension?.params
            ?: return logger.fail("Failed to get extension params for extensions: $EXTENSION_ID")
        val lottieView = view as? LoadableImageView
            ?: return logger.fail("View is not an instance of DivGifImageView")
        val lottieController = lottieView.delegate as? LottieController
            ?: return logger.fail("DivGifImageView delegate not an instance of LottieController")

        val parsedParams = parser.parse(params, expressionResolver) ?: return

        val lottieData = parsedParams.data
        if (lottieData == lottieController.data) return

        lottieController.clearComposition()
        lottieController.data = lottieData
        lottieController.clearPlaybackEndListeners()

        lottieView.launchOnAttachedToWindow {
            val result = withContext(Dispatchers.IO) {
                repo.receiveLottieComposition(lottieData, view.context)
            }
            withContext(Dispatchers.Main) {
                val playbackState = getOrCreatePlaybackState(divView, div)
                lottieController.addEndListener(playbackState.animationEndListener)
                lottieController.bind(
                    divView = divView,
                    result = result,
                    params = parsedParams,
                    resolver = expressionResolver,
                    description = lottieData.description,
                    playbackStateController = playbackState,
                    animationsEnabled = { divView.div2Component.animationsEnabledController.isEnabled() },
                )
            }
        }
    }

    private fun getOrCreatePlaybackState(
        divView: Div2View,
        div: DivBase
    ): PlaybackStateController {
        val id = "tag: ${divView.dataTag} div: $div"
        return playbackRecords.getOrPut(id) { PlaybackStateController() }
    }

    private fun LottieController.bind(
        divView: Div2View,
        result: LottieResult<LottieComposition>,
        params: LottieExtensionParams,
        resolver: ExpressionResolver,
        description: String,
        playbackStateController: PlaybackStateController,
        animationsEnabled: () -> Boolean,
    ) {
        val composition = result.value ?: return logger.fail(
            "Failed to receive LottieComposition for: $description",
            result.exception
        )

        setComposition(composition)

        val repeats = params.repeats
        if (repeats.isEmpty()) {
            setRepeatCount(params.repeatCount)
            setRepeatMode(params.repeatMode.toLottieDrawableRepeatMode())
        } else {
            setupRepeats(repeats, composition)
        }
        setSafeMode(params.safeMode)
        playbackStateController.overrideRepeatCount(getRepeatCount())

        val isPlaying = params.isPlaying
        val startPlay = isPlaying?.evaluate(resolver) ?: true
        if (playbackStateController.canPlay() && startPlay && animationsEnabled()) {
            playAnimation()
        } else {
            pauseAnimationAt(progress = if (getRepeatMode() == LottieDrawable.REVERSE) 0f else 1f)
        }

        isPlaying?.let { expression ->
            addSubscription(expression.observe(resolver) { playOrPauseAnimation(it && animationsEnabled()) })
        }

        subscribeToAnimationsEnabled(divView) {
            playOrPauseAnimation((isPlaying?.evaluate(resolver) ?: true) && animationsEnabled())
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

    private fun LottieController.setupRepeats(
        repeats: List<LottieRepeat>,
        composition: LottieComposition
    ) {
        var repeatIndex = 0
        var currentRepeat = repeats.getOrNull(repeatIndex)

        val update: (LottieRepeat) -> Unit = { repeat ->
            setRepeatCount(repeat.count)
            setRepeatMode(repeat.mode.toLottieDrawableRepeatMode())
            setMinFrame(repeat.minFrame ?: composition.startFrame.toInt())
            setMaxFrame(repeat.maxFrame ?: composition.endFrame.toInt())
        }

        currentRepeat?.let(update)

        addEndListener {
            repeatIndex++
            currentRepeat = repeats.getOrNull(repeatIndex)
            currentRepeat?.let(update)
            resumeAnimation()
        }
    }

    override fun unbindView(
        divView: Div2View,
        expressionResolver: ExpressionResolver,
        view: View,
        div: DivBase
    ) {
        val lottieView = view as? LoadableImageView
            ?: return logger.fail("View is not an instance of DivGifImageView")
        val lottieController = lottieView.delegate as? LottieController
            ?: return logger.fail("DivGifImageView delegate not an instance of LottieController")
        lottieView.clearOnAttachedToWindowScope()
        lottieController.clearComposition()
        lottieController.data = null
        lottieController.clearPlaybackEndListeners()
        lottieController.clearAnimationsEnabledSubscription()
        release()
    }
}

@LottieDrawable.RepeatMode
private fun LottieRepeatMode.toLottieDrawableRepeatMode(): Int {
    return when (this) {
        LottieRepeatMode.RESTART -> LottieDrawable.RESTART
        LottieRepeatMode.REVERSE -> LottieDrawable.REVERSE
    }
}

private val DivBase.lottieExtension: DivExtension?
    get() = extensions?.find { it.id == EXTENSION_ID }

private class PlaybackStateController {
    private var playedAlLeastOnce = false
    private var infinitePlaybacks = false

    val animationEndListener: () -> Unit = {
        playedAlLeastOnce = true
    }

    fun overrideRepeatCount(repeatCount: Int) {
        infinitePlaybacks = repeatCount == INFINITE_PLAYBACK
    }

    fun canPlay(): Boolean {
        return infinitePlaybacks || !playedAlLeastOnce
    }
}

private const val EXTENSION_ID = "lottie"
private const val INFINITE_PLAYBACK = -1
