package com.yandex.div.core.view2

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Canvas
import android.net.Uri
import android.os.SystemClock
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.annotation.VisibleForTesting
import androidx.core.view.ViewCompat
import androidx.core.view.doOnLayout
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.yandex.div.DivDataTag
import com.yandex.div.core.Div2Context
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivCreationTracker
import com.yandex.div.core.DivCustomContainerChildFactory
import com.yandex.div.core.DivKit
import com.yandex.div.core.DivViewConfig
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.Div2Component
import com.yandex.div.core.dagger.Div2ViewComponent
import com.yandex.div.core.downloader.DivDataChangedObserver
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.variables.VariableController
import com.yandex.div.core.images.LoadReference
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.tooltip.DivTooltipController
import com.yandex.div.core.util.Assert
import com.yandex.div.core.util.KAssert
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.animations.allowsTransitionsOnDataChange
import com.yandex.div.core.view2.animations.doOnEnd
import com.yandex.div.core.view2.divs.bindLayoutParams
import com.yandex.div.core.view2.divs.drawChildrenShadows
import com.yandex.div.core.view2.divs.saveLoadReference
import com.yandex.div.core.view2.divs.widgets.DivAnimator
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.divs.widgets.ReleaseViewVisitor
import com.yandex.div.data.VariableMutationException
import com.yandex.div.histogram.Div2ViewHistogramReporter
import com.yandex.div.histogram.HistogramCallType
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.util.DivDataUtils.INVALID_STATE_ID
import com.yandex.div.util.DivDataUtils.getInitialStateId
import com.yandex.div.util.DivViewScrollHelper.hasScrollableChildUnder
import com.yandex.div.util.immutableCopy
import com.yandex.div.view.menu.OverflowMenuSubscriber
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.div2.DivTransitionSelector
import java.lang.ref.WeakReference
import java.util.WeakHashMap

/**
 * Main entry point for building Div2s
 */
