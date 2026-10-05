package com.yandex.div.lottie

import android.view.View
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieResult
import com.yandex.div.core.Disposable
import com.yandex.div.core.extension.DivExtensionActionHandler
import com.yandex.div.core.extension.DivExtensionHandler
import com.yandex.div.core.network.DivNetworkClient
import com.yandex.div.core.preload.PreloadingRegistry
import com.yandex.div.core.preload.UriPreloadResult
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.widget.LoadableImageView
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.extensions.lottie.LottieData
import com.yandex.div.internal.extensions.lottie.LottieExtensionParamsParser
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
 * @param resourceLoader optional loader for host-provided resources. Unclaimed URLs keep
 * the existing cache and network behavior.
 */
open class DivLottieExtensionHandler @JvmOverloads constructor(
    private val rawResProvider: DivLottieRawResProvider = DivLottieRawResProvider.STUB,
    private val logger: DivLottieLogger = DivLottieLogger.STUB,
    cache: DivLottieNetworkCache = DivLottieNetworkCache.STUB,
    private val asyncUpdatesEnabled: Boolean,
    private val preloadScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    networkClient: DivNetworkClient? = null,
    private val resourceLoader: DivLottieResourceLoader? = null,
) : DivExtensionHandler, ExpressionSubscriber {

    constructor(
        rawResProvider: DivLottieRawResProvider = DivLottieRawResProvider.STUB,
        logger: DivLottieLogger = DivLottieLogger.STUB,
        cache: DivLottieNetworkCache = DivLottieNetworkCache.STUB,
    ) : this(rawResProvider, logger, cache, asyncUpdatesEnabled = true)

    public constructor(
        networkClient: DivNetworkClient,
        rawResProvider: DivLottieRawResProvider = DivLottieRawResProvider.STUB,
        logger: DivLottieLogger = DivLottieLogger.STUB,
        cache: DivLottieNetworkCache = DivLottieNetworkCache.STUB,
    ) : this(rawResProvider, logger, cache, asyncUpdatesEnabled = true, networkClient = networkClient)

    private val parser = LottieExtensionParamsParser(
        assetMapper = rawResProvider::provideAssetFile,
        rawResMapper = rawResProvider::provideRes,
        reportError = logger::fail,
        urlFilter = { url -> resourceLoader?.canLoad(url) == true },
    )

    internal val compositionRepository = DivLottieCompositionRepository(
        cache, logger, networkClient, preloadScope, resourceLoader,
    )
    private val lottieActionHandler = DivLottieActionHandler()

    override val actionHandler: DivExtensionActionHandler = lottieActionHandler

    // Kept for API compatibility. Playback controllers own the subscriptions of each view.
    override val subscriptions: MutableList<Disposable> = mutableListOf()

    override fun addSubscription(subscription: Disposable?): Unit = Unit

    override fun closeAllSubscription(): Unit = Unit

    override fun release(): Unit = Unit

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
            compositionRepository.preloadLottieComposition(url) { result ->
                preloading?.onCompleted(result)
            }
            return
        }
        if (!asyncUpdatesEnabled) return
        val jsonData = parser.parseInlineJson(params) ?: return
        val preloading = preloadingRegistry?.registerPreloading("lottie")
        if (preloading == null) {
            preloadScope.launch { compositionRepository.preloadInlineComposition(jsonData) {} }
            return
        }
        val completed = AtomicBoolean(false)
        val job = preloadScope.launch {
            compositionRepository.preloadInlineComposition(jsonData) { result ->
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

        (view.getTag(R.id.lottie_playback_controller) as? LottiePlaybackController)?.close()
        view.setTag(R.id.lottie_playback_controller, null)
        lottieController.clearComposition()
        lottieController.data = lottieData
        val playbackController = LottiePlaybackController(
            lottieController,
            divView,
            parsedParams,
            expressionResolver,
            divView.div2Component.animationsEnabledController,
        )
        view.setTag(R.id.lottie_playback_controller, playbackController)

        lottieView.launchOnAttachedToWindow {
            val result = withContext(Dispatchers.IO) {
                compositionRepository.receiveLottieCompositionAsync(lottieData, view.context)
            }
            withContext(Dispatchers.Main) {
                if (view.getTag(R.id.lottie_playback_controller) === playbackController) {
                    playbackController.onCompositionReady(result, lottieData.description)
                }
            }
        }
    }

    private fun LottiePlaybackController.onCompositionReady(
        result: LottieResult<LottieComposition>,
        description: String,
    ) {
        val composition = result.value ?: return logger.fail(
            "Failed to receive LottieComposition for: $description",
            result.exception
        )
        onCompositionReady(composition)
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
        (view.getTag(R.id.lottie_playback_controller) as? LottiePlaybackController)?.close()
        view.setTag(R.id.lottie_playback_controller, null)
        lottieView.clearOnAttachedToWindowScope()
        lottieController.clearComposition()
        lottieController.data = null
    }
}

private val DivBase.lottieExtension: DivExtension?
    get() = extensions?.find { it.id == EXTENSION_ID }

private const val EXTENSION_ID = "lottie"
