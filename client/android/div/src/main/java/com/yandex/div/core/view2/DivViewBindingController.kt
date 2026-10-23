package com.yandex.div.core.view2

import android.view.ViewGroup
import androidx.core.view.isEmpty
import com.yandex.div.DivDataTag
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.ObserverList
import com.yandex.div.core.downloader.DivDataChangedObserver
import com.yandex.div.core.downloader.PersistentDivDataObserver
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.local.RuntimeStoreImpl
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.timer.DivTimerEventDispatcher
import com.yandex.div.core.util.SingleTimeOnAttachCallback
import com.yandex.div.core.util.binding.AsyncBindingObserver
import com.yandex.div.core.util.binding.AsyncBindingRequest
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.binding.MainThreadTaskPolicy
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.animations.allowsTransitionsOnDataChange
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseChildren
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseMedia
import com.yandex.div.core.view2.logging.bind.ForceRebindReporter
import com.yandex.div.core.view2.logging.bind.SimpleRebindReporter
import com.yandex.div.core.view2.logging.patch.PatchEventReporter
import com.yandex.div.core.view2.reuse.ComplexRebindReporter
import com.yandex.div.core.view2.reuse.RebindTask
import com.yandex.div.core.view2.reuse.RebindTaskImpl
import com.yandex.div.core.view2.reuse.ReusableTokenList
import com.yandex.div.core.view2.reuse.isInProgress
import com.yandex.div.core.view2.reuse.reusableList
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.KLog
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.patch.DivPatchApply
import com.yandex.div.internal.util.UiThreadHandler.Companion.executeOnMainThreadBlocking
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import java.util.UUID