@SuppressLint("ViewConstructor")
@Mockable
class Div2View private constructor(
    context: Div2Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    private val constructorCallTime: Long,
) : FrameLayout(context, attrs, defStyleAttr), DivViewFacade {

    internal val div2Component: Div2Component = context.div2Component
    internal val viewComponent: Div2ViewComponent = div2Component.viewComponent()
        .divView(this)
        .build()

    private val bindingProvider: ViewBindingProvider = viewComponent.bindingProvider

    private val divBuilder: Div2Builder = context.div2Component.div2Builder
    private val loadReferences = mutableListOf<WeakReference<LoadReference>>()
    private val overflowMenuListeners = mutableListOf<OverflowMenuSubscriber.Listener>()
    private val divDataChangedObservers = mutableListOf<DivDataChangedObserver>()
    private val viewToDivBindings = WeakHashMap<View, Div>()
    private val propagatedAccessibilityModes = WeakHashMap<View, DivAccessibility.Mode>()
    private val bulkActionsHandler = BulkActionHandler()
    private val tooltipController: DivTooltipController
        get() = div2Component.tooltipController
    internal val releaseViewVisitor: ReleaseViewVisitor
        get() = viewComponent.releaseViewVisitor
    private var _expressionsRuntime: ExpressionsRuntime? = null
    private val variableController: VariableController?
        get() = _expressionsRuntime?.variableController

    @VisibleForTesting
    internal var stateId = INVALID_STATE_ID
    private var config = DivViewConfig.DEFAULT

    private val renderConfig = {
        DivKit.getInstance(context).component.histogramRecordConfiguration.renderConfiguration.get()
    }
    private val histogramReporter by lazy(LazyThreadSafetyMode.NONE) {
        Div2ViewHistogramReporter(
            { div2Component.histogramReporter },
            renderConfig
        )
    }
    var dataTag: DivDataTag = DivDataTag.INVALID
        internal set(value) {
            prevDataTag = field
            field = value
            bindingProvider.update(field, divData)
        }

    var prevDataTag: DivDataTag = DivDataTag.INVALID
        internal set

    var divData: DivData? = null
        internal set(value) {
            field = value
            updateExpressionsRuntime()
            bindingProvider.update(dataTag, field)
        }

    private fun updateExpressionsRuntime() {
        val data = divData ?: return
        val oldRuntime = _expressionsRuntime
        val newRuntime = div2Component.expressionsRuntimeProvider.getOrCreate(dataTag, data)
        _expressionsRuntime = newRuntime
        if (oldRuntime != newRuntime) {
            oldRuntime?.let { it.activeBinding = null }
        }
        newRuntime.activeBinding = this
    }

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

    init {
        timeCreated = DivCreationTracker.currentUptimeMillis
    }

    @JvmOverloads
    constructor(context: Div2Context,
                attrs: AttributeSet? = null,
                defStyleAttr: Int = 0
    ) : this(context, attrs, defStyleAttr, SystemClock.uptimeMillis())

    fun setData(data: DivData?, tag: DivDataTag) = setData(data, divData, tag)

    fun setData(data: DivData?, oldDivData: DivData?, tag: DivDataTag): Boolean {
        if (data == null || divData == data) {
            return false
        }

        histogramReporter.onRenderStarted()

        var oldData = divData ?: oldDivData
        if (!DivComparator.isDivDataReplaceable(oldData, data, stateId, expressionResolver)) {
            oldData = null
        }
        dataTag = tag

        data.states.forEach {
            div2Component.preLoader.preload(it.div, expressionResolver)
        }


        val result = if (oldData != null) {
            if (data.allowsTransitionsOnDataChange(expressionResolver)) {
                updateNow(data, tag)
            } else {
                rebind(data, false)
            }
            false
        } else {
            updateNow(data, tag)
        }
        div2Component.divBinder.attachIndicators()
        sendCreationHistograms()
        return result
    }

    fun setDataWithStates(
        data: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>,
        temporary: Boolean
    ): Boolean {
        if (data == null || divData == data) {
            return false
        }

        histogramReporter.onRenderStarted()

        var oldData = divData
        if (!DivComparator.isDivDataReplaceable(oldData, data, stateId, expressionResolver)) {
            oldData = null
        }
        dataTag = tag

        data.states.forEach {
            div2Component.preLoader.preload(it.div, expressionResolver)
        }
        paths.forEach { path ->
            div2Component.stateManager.updateStates(divTag.id, path, temporary)
        }
        val result = if (oldData != null) {
            rebind(data, false)
            true
        } else {
            updateNow(data, tag)
        }
        div2Component.divBinder.attachIndicators()
        sendCreationHistograms()
        return result
    }

    fun applyPatch(patch: DivPatch): Boolean {
        val oldData: DivData = divData ?: return false
        val newDivData = div2Component.patchManager.createPatchedDivData(oldData, dataTag, patch, expressionResolver)
        if (newDivData != null) {
            rebind(oldData, false)
            divData = newDivData
            div2Component.patchManager.removePatch(dataTag)
            divDataChangedObservers.forEach { it.onDivPatchApplied(newDivData) }
            return true
        }
        return false
    }

    private fun updateNow(data: DivData, tag: DivDataTag): Boolean {
        histogramReporter?.onBindingStarted()
        val oldData = divData
        cleanup(removeChildren = false)

        dataTag = tag
        divData = data

        val result = switchToDivData(oldData, data)
        histogramReporter?.onBindingFinished()
        return result
    }

    fun tryLogVisibility() {
        val state = divData?.states?.firstOrNull { it.stateId == stateId }
        state?.let { trackStateVisibility(it) }
        trackChildrenVisibility()
    }

    private fun trackStateVisibility(state: DivData.State) {
        div2Component.visibilityActionTracker.trackVisibilityActionsOf(this, view, state.div)
    }

    private fun discardStateVisibility(state: DivData.State) {
        div2Component.visibilityActionTracker.trackVisibilityActionsOf(this, null, state.div)
    }

    fun trackChildrenVisibility() {
        val visibilityActionTracker = div2Component.visibilityActionTracker
        viewToDivBindings.forEach { (view, div) ->
            if (ViewCompat.isAttachedToWindow(view)) {
                visibilityActionTracker.trackVisibilityActionsOf(this, view, div)
            }
        }
    }

    internal fun getCustomContainerChildFactory(): DivCustomContainerChildFactory {
        return div2Component.divCustomContainerChildFactory
    }

    private fun sendCreationHistograms() {
        if (timeCreated < 0) {
            return
        }
        div2Component.divCreationTracker.sendHistograms(
            constructorCallTime, timeCreated, div2Component.histogramReporter, viewCreateCallType
        )
        timeCreated = -1
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

    override fun draw(canvas: Canvas?) {
        drawWasSkipped = false
        histogramReporter.onDrawStarted()
        super.draw(canvas)
        histogramReporter.onDrawFinished()
        drawWasSkipped = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        tryLogVisibility()
    }

    override fun addLoadReference(loadReference: LoadReference, targetView: View) {
        targetView.saveLoadReference(loadReference)
        loadReferences.add(WeakReference(loadReference))
    }

    override fun cleanup() {
        cleanup(removeChildren = true)
    }

    private fun cleanup(removeChildren: Boolean) {
        if (removeChildren) {
            releaseAndRemoveChildren(this)
        }
        divData = null
        dataTag = DivDataTag.INVALID
        cancelImageLoads()
        viewToDivBindings.clear()
        propagatedAccessibilityModes.clear()
        cancelTooltips()
        clearSubscriptions()
        divDataChangedObservers.clear()
    }

    private fun cancelImageLoads() {
        loadReferences.forEach { it.get()?.cancel() }
        loadReferences.clear()
    }

    override fun switchToState(stateId: Int, temporary: Boolean) {
        if (stateId != INVALID_STATE_ID) forceSwitchToState(stateId, temporary)
    }

    override fun switchToInitialState() {
        val data = divData ?: return
        var stateId = data.getInitialStateId()
        val viewState = currentState
        if (viewState != null) {
            stateId = viewState.currentDivStateId
        }
        switchToState(stateId)
    }

    override fun switchToState(path: DivStatePath, temporary: Boolean) {
        if (stateId == path.topLevelStateId) {
            val state = divData?.states?.firstOrNull { it.stateId == path.topLevelStateId }
            bulkActionsHandler.switchState(state, path, temporary)
        } else if (path.topLevelStateId != INVALID_STATE_ID) {
            div2Component.stateManager.updateStates(dataTag.id, path, temporary)
            switchToState(path.topLevelStateId, temporary)
        }
    }

    /**
     * Switch view to states
     * @param pathList - states. See {@link com.yandex.div.core.state.DivStatePath}
     */
    fun switchToMultipleStates(pathList: List<DivStatePath>, temporary: Boolean, withAnimations: Boolean) {
        val firstPath = if (pathList.isNotEmpty()) {
            pathList[0]
        } else {
            Assert.fail("Empty path list!")
            return
        }
        pathList.find { it.topLevelStateId != firstPath.topLevelStateId }?.let {
            Assert.fail("Trying to switch different top level states in path list!")
        }
        if (stateId == firstPath.topLevelStateId) {
            val state = divData?.states?.firstOrNull { it.stateId == firstPath.topLevelStateId }
            bulkActionsHandler.switchMultipleStates(state, pathList, temporary)
        } else {
            pathList.forEach { path ->
                div2Component.stateManager.updateStates(divTag.id, path, temporary)
            }
            switchToState(firstPath.topLevelStateId)
        }
    }

    /**
     * Observers called when DivData changed. Now it used when patch applied
     */
    fun addDivDataChangeObserver(observer: DivDataChangedObserver) {
        divDataChangedObservers.add(observer)
    }

    fun removeDivDataChangeObserver(observer: DivDataChangedObserver) {
        divDataChangedObservers.remove(observer)
    }

    override fun resetToInitialState() {
        val viewState = currentState
        viewState?.reset()
        div2Component.temporaryDivStateCache.resetCard(divTag.id)

        switchToInitialState()
    }

    private fun switchToDivData(oldData: DivData?, newData: DivData): Boolean {
        val oldState = oldData?.let {
            val oldStateId = currentState?.currentDivStateId ?: it.getInitialStateId()
            oldData.states.firstOrNull { state -> state.stateId == oldStateId }
        }

        val newStateId = currentState?.currentDivStateId ?: newData.getInitialStateId()
        val newState = newData.states.firstOrNull { it.stateId == newStateId }

        this.stateId = newStateId

        if (newState != null) {
            val newStateView = buildViewAndUpdateState(newState, newStateId)
            if (newStateView != null) {
                oldState?.let { discardStateVisibility(it) }
                trackStateVisibility(newState)
                if (oldData?.allowsTransitionsOnDataChange(expressionResolver) == true ||
                    newData.allowsTransitionsOnDataChange(expressionResolver)) {
                    val transition = prepareTransition(oldData, newData, oldState?.div, newState.div)
                    if (transition != null) {
                        val currentScene = Scene.getCurrentScene(this)
                        currentScene?.setExitAction {
                            releaseAndRemoveChildren(this)
                        }
                        val newStateScene = Scene(this, newStateView)
                        TransitionManager.endTransitions(this)
                        TransitionManager.go(newStateScene, transition)
                    } else {
                        releaseAndRemoveChildren(this)
                        addView(newStateView)
                        viewComponent.errorMonitor.connect(this)
                    }
                } else {
                    releaseAndRemoveChildren(this)
                    addView(newStateView)
                    viewComponent.errorMonitor.connect(this)
                }
            }
            return true
        }

        return false
    }

    private fun forceSwitchToState(stateId: Int, temporary: Boolean): Boolean {
        this.stateId = stateId

        val currentStateId = currentState?.currentDivStateId
        val currentState = divData?.states?.firstOrNull { it.stateId == currentStateId }
        val newState = divData?.states?.firstOrNull { it.stateId == stateId }

        newState?.let {
            currentState?.let { discardStateVisibility(it) }
            trackStateVisibility(newState)
            if (DivComparator.areDivsReplaceable(
                    currentState?.div,
                    newState.div,
                    expressionResolver
                )
            ) {
                bindAndUpdateState(it, stateId, temporary)
            } else {
                releaseAndRemoveChildren(this)
                val rootDivView = buildViewAndUpdateState(it, stateId, temporary)
                addView(rootDivView)
            }
            div2Component.divBinder.attachIndicators()
        }

        return newState != null
    }

    private fun bindAndUpdateState(
        newState: DivData.State,
        stateId: Int,
        temporary: Boolean,
    ) {
        val rootView = view.getChildAt(0)
        div2Component.divBinder.bind(rootView, newState.div, this, DivStatePath.fromState(stateId))
        div2Component.stateManager.updateState(dataTag, stateId, temporary)
    }

    private fun buildViewAndUpdateState(
        newState: DivData.State,
        stateId: Int,
        isUpdateTemporary: Boolean = true
    ): View {
        div2Component.stateManager.updateState(dataTag, stateId, isUpdateTemporary)
        return divBuilder.buildView(newState.div, this, DivStatePath.fromState(newState.stateId))
    }

    private fun prepareTransition(oldData: DivData?, newData: DivData, oldDiv: Div?, newDiv: Div?): Transition? {
        if (oldDiv == newDiv) {
            return null
        }

        val transition = viewComponent.transitionBuilder.buildTransitions(
            from = oldDiv?.let { divSequenceForTransition(oldData, it) },
            to = newDiv?.let { divSequenceForTransition(newData, it) },
            resolver = expressionResolver
        )

        if (transition.transitionCount == 0) {
            return null
        }

        val dataChangeListener = div2Component.divDataChangeListener
        dataChangeListener.beforeAnimatedDataChange(this, newData)
        transition.doOnEnd {
            dataChangeListener.afterAnimatedDataChange(this, newData)
        }

        return transition
    }

    private fun divSequenceForTransition(divData: DivData?, div: Div): Sequence<Div> {
        val resolver = expressionResolver
        val selectors = ArrayDeque<DivTransitionSelector>().apply {
            addLast(divData?.transitionAnimationSelector?.evaluate(resolver) ?: DivTransitionSelector.NONE)
        }

        return div.walk()
            .onEnter { div ->
                if (div is Div.State) selectors.addLast(div.value.transitionAnimationSelector.evaluate(resolver))
                true
            }
            .onLeave { div ->
                if (div is Div.State) selectors.removeLast()
            }
            .filter { div ->
                div.value().transitionTriggers?.allowsTransitionsOnDataChange()
                    ?: selectors.lastOrNull()?.allowsTransitionsOnDataChange()
                    ?: false
            }
    }

    fun startDivAnimation() {
        if (childCount > 0) (getChildAt(0) as? DivAnimator)?.startDivAnimation()
    }

    fun stopDivAnimation() {
        if (childCount > 0) (getChildAt(0) as? DivAnimator)?.stopDivAnimation()
    }

    fun handleAction(action: DivAction) {
        handleActionWithResult(action)
    }

    fun handleActionWithResult(action: DivAction): Boolean {
        return div2Component.actionHandler.handleAction(action, this)
    }

    override fun handleUri(uri: Uri) {
        div2Component.actionHandler.handleUri(uri, this)
    }

    override fun setConfig(viewConfig: DivViewConfig) {
        this.config = viewConfig
    }

    override fun getConfig(): DivViewConfig = config

    override fun getDivTag(): DivDataTag = dataTag

    override fun subscribe(listener: OverflowMenuSubscriber.Listener) {
        overflowMenuListeners.add(listener)
    }

    override fun clearSubscriptions() {
        overflowMenuListeners.clear()
    }

    override fun onConfigurationChangedOutside(newConfig: Configuration) {
        dismissPendingOverflowMenus()
    }

    override fun dismissPendingOverflowMenus() {
        overflowMenuListeners.forEach { it.dismiss() }
    }

    override fun hasScrollableViewUnder(event: MotionEvent): Boolean = hasScrollableChildUnder(event)

    override fun getCurrentStateId() = stateId

    override fun getCurrentState(): DivViewState? {
        val data = divData ?: return null
        val currentState = div2Component.stateManager.getState(dataTag)
        return if (data.states.any { it.stateId == currentState?.currentDivStateId }) {
            currentState
        } else {
            null
        }
    }

    override fun getView() = this

    override fun getExpressionResolver(): ExpressionResolver {
        return _expressionsRuntime?.expressionResolver ?: ExpressionResolver.EMPTY
    }

    override fun showTooltip(tooltipId: String) {
        tooltipController.showTooltip(tooltipId, this)
    }

    override fun hideTooltip(tooltipId: String) {
        tooltipController.hideTooltip(tooltipId, this)
    }

    override fun cancelTooltips() {
        tooltipController.cancelTooltips(this)
    }

    override fun dispatchDraw(canvas: Canvas) {
        // By default ViewGroup (and FrameLayout) doesn't draw, so draw() call may be skipped.
        // dispatchDraw() is called directly in this case.
        if (drawWasSkipped) {
            histogramReporter.onDrawStarted()
        }
        drawChildrenShadows(canvas)

        super.dispatchDraw(canvas)
        if (drawWasSkipped) {
            histogramReporter.onDrawFinished()
        }
    }

    internal fun bindViewToDiv(view: View, div: Div) {
        viewToDivBindings[view] = div
    }

    internal fun setPropagatedAccessibilityMode(view: View, mode: DivAccessibility.Mode) {
        propagatedAccessibilityModes[view] = mode
    }

    internal fun getPropagatedAccessibilityMode(view: View): DivAccessibility.Mode? {
        return propagatedAccessibilityModes[view]
    }

    @Throws(VariableMutationException::class)
    fun setVariable(name: String, value: String) {
        val mutableVariable = variableController?.getMutableVariable(name) ?: run {
            Assert.fail("Variable '$name' not defined!")
            return
        }

        try {
            mutableVariable.set(value)
        } catch (e: VariableMutationException) {
            Assert.fail("Variable '$name' mutation failed!", e)
        }
    }

    internal fun unbindViewFromDiv(view: View): Div? = viewToDivBindings.remove(view)

    private fun rebind(newData: DivData, isAutoanimations: Boolean) {
        try {
            if (childCount == 0) {
                updateNow(newData, dataTag)
                return
            }
            histogramReporter?.onRebindingStarted()
            val state = newData.states.firstOrNull { it.stateId == stateId } ?: newData.states[0]
            val rootDivView = getChildAt(0).apply {
                bindLayoutParams(state.div.value(), expressionResolver)
            }
            divData = newData
            div2Component.divBinder.bind(rootDivView, state.div, this, DivStatePath.fromState(stateId))
            requestLayout()
            if (isAutoanimations) {
                div2Component.divStateChangeListener.onDivAnimatedStateChanged(this)
            }
            histogramReporter?.onRebindingFinished()
        } catch (error: Exception) {
            updateNow(newData, dataTag)
            KAssert.fail(error)
        }
    }

    var visualErrorsEnabled: Boolean
        set(value) {
            viewComponent.errorMonitor.enabled = value
        }
        get() {
            return viewComponent.errorMonitor.enabled
        }

    internal fun bulkActions(function: () -> Unit) {
        bulkActionsHandler.bulkActions(function)
    }

    private inner class BulkActionHandler {
        private var bulkMode = false
        private var pendingState: DivData.State? = null
        private val pendingPaths = mutableListOf<DivStatePath>()

        fun bulkActions(function: () -> Unit = {}) {
            if (bulkMode) {
                return
            }
            bulkMode = true
            function()
            runBulkActions()
            bulkMode = false
        }

        fun switchState(state: DivData.State?, path: DivStatePath, temporary: Boolean) {
            switchMultipleStates(state, listOf(path), temporary)
        }

        fun switchMultipleStates(
            state: DivData.State?,
            paths: List<DivStatePath>,
            temporary: Boolean,
        ) {
            if (pendingState != null && state != pendingState) {
                pendingPaths.clear()
            }
            pendingState = state
            pendingPaths += paths
            paths.forEach { path: DivStatePath ->
                div2Component.stateManager.updateStates(divTag.id, path, temporary)
            }

            if (!bulkMode) {
                runBulkActions()
            }
        }

        fun runBulkActions() {
            if (childCount == 0) {
                doOnLayout { bulkActions() }
                return
            }
            val currentState = pendingState ?: return
            viewComponent.stateSwitcher.switchStates(currentState, pendingPaths.immutableCopy())
            pendingState = null
            pendingPaths.clear()
        }
    }
}
