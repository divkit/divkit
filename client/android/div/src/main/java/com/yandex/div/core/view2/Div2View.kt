package com.yandex.div.core.view2

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Canvas
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnyThread
import androidx.core.view.isNotEmpty
import androidx.core.view.isVisible
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivBindingCallback
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.core.DivCreationTracker
import com.yandex.div.core.DivCustomContainerChildFactory
import com.yandex.div.core.DivErrorsReporter
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivViewConfig
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.downloader.DivDataChangedObserver
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.util.binding.BindingDispatcher
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.binding.MainThreadTaskPolicy
import com.yandex.div.core.util.binding.asBindingObserver
import com.yandex.div.core.view2.divs.clearFocusOnClick
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.core.view2.divs.drawShadow
import com.yandex.div.core.view2.divs.widgets.DivAnimator
import com.yandex.div.data.VariableMutationException
import com.yandex.div.histogram.Div2ViewHistogramReporter
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.histogram.util.HistogramClock
import com.yandex.div.internal.util.UiThreadHandler
import com.yandex.div.internal.util.hasScrollableChildUnder
import com.yandex.div.internal.util.toMapSafe
import com.yandex.div.internal.widget.FrameContainerLayout
import com.yandex.div.internal.widget.menu.OverflowMenuSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import java.util.WeakHashMap

/**
 * Main entry point for building Div2s
 */