internal class DivViewBindingController(
    private val divView: Div2View,
    private val rootStates: DivRootStateController,
) {
    private val bindingDispatcher get() = divView.viewComponent.bindingDispatcher
    private val divDataChangedObservers = mutableListOf<DivDataChangedObserver>()
    private val persistentDivDataObservers = ObserverList<PersistentDivDataObserver>()
    private val asyncBinding = DivAsyncBindingController(divView, rootStates, ::setDataInternal)
    val expressionResolver: ExpressionResolver get() = runtimeStore.resolver
    private var oldRuntimeStore: RuntimeStore? = null
    val oldExpressionResolver: ExpressionResolver get() = oldRuntimeStore.resolver
    var runtimeStore: RuntimeStore = RuntimeStore.EMPTY
    var viewStateStore: DivViewStateStore = DivViewStateStore.EMPTY
    var inMiddleOfBind = false
    var divTimerEventDispatcher: DivTimerEventDispatcher? = null
        private set
    private var reportBindingResumedRunnable: SingleTimeOnAttachCallback? = null
    private var reportBindingFinishedRunnable: SingleTimeOnAttachCallback? = null
    private var rebindTask: RebindTask? = null
    private val bindOnAttachEnabled get() = divView.div2Component.isBindOnAttachEnabled
    private val isComplexRebindEnabled get() = divView.div2Component.isComplexRebindEnabled
    private val histogramReporter get() = divView.histogramReporter
    private val bindingProvider get() = divView.viewComponent.bindingProvider
    private val rootDivBlock get() = divView.getChildAt(0)?.divBlock

    val currentRebindReusableList: ReusableTokenList?
        get() = if (isComplexRebindEnabled) {
            rebindTask.reusableList
        } else {
            null
        }
    val complexRebindInProgress: Boolean
        get() = isComplexRebindEnabled && rebindTask.isInProgress

    var dataTag: DivDataTag = DivDataTag.INVALID
        set(value) {
            prevDataTag = field
            field = value
            bindingProvider.update(field, divData)
        }
    var prevDataTag: DivDataTag = DivDataTag.INVALID
    private var _divData: DivData? = null
        set(value) {
            field = value
            updateTimers()
            bindingProvider.update(dataTag, field)
        }
    val divData: DivData? get() = _divData
    private val RuntimeStore?.resolver get() =
        (this as? RuntimeStoreImpl)?.rootRuntime?.expressionResolver ?: ExpressionResolver.EMPTY

    fun onAttachedToWindow(): Unit = with(divView) {
        asyncBinding.onAttachedToWindow {
            reportBindingResumedRunnable?.onAttach()
            bindingDispatcher.runWithinBindingContext(BindingTaskType.DATA) {
                tryAttachVariableTriggers(divData)
            }
            rootStates.completeDeferredBindingOnAttach()
            reportBindingFinishedRunnable?.onAttach()
            divTimerEventDispatcher?.onAttach(this)
            dataComponent.layoutProviderBinder.onAttach(this)
            viewComponent.errorMonitor.onAttach()
        }
    }

    fun onDetachedFromWindow(): Unit = with(divView) {
        divTimerEventDispatcher?.onDetach(this)
        viewComponent.animatorController.onDetachedFromWindow()
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            runtimeStore.onDetachedFromWindow(this)
        }
        dataComponent.layoutProviderBinder.onDetach(this)
        viewComponent.errorMonitor.onDetach()
    }

    fun loadMedia(): Unit = asyncBinding.loadMedia()

    fun releaseMedia(): Unit = asyncBinding.releaseMedia()

    fun prepareForRecycleOrCleanup(
        newData: DivData,
        oldData: DivData?,
        newDataTag: DivDataTag?,
    ): Boolean = with(divView) {
        return bindingDispatcher.withLock(
            fallback = false,
            policy = MainThreadTaskPolicy.REPLACE,
            onBusy = ::cleanup,
        ) {
            val tag = newDataTag ?: DivDataTag(UUID.randomUUID().toString())
            val newRuntimeStore = div2Component.dataComponentStore.getOrPut(tag.id, div2Component)
                .runtimeStoreProvider.getOrCreate(newData)
            val oldRoot = getChildAt(0)?.divBlock
                ?: (divData ?: oldData)?.let { rootStates.createRootDivBlock(it) }
                ?: return@withLock false
            val canBeReplaced = DivComparator.isDivDataReplaceable(
                oldRoot,
                rootStates.createRootDivBlock(newData, newRuntimeStore),
            )
            if (canBeReplaced) {
                releaseChildren(this)
                stopLoadAndSubscriptions()
            } else {
                cleanup()
            }

            canBeReplaced
        }
    }

    private fun updateRuntimeStore(data: DivData, tag: DivDataTag): Unit = with(divView) {
        oldRuntimeStore = runtimeStore
        val oldDataComponent = dataComponent
        dataComponent = div2Component.dataComponentStore.getOrPut(tag.id, div2Component)
        runtimeStore = dataComponent.runtimeStoreProvider.getOrCreate(data)
        viewStateStore = dataComponent.viewStateStore
        if (oldRuntimeStore != runtimeStore) {
            oldDataComponent.viewConnector.detach(this)
            dataComponent.viewConnector.attach(this)
        }
        // Replay subscriptions through the current owner, after detach has finished cleaning up runtimes.
        runtimeStore.updateSubscriptions()
        dataComponent.stateManager.collectStateVariables(data, expressionResolver)
        dataTag = tag
    }

    private fun resetRuntimeStoreAndTag(): Unit = with(divView) {
        dataComponent.viewConnector.detach(this)
        oldRuntimeStore = runtimeStore
        runtimeStore = RuntimeStore.EMPTY
        dataComponent = div2Component.dataComponentStore.getOrPut("", div2Component)
        viewStateStore = DivViewStateStore.EMPTY
        dataTag = DivDataTag.INVALID
    }

    private fun tryAttachVariableTriggers(data: DivData?): Unit = with(divView) {
        if (bindOnAttachEnabled && !view.isAttachedToWindow) {
            return
        }

        val state = rootStates.findCurrentState(data) ?: return
        dataComponent.runtimeVisitor.createAndAttachRuntimes(state.div, DivStatePath.fromState(state), this)
    }

    private fun updateTimers(): Unit = with(divView) {
        val data = divData ?: return

        val newDivTimerEventDispatcher =
            dataComponent.timerEventDispatcherProvider.getOrCreate(data, expressionResolver)

        if (divTimerEventDispatcher != newDivTimerEventDispatcher) {
            divTimerEventDispatcher?.onDetach(this)
        }

        this@DivViewBindingController.divTimerEventDispatcher = newDivTimerEventDispatcher

        newDivTimerEventDispatcher?.onAttach(this)
    }
    fun setDataAsync(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>? = null,
        temporary: Boolean = true,
        onComplete: ((Boolean) -> Unit)?,
    ): Unit = asyncBinding.setDataAsync(data, oldDivData, tag, paths, temporary, onComplete)

    fun setDataAsyncWithPhases(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        observer: AsyncBindingObserver?,
    ): Unit = asyncBinding.setDataAsyncWithPhases(data, oldDivData, tag, observer)

    fun cancelBindingRequest(): Unit = asyncBinding.cancelBindingRequest()

    fun setDataOrReplacePending(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>? = null,
        temporary: Boolean = true,
    ): Boolean {
        val policy = if (data === divData) {
            MainThreadTaskPolicy.DEFER
        } else {
            MainThreadTaskPolicy.REPLACE
        }
        return bindingDispatcher.withLock(fallback = true, policy = policy) {
            setDataInternal(data, oldDivData ?: divData, tag, paths, temporary)
        }
    }

    private fun setDataInternal(
        data: DivData?,
        oldDivData: DivData?,
        tag: DivDataTag,
        paths: List<DivStatePath>? = null,
        temporary: Boolean = true,
        request: AsyncBindingRequest? = null,
    ): Boolean = with(divView) {
        bindingDispatcher.withLock(fallback = false) {
            val reporter = viewComponent.bindingReporterProvider.get(oldDivData, data)

            if (data == null) {
                reporter.onBindingFatalNoData()
                return@withLock false
            } else if (divData === data) {
                reporter.onBindingFatalSameData()
                loadMedia()
                return@withLock false
            }
            beginBinding(request)

            val oldData = divData ?: oldDivData
            rootStates.bindingPhase = null
            updateRuntimeStore(data, tag)
            dataComponent.layoutProviderBinder.release()
            val newDivBlock = rootStates.createRootDivBlock(data)
            val isDataReplaceable = DivComparator.isDivDataReplaceable(rootDivBlock, newDivBlock, reporter)

            div2Component.divViewDataPreloader.preload(data, expressionResolver)
            paths?.forEach { dataComponent.stateManager.updateStates(it, temporary) }

            val withStates = paths != null
            val result = applyBindingStrategy(
                newData = data,
                newDivBlock = newDivBlock,
                oldData = oldData,
                isReplaceable = isDataReplaceable,
                reporter = reporter,
                complexReporter = reporter,
                forceUpdate = !withStates && data.allowsTransitionsOnDataChange(expressionResolver),
                returnTrueOnRebind = withStates,
            )

            div2Component.divBinder.attachIndicators(this)
            divView.sendCreationHistograms()
            notifyBindEnded()
            return@withLock result
        }
    }

    private fun beginBinding(request: AsyncBindingRequest?) {
        asyncBinding.onBindingStarted(request)
        notifyBindStarted()
        rootStates.cancelDeferredBinding()
        histogramReporter.onRenderStarted()
    }

    private fun notifyBindStarted(): Unit = with(divView) {
        if (inMiddleOfBind) {
            logError(RuntimeException("New binding started when previous not ended!"))
        }
        inMiddleOfBind = true
        notifyDataObservers { it.onBeforeDivDataChanged() }
    }

    private fun notifyBindEnded(): Unit = with(divView) {
        inMiddleOfBind = false
        notifyDataObservers { it.onAfterDivDataChanged() }
    }

    private inline fun notifyDataObservers(crossinline notify: (PersistentDivDataObserver) -> Unit) {
        divView.flushPendingVisibilityTracking()
        persistentDivDataObservers.forEach { notify(it) }
    }

    fun applyPatchAsync(
        patch: DivPatch,
        onComplete: ((Boolean) -> Unit)?,
    ): Unit = asyncBinding.applyPatchAsync(patch, onComplete, ::applyPatch)

    fun applyPatch(patch: DivPatch): Boolean = with(divView) {
        bindingDispatcher.withLock(fallback = false) {
            val oldData: DivData = divData ?: return@withLock false
            val newDivData = div2Component.patchManager.createPatchedDivData(
                oldDivData = oldData,
                divDataTag = dataTag,
                patch = patch,
                resolver = expressionResolver,
                divView = this,
            )
            val reporter = viewComponent.patchReporterProvider.get(patch)

            if (newDivData != null && tryApplyPatch(patch, oldData, newDivData, reporter)) {
                div2Component.patchManager.removePatch(dataTag)
                divDataChangedObservers.forEach { it.onDivPatchApplied(newDivData) }
                tryAttachVariableTriggers(newDivData)
                div2Component.divBinder.attachIndicators(this)
                reporter.onPatchSuccess()
                div2Component.actionPerformer
                    .performActions(divView, expressionResolver, patch.onAppliedActions, DivActionReason.PATCH)
                return@withLock true
            }

            runMainThreadAction(BindingTaskType.DATA) {
                div2Component.actionPerformer
                    .performActions(divView, expressionResolver, patch.onFailedActions, DivActionReason.PATCH)
            }
            reporter.onPatchNoState()
            return@withLock false
        }
    }

    private fun tryApplyPatch(
        patch: DivPatch,
        oldData: DivData,
        newDivData: DivData,
        reporter: PatchEventReporter,
    ): Boolean = with(divView) {
        rootStates.cancelDeferredBinding()
        val oldRootDiv = rootStates.findCurrentState(oldData)?.div
        val rootChanges = patch.changes.find { it.id == oldRootDiv?.value()?.id } ?: run {
            val newDivBlock = rootStates.createRootDivBlock(newDivData) ?: return false
            rebind(newDivData, newDivBlock, reporter)
            return true
        }

        val items = rootChanges.items
        val newRootDiv = when {
            items.isNullOrEmpty() -> {
                KLog.e(DivPatchApply.TAG) { "Unable to patch root div because there is no div in patch." }
                return false
            }
            items.size > 1 -> {
                KLog.e(DivPatchApply.TAG) { "More than 1 div in patch for root div. The first was applied." }
                items[0]
            }
            else -> items[0]
        }

        val bindingReporter = viewComponent.bindingReporterProvider.get(newDivData, oldData)
        val newDivBlock = rootStates.createDivBlock(newRootDiv, currentRootPath)
        val isDataReplaceable = DivComparator.areDivsReplaceable(rootDivBlock, newDivBlock, bindingReporter)
        return applyBindingStrategy(newDivData, newDivBlock, oldData, isDataReplaceable, reporter, bindingReporter)
    }

    private fun applyBindingStrategy(
        newData: DivData,
        newDivBlock: DivBlock?,
        oldData: DivData?,
        isReplaceable: Boolean,
        reporter: SimpleRebindReporter,
        complexReporter: ComplexRebindReporter,
        forceUpdate: Boolean = false,
        returnTrueOnRebind: Boolean = true,
    ): Boolean = with(divView) {
        return when {
            forceUpdate -> updateNow(newData, reporter)

            isReplaceable -> {
                rebind(newData, newDivBlock, reporter)
                returnTrueOnRebind
            }
            getChildAt(0) !is ViewGroup || oldData == null -> updateNow(newData, reporter)

            isComplexRebindEnabled && complexRebind(newData, oldData, complexReporter) -> returnTrueOnRebind

            else -> updateNow(newData, reporter)
        }
    }

    private fun updateNow(data: DivData, reporter: ForceRebindReporter): Boolean = with(divView) {
        rootStates.bindingPhase = null
        val oldData = divData
        if (oldData == null) {
            histogramReporter.onBindingStarted()
        } else {
            histogramReporter.onRebindingStarted()
        }

        cleanup(removeChildren = false)

        _divData = data

        val result = rootStates.bindData(oldData, data, reporter)

        tryAttachVariableTriggers(data)

        if (oldData != null) {
            histogramReporter.onRebindingFinished()
            return result
        }

        if (!bindOnAttachEnabled) {
            histogramReporter.onBindingFinished()
            return result
        }

        histogramReporter.onBindingPaused()
        reportBindingResumedRunnable = SingleTimeOnAttachCallback(this) {
            histogramReporter.onBindingResumed()
        }
        reportBindingFinishedRunnable = SingleTimeOnAttachCallback(this) {
            histogramReporter.onBindingFinished()
        }
        return result
    }

    fun cleanup(): Unit = with(divView) {
        bindingDispatcher.cancelPendingTasks()
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            cleanup(removeChildren = true)
        }
    }

    private fun cleanup(removeChildren: Boolean): Unit = with(divView) {
        if (removeChildren) {
            cancelBindingRequest()
            rootStates.bindingPhase = null
        }
        rebindTask?.clear()?.let {
            rebindTask = null
        }
        discardVisibilityTracking(dispatchDisappearActions = removeChildren)
        cancelImageLoads()
        releaseMedia(this)
        stopLoadAndSubscriptions() // Depends on children, should be called before removing them
        if (removeChildren) {
            releaseAndRemoveChildren(this) // Removes children
            div2Component.extensionController.releaseBindings(this)
        }
        errorCollector.cleanRuntimeWarningsAndErrors()
        dataComponent.layoutProviderBinder.release()

        if (removeChildren) {
            runMainThreadAction(BindingTaskType.LIFECYCLE) {
                _divData = null
                resetRuntimeStoreAndTag()
            }
        }
    }

    internal fun stopLoadAndSubscriptions(): Unit = with(divView) {
        clearViewBindingsAndSubscriptions() // Depends on children, should be called before removing them
        divDataChangedObservers.clear()
    }

    /**
     * Observers called when DivData changed. Now it used when patch applied.
     * The list with these observers cleans when setting new DivData.
     */
    fun addDivDataChangeObserver(
        observer: DivDataChangedObserver
    ): Unit = with(divView) {
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            divDataChangedObservers.add(observer)
        }
    }

    fun removeDivDataChangeObserver(
        observer: DivDataChangedObserver
    ): Unit = with(divView) {
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
            divDataChangedObservers.remove(observer)
        }
    }

    /**
     * Observers called before and after changing div data.
     * The list with these observers doesn't cleans when setting new DivData.
     */
    internal fun addPersistentDivDataObserver(observer: PersistentDivDataObserver): Unit = with(divView) {
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
                persistentDivDataObservers.addObserver(observer)
            }
    }

    internal fun removePersistentDivDataObserver(observer: PersistentDivDataObserver): Unit = with(divView) {
        bindingDispatcher.runWithinBindingContext(BindingTaskType.LIFECYCLE) {
                persistentDivDataObservers.removeObserver(observer)
            }
    }

    private fun rebind(newData: DivData, newDivBlock: DivBlock?, reporter: SimpleRebindReporter): Unit = with(divView) {
        rootStates.bindingPhase = null
        try {
            if (isEmpty()) {
                reporter.onSimpleRebindNoChild()
                updateNow(newData, reporter)
                return
            }
            val state = rootStates.stateToBind(newData) ?: let {
                reporter.onSimpleRebindFatalNoState()
                return
            }
            newDivBlock ?: let {
                reporter.onSimpleRebindFatalNoState()
                return
            }

            runMainThreadAction(BindingTaskType.DATA) {
                histogramReporter.onRebindingStarted()
                errorCollector.cleanRuntimeWarningsAndErrors()
                _divData = newData
                dataComponent.stateManager.updateState(state.stateId, true)
                div2Component.divBinder.bind(getChildAt(0), newDivBlock, this)
                requestLayout()
                tryAttachVariableTriggers(newData)
                histogramReporter.onRebindingFinished()

                reporter.onSimpleRebindSuccess()
                rootStates.onBindingComplete(newData)
            }
        } catch (error: Exception) {
            reporter.onSimpleRebindException(error)
            updateNow(newData, reporter)
            KAssert.fail(error)
        }
    }

    private fun complexRebind(
        newData: DivData,
        oldData: DivData,
        reporter: ComplexRebindReporter
    ): Boolean = with(divView) {
        executeOnMainThreadBlocking {
            rootStates.bindingPhase = null
            val stateToBind = rootStates.stateToBind(newData) ?: let {
                reporter.onComplexRebindFatalNoState()
                return@executeOnMainThreadBlocking false
            }
            histogramReporter.onRebindingStarted()
            _divData = newData

            val task = this@DivViewBindingController.rebindTask ?: createRebindTask(reporter).also {
                this@DivViewBindingController.rebindTask = it
            }

            dataComponent.stateManager.updateState(stateToBind.stateId, false)
            val result = task.prepareAndRebind(
                oldData,
                newData,
                view.getChildAt(0) as ViewGroup,
                DivStatePath.fromState(stateToBind)
            )
            if (!result) {
                return@executeOnMainThreadBlocking false
            }
            requestLayout()

            histogramReporter.onRebindingFinished()
            rootStates.onBindingComplete(newData)
            return@executeOnMainThreadBlocking true
        }
    }

    private fun createRebindTask(reporter: ComplexRebindReporter): RebindTask = with(divView) {
        return if (isComplexRebindEnabled) {
            RebindTaskImpl(
                div2View = this,
                div2Component.divBinder,
                oldExpressionResolver,
                expressionResolver,
                reporter,
                currentRootPath,
            )
        } else {
            RebindTask.NO_OP
        }
    }
}