@SuppressLint("ViewConstructor")
open class Div2View private constructor(
    internal val context: Div2Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val constructorCallTime: Long,
) : FrameContainerLayout(context, attrs, defStyleAttr), DivViewFacade {

    @InternalApi
    val div2Component: Div2Component = context.div2Component

    private val consumeTouchesToClearInputFocus = div2Component.consumeTouchesToClearInputFocus
    private val overflowMenuListeners = mutableListOf<OverflowMenuSubscriber.Listener>()
    internal val oldExpressionResolver get() = bindingController.oldExpressionResolver
    internal var runtimeStore: RuntimeStore
        get() = bindingController.runtimeStore
        set(value) { bindingController.runtimeStore = value }
    internal var viewStateStore: DivViewStateStore
        get() = bindingController.viewStateStore
        set(value) { bindingController.viewStateStore = value }
    internal var inMiddleOfBind: Boolean
        get() = bindingController.inMiddleOfBind
        set(value) { bindingController.inMiddleOfBind = value }
    internal val divTimerEventDispatcher get() = bindingController.divTimerEventDispatcher

    @PublishedApi
    internal var forceCanvasClipping: Boolean = false

    internal val bindOnAttachRunnable get() = rootStateController.deferredBinding
    internal var stateId: Long
        get() = rootStateController.stateId
        set(value) { rootStateController.stateId = value }

    private val viewToDivBindings = WeakHashMap<View, Div>()
    private var visibilityTrackingBatch: VisibilityTrackingBatch? = null
    private var workerVisibilityTrackingBatch: WorkerVisibilityTrackingBatch? = null
    private val loadReferences = mutableListOf<LoadReference>()

    @Volatile
    private var config = DivViewConfig.DEFAULT

    private val rootStateController by lazy { DivRootStateController(this) }
    private val bindingController by lazy {
        DivViewBindingController(this, rootStateController)
    }
    internal val currentRebindReusableList get() = bindingController.currentRebindReusableList
    internal val complexRebindInProgress get() = bindingController.complexRebindInProgress

    private val renderConfig = {
        DivKit.getInstance(context).component.histogramRecordConfiguration.renderConfiguration.get()
    }
    internal val histogramReporter by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Div2ViewHistogramReporter(
            { div2Component.histogramReporter },
            renderConfig
        )
    }

    private val gestureDetector = GestureDetector(
        this.context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                clearFocusOnClick(inputFocusTracker)
                return true
            }
        },
        Handler(Looper.getMainLooper())
)

    var dataTag: DivDataTag
        get() = bindingController.dataTag
        internal set(value) { bindingController.dataTag = value }

    var prevDataTag: DivDataTag
        get() = bindingController.prevDataTag
        internal set(value) { bindingController.prevDataTag = value }

    val divData: DivData? get() = bindingController.divData

    val logId: String
        get() = divData?.logId ?: ""

    var actionHandler: DivActionHandler? = null

    /**
     * Name of component that uses [Div2View].
     */
    var componentName: String?
        get() = histogramReporter.component
        set(value) {
            histogramReporter.component = value
        }

    private var timeCreated: Long = -1

    @HistogramCallType
    private val viewCreateCallType: String = div2Component.divCreationTracker.viewCreateCallType

    private var drawWasSkipped = true

    internal var dataComponent = div2Component.dataComponentStore.getOrPut("", div2Component)

    internal val viewComponent: Div2ViewComponent = div2Component.viewComponent()
        .divView(this)
        .build()

    private val bindingDispatcher: BindingDispatcher
        get() = viewComponent.bindingDispatcher

    internal val inputFocusTracker = viewComponent.inputFocusTracker

    init {
        timeCreated = DivCreationTracker.currentUptime
        div2Component.releaseManager.observeDivLifecycle(this)
    }

    @JvmOverloads
    constructor(context: Div2Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0
    ) : this(context, attrs, defStyleAttr, HistogramClock.uptime())

    final override var aspectRatio: Float
        get() = super.aspectRatio
        set(value) {
            super.aspectRatio = value
        }

    @ExperimentalApi
    fun isBackgroundBindingInProgress(): Boolean = bindingDispatcher.isBackgroundBindingInProgress

    /**
     * Returns whether asynchronous work is queued or unfinished for this view,
     * including main-thread changes and successful completion callbacks.
     */
    @ExperimentalApi
    fun hasPendingAsyncBindings(): Boolean = bindingDispatcher.hasPendingAsyncBindings

    /**
     * Reports completion of asynchronous preparation through the legacy Boolean callback.
     * Binding that requires attachment may still be pending. The Boolean retains its legacy semantics.
     */
    @ExperimentalApi
    fun setDataAsync(
        data: DivData?,
        tag: DivDataTag,
        onComplete: ((Boolean) -> Unit)?,
    ) = bindingController.setDataAsync(data, null, tag, onComplete = onComplete)

    /**
     * Reports this request's binding phases on the main thread through one [onBindingPhase] callback.
     * [DivBindingPhase.PREPARED] means preparation has finished and the view can be attached.
     * If binding is already complete, [DivBindingPhase.APPLIED] is reported without PREPARED.
     * Otherwise APPLIED follows the remaining binding on attachment. Layout and media may still be pending.
     * Each request has exactly one terminal phase: APPLIED, CANCELLED, or FAILED.
     * Runtime errors use the configured [DivErrorsReporter]. Observing phases does not change binding strategy.
     * Exceptions that propagated from binding before observing phases continue to propagate.
     * Kotlin callers select this overload with the named `onBindingPhase` argument;
     * Java callers use `setDataAsyncWithPhases`.
     */
    @ExperimentalApi
    @JvmName("setDataAsyncWithPhases")
    fun setDataAsync(
        data: DivData?,
        tag: DivDataTag,
        onBindingPhase: DivBindingCallback,
    ) = bindingController.setDataAsyncWithPhases(data, null, tag, onBindingPhase.asBindingObserver())

    /**
     * Uses the same binding lifecycle as [setDataAsync], with an explicit previous document.
     */
    @ExperimentalApi
    fun setDataAsync(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        onComplete: ((Boolean) -> Unit)?,
    ) = bindingController.setDataAsync(data, oldDivData, tag, onComplete = onComplete)

    /**
     * See [setDataAsync] for the per-request lifecycle contract.
     */
    @ExperimentalApi
    @JvmName("setDataAsyncWithPhases")
    fun setDataAsync(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        onBindingPhase: DivBindingCallback,
    ) = bindingController.setDataAsyncWithPhases(data, oldDivData, tag, onBindingPhase.asBindingObserver())

    fun setData(data: DivData?, tag: DivDataTag): Boolean =
        bindingController.setDataOrReplacePending(data, null, tag)

    fun setData(data: DivData?, oldDivData: DivData?, tag: DivDataTag): Boolean =
        bindingController.setDataOrReplacePending(data, oldDivData, tag)

    @ExperimentalApi
    fun setDataWithStatesAsync(
        data: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>,
        temporary: Boolean,
        onComplete: ((Boolean) -> Unit)?,
    ) =
        bindingController.setDataAsync(data, null, tag, paths, temporary, onComplete)

    fun setDataWithStates(
        data: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>,
        temporary: Boolean
    ): Boolean =
        bindingController.setDataOrReplacePending(data, null, tag, paths, temporary)

    @ExperimentalApi
    fun applyPatchAsync(
        patch: DivPatch,
        onComplete: ((Boolean) -> Unit)?,
    ) =
        bindingController.applyPatchAsync(patch, onComplete)

    fun applyPatch(patch: DivPatch): Boolean =
        bindingController.applyPatch(patch)

    fun tryLogVisibility(): Unit = bindingDispatcher.runWithinBindingContext(
        BindingTaskType.DATA,
        VisibilityTrackingOperation.LOG_VISIBILITY,
    ) {
        val state = divData?.states?.firstOrNull { it.stateId == stateId }
        state?.let { trackStateVisibility(it) }
        trackChildrenVisibilityInternal()
    }

    /**
     * Canceling visibility tracking.
     *
     * @param dispatchDisappearActions when true, disappear actions that were already appeared
     * but haven't fired yet are dispatched instead of just being discarded, e.g. on [cleanup].
     * */
    @JvmOverloads
    fun discardVisibilityTracking(dispatchDisappearActions: Boolean = false) {
        if (UiThreadHandler.get().isMainThread()) {
            visibilityTrackingBatch?.pending = false
        } else if (bindingDispatcher.isCollectingMainThreadActions) {
            workerVisibilityTrackingBatch?.discard()
        }
        bindingDispatcher.runWithinBindingContext(BindingTaskType.DATA, VisibilityTrackingOperation.DISCARD) {
            val state = divData?.states?.firstOrNull { it.stateId == stateId }
            state?.let { discardStateVisibility(it, dispatchDisappearActions) }
            discardChildrenVisibility(dispatchDisappearActions)
        }
    }

    internal fun <T> withBatchedVisibilityTracking(block: () -> T): T {
        if (!UiThreadHandler.get().isMainThread()) {
            if (!bindingDispatcher.isCollectingMainThreadActions) return block()
            val batch = workerVisibilityTrackingBatch ?: WorkerVisibilityTrackingBatch(this, bindingDispatcher) {
                suppressExpressionErrors { trackChildrenVisibilityNow() }
            }.also { workerVisibilityTrackingBatch = it }
            return batch.collect(block)
        }
        if (visibilityTrackingBatch?.matchesData(this) == true) {
            return block()
        }
        val batch = VisibilityTrackingBatch(divData, dataTag)
        visibilityTrackingBatch = batch
        try {
            val result = block()
            if (visibilityTrackingBatch === batch) {
                visibilityTrackingBatch = null
                if (batch.pending && batch.matchesData(this)) {
                    suppressExpressionErrors { trackChildrenVisibilityNow() }
                }
            }
            return result
        } finally {
            if (visibilityTrackingBatch === batch) {
                visibilityTrackingBatch = null
            }
        }
    }

    fun trackChildrenVisibility() {
        if (UiThreadHandler.get().isMainThread()) {
            visibilityTrackingBatch?.let {
                if (it.matchesData(this)) {
                    it.pending = true
                    return
                }
            }
        } else if (bindingDispatcher.isCollectingMainThreadActions) {
            val batch = workerVisibilityTrackingBatch
            if (batch != null && batch.tryDeferScan()) return
        }
        trackChildrenVisibilityNow()
    }

    internal fun clearViewBindingsAndSubscriptions() {
        synchronized(viewToDivBindings) {
            viewToDivBindings.clear()
        }
        cancelTooltipsInternal()
        overflowMenuListeners.clear()
    }

    internal fun trackStateVisibility(state: DivData.State) {
        div2Component.visibilityActionTracker.trackVisibilityActionsOf(this, expressionResolver, view, state.div)
    }

    internal fun discardStateVisibility(state: DivData.State, dispatchDisappearActions: Boolean = false) {
        val visibilityActionTracker = div2Component.visibilityActionTracker
        if (dispatchDisappearActions) {
            visibilityActionTracker.dispatchWaitingDisappearActions(this, expressionResolver, view)
        }
        visibilityActionTracker.trackVisibilityActionsOf(
            scope = this,
            resolver = expressionResolver,
            view = null,
            div = state.div
        )
    }

    internal fun flushPendingVisibilityTracking() {
        if (!UiThreadHandler.get().isMainThread() && bindingDispatcher.isCollectingMainThreadActions) {
            workerVisibilityTrackingBatch?.flush()
        }
    }

    private fun trackChildrenVisibilityNow(): Unit = bindingDispatcher.runWithinBindingContext(
        BindingTaskType.DATA,
        VisibilityTrackingOperation.TRACK_CHILDREN,
    ) {
        trackChildrenVisibilityInternal()
    }

    private fun trackChildrenVisibilityInternal() {
        val visibilityActionTracker = div2Component.visibilityActionTracker
        val bindingsSnapshot = synchronized(viewToDivBindings) {
            viewToDivBindings.toMapSafe()
        }
        bindingsSnapshot.forEach { (view, div) ->
            view.divBlock?.expressionResolver?.let {
                if (view.isAttachedToWindow) {
                    visibilityActionTracker.trackVisibilityActionsOf(this, it, view, div)
                } else {
                    visibilityActionTracker.trackVisibilityActionsOf(this, it, null, div)
                }
            }
        }
    }

    private fun discardChildrenVisibility(dispatchDisappearActions: Boolean = false) {
        val visibilityActionTracker = div2Component.visibilityActionTracker
        val bindingsSnapshot = synchronized(viewToDivBindings) {
            viewToDivBindings.toMapSafe()
        }
        bindingsSnapshot.forEach { (view, div) ->
            view.divBlock?.expressionResolver?.let {
                if (dispatchDisappearActions) {
                    visibilityActionTracker.dispatchWaitingDisappearActions(this, it, view)
                }
                visibilityActionTracker.trackVisibilityActionsOf(this, it, null, div)
            }
        }
    }

    internal fun getCustomContainerChildFactory(): DivCustomContainerChildFactory {
        return div2Component.divCustomContainerChildFactory
    }

    internal fun sendCreationHistograms() {
        if (timeCreated < 0) {
            return
        }
        div2Component.divCreationTracker.sendHistograms(
            constructorCallTime, timeCreated, div2Component.histogramReporter, viewCreateCallType
        )
        timeCreated = -1
    }

    @SuppressLint("ClickableViewAccessibility")
    final override fun onTouchEvent(event: MotionEvent): Boolean {
        if (consumeTouchesToClearInputFocus && inputFocusTracker.isFocusedOnInput()) {
            gestureDetector.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_DOWN) {
                // Capturing DOWN event to get UP event, but only if actively focused on input
                 return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        histogramReporter.onLayoutStarted()
        super.onLayout(changed, left, top, right, bottom)
        tryLogVisibility()
        histogramReporter.onLayoutFinished()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        histogramReporter.onMeasureStarted()
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        histogramReporter.onMeasureFinished()
    }

    override fun draw(canvas: Canvas) {
        drawWasSkipped = false
        histogramReporter.onDrawStarted()
        super.draw(canvas)
        histogramReporter.onDrawFinished()
        drawWasSkipped = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        bindingController.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        discardVisibilityTracking()
        bindingController.onDetachedFromWindow()
    }

    final override fun addLoadReference(loadReference: LoadReference, targetView: View): Unit =
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            loadReferences.add(loadReference)
        }

    /** Returns true if div can be replaced with given DivData or false otherwise **/
    fun prepareForRecycleOrCleanup(
        newData: DivData,
        oldData: DivData? = null,
        newDataTag: DivDataTag? = null
    ): Boolean {
        return bindingController.prepareForRecycleOrCleanup(newData, oldData, newDataTag)
    }

    internal val errorCollector get() = dataComponent.errorCollectors.getOrCreate(divData)

    fun logError(throwable: Throwable) = errorCollector.logError(throwable)

    fun loadMedia(): Unit = bindingController.loadMedia()

    fun releaseMedia(): Unit = bindingController.releaseMedia()

    internal fun cancelImageLoads() {
        loadReferences.forEach { it.cancel() }
        loadReferences.clear()
    }

    final override fun cleanup() {
        bindingController.cancelBindingRequest()
        rootStateController.bindingPhase = null
        if (UiThreadHandler.get().isMainThread()) {
            visibilityTrackingBatch = null
        }
        bindingController.cleanup()
    }

    final override fun switchToState(
        stateId: Long,
        temporary: Boolean
    ): Unit =
        rootStateController.switchToState(stateId, temporary)

    final override fun switchToInitialState() =
        rootStateController.switchToInitialState()

    final override fun switchToState(
        path: DivStatePath,
        temporary: Boolean
    ): Unit =
        rootStateController.switchToState(path, temporary)

    /**
     * Switch view to states
     * @param pathList - states. See {@link com.yandex.div.core.state.DivStatePath}
     */
    fun switchToMultipleStates(pathList: List<DivStatePath>, temporary: Boolean, withAnimations: Boolean) =
        rootStateController.switchToMultipleStates(pathList, temporary)

    fun isInState(statePath: DivStatePath): Boolean =
        rootStateController.isInState(statePath)

    /**
     * Observers called when DivData changed. Now it used when patch applied.
     * The list with these observers cleans when setting new DivData.
     */
    fun addDivDataChangeObserver(
        observer: DivDataChangedObserver
    ): Unit =
        bindingController.addDivDataChangeObserver(observer)

    fun removeDivDataChangeObserver(
        observer: DivDataChangedObserver
    ): Unit =
        bindingController.removeDivDataChangeObserver(observer)

    /**
     * Observers called before and after changing div data.
     * The list with these observers doesn't cleans when setting new DivData.
     */
    internal fun addPersistentDivDataObserver(observer: PersistentDivDataObserver) =
        bindingController.addPersistentDivDataObserver(observer)

    internal fun removePersistentDivDataObserver(observer: PersistentDivDataObserver) =
        bindingController.removePersistentDivDataObserver(observer)

    final override fun resetToInitialState() = rootStateController.resetToInitialState()

    internal fun rootDiv() = rootStateController.rootDiv()

    fun startDivAnimation(): Unit = bindingDispatcher.runWithinBindingContext(BindingTaskType.DATA) {
        if (isNotEmpty()) {
            (getChildAt(0) as? DivAnimator)?.startDivAnimation()
        }
    }

    fun stopDivAnimation(): Unit = bindingDispatcher.runWithinBindingContext(BindingTaskType.DATA) {
        if (isNotEmpty()) {
            (getChildAt(0) as? DivAnimator)?.stopDivAnimation()
        }
    }

    @JvmOverloads
    fun handleAction(
        action: DivAction,
        reason: String = DivActionReason.EXTERNAL,
        resolver: ExpressionResolver = expressionResolver
    ) {
        val callResolver = expressionResolver
        withLockOrDefer {
            handleActionWithResult(action, reason, resolverAtExecution(resolver, callResolver))
        }
    }

    @JvmOverloads
    fun handleActionWithResult(
        action: DivAction,
        reason: String = DivActionReason.EXTERNAL,
        resolver: ExpressionResolver = expressionResolver
    ): Boolean {
        val callResolver = expressionResolver
        return withLockOrDefer(fallback = true) {
            div2Component.actionPerformer.performAction(
                divView = this,
                resolverAtExecution(resolver, callResolver),
                action = action,
                reason = reason,
                actionUid = null,
                viewActionHandler = actionHandler
            )
        }
    }

    final override fun handleUri(uri: Uri): Unit = withLockOrDefer {
        if (actionHandler?.handleActionUrl(uri, this) != true) {
            div2Component.actionHandler.handleActionUrl(uri, this)
        }
    }

    final override fun setConfig(viewConfig: DivViewConfig): Unit =
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            this.config = viewConfig
        }

    final override fun getConfig(): DivViewConfig = config

    @AnyThread
    final override fun getDivTag(): DivDataTag = dataTag

    final override fun subscribe(
        listener: OverflowMenuSubscriber.Listener
    ): Unit = withLockOrDefer {
        overflowMenuListeners.add(listener)
    }

    final override fun clearSubscriptions(): Unit = withLockOrDefer {
        overflowMenuListeners.clear()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        handleConfigurationChange()
    }

    final override fun onConfigurationChangedOutside(newConfig: Configuration) {
        handleConfigurationChange()
    }

    private fun handleConfigurationChange() {
        dismissPendingOverflowMenus()
        div2Component.tooltipController.handleConfigurationChange(this)
    }

    final override fun dismissPendingOverflowMenus(): Unit = withLockOrDefer {
        overflowMenuListeners.forEach { it.dismiss() }
    }

    final override fun hasScrollableViewUnder(event: MotionEvent): Boolean = hasScrollableChildUnder(event)

    final override fun getCurrentStateId(): Long = rootStateController.stateId
    internal val currentRootPath get() = rootStateController.currentRootPath
    final override fun getCurrentState(): DivViewState? = rootStateController.getCurrentState()

    final override fun getView(): Div2View = this

    final override fun getExpressionResolver(): ExpressionResolver {
        return bindingController.expressionResolver
    }

    @Deprecated("Use showTooltip(tooltipId, multiple, scopeId")
    final override fun showTooltip(tooltipId: String): Unit = showTooltip(tooltipId, false, null)

    @Deprecated("Use showTooltip(tooltipId, multiple, scopeId")
    final override fun showTooltip(
        tooltipId: String,
        multiple: Boolean
    ): Unit = showTooltip(tooltipId, multiple, null)

    final override fun showTooltip(tooltipId: String, multiple: Boolean, scopeId: String?) {
        withLockOrDefer {
            div2Component.tooltipController.showTooltip(tooltipId, this, multiple, scopeId)
        }
    }

    @Deprecated("Use hideTooltip(tooltipId, scopeId)")
    final override fun hideTooltip(tooltipId: String): Unit = hideTooltip(tooltipId, null)

    final override fun hideTooltip(tooltipId: String, scopeId: String?) {
        withLockOrDefer {
            div2Component.tooltipController.hideTooltip(tooltipId, scopeId)
        }
    }

    final override fun cancelTooltips(): Unit = withLockOrDefer(::cancelTooltipsInternal)

    private fun cancelTooltipsInternal() = div2Component.tooltipController.cancelTooltips(this)

    override fun dispatchDraw(canvas: Canvas) {
        // By default ViewGroup (and FrameLayout) doesn't draw, so draw() call may be skipped.
        // dispatchDraw() is called directly in this case.
        if (drawWasSkipped) {
            histogramReporter.onDrawStarted()
        }

        super.dispatchDraw(canvas)

        if (drawWasSkipped) {
            histogramReporter.onDrawFinished()
        }
    }

    override fun drawChild(canvas: Canvas, child: View?, drawingTime: Long): Boolean {
        if (child != null && child.isVisible) {
            child.drawShadow(canvas)
        }
        return super.drawChild(canvas, child, drawingTime)
    }

    internal fun bindViewToDiv(view: View, div: Div) {
        synchronized(viewToDivBindings) {
            viewToDivBindings[view] = div
        }
    }

    internal fun takeBindingDiv(view: View): Div? = synchronized(viewToDivBindings) {
        viewToDivBindings[view]
    }

    /**
     * @return exception if setting variable failed, null otherwise.
     */
    fun setVariable(
        name: String,
        value: String
    ): VariableMutationException? = withLockOrDefer(fallback = null) {
        div2Component.variableMutationHandler.setVariable(name, value, expressionResolver, errorCollector)
    }

    fun applyTimerCommand(
        id: String,
        command: String
    ): Unit = withLockOrDefer {
        divTimerEventDispatcher?.changeState(id, command)
    }

    @JvmOverloads
    fun applyVideoCommand(
        divId: String,
        command: String,
        expressionResolver: ExpressionResolver = getExpressionResolver(),
        scopeId: String? = null,
    ): Boolean {
        val callResolver = getExpressionResolver()
        return withLockOrDefer(fallback = true) {
            val resolver = resolverAtExecution(expressionResolver, callResolver)
            div2Component.divVideoActionHandler.handleAction(this, divId, scopeId, command, resolver)
        }
    }

    private fun resolverAtExecution(passed: ExpressionResolver, atCall: ExpressionResolver): ExpressionResolver =
        if (passed === atCall) {
            getExpressionResolver()
        } else {
            passed
        }

    private fun withLockOrDefer(block: () -> Unit): Unit = withLockOrDefer(Unit, block)

    private fun <T> withLockOrDefer(fallback: T, block: () -> T): T =
        bindingDispatcher.withLock(fallback, MainThreadTaskPolicy.DEFER, block = block)

    internal fun unbindViewFromDiv(view: View): Div? = synchronized(viewToDivBindings) {
        viewToDivBindings.remove(view)
    }

    fun stateToBind(divData: DivData): DivData.State? = rootStateController.stateToBind(divData)

    var visualErrorsEnabled: Boolean
        set(value) {
            viewComponent.errorMonitor.enabled = value
        }
        get() {
            return viewComponent.errorMonitor.enabled
        }

    internal fun bulkActions(function: () -> Unit) {
        viewComponent.bulkActionHandler.bulkActions(function)
    }
}
