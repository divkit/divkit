package com.yandex.div.core.view2

import android.view.View
import androidx.core.view.doOnAttach
import androidx.core.view.isNotEmpty
import androidx.transition.Scene
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.yandex.div.core.DivBindingPhase
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.DivViewState
import com.yandex.div.core.util.SingleTimeOnAttachCallback
import com.yandex.div.core.util.binding.BindingTaskType
import com.yandex.div.core.util.binding.MainThreadTaskPolicy
import com.yandex.div.core.util.clearTreeAnimations
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.animations.SceneRootWatcher
import com.yandex.div.core.view2.animations.TransitionData
import com.yandex.div.core.view2.animations.allowsTransitionsOnDataChange
import com.yandex.div.core.view2.animations.doOnEnd
import com.yandex.div.core.view2.animations.toTransitionData
import com.yandex.div.core.view2.divs.divBlock
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.logging.bind.ForceRebindReporter
import com.yandex.div.internal.Assert
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.util.INVALID_STATE_ID
import com.yandex.div.util.getInitialStateId
import com.yandex.div2.Div
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivData
import com.yandex.div2.DivTransitionSelector

internal class DivRootStateController(
    private val divView: Div2View,
) {
    private val bindingDispatcher get() = divView.viewComponent.bindingDispatcher
    @Volatile
    var bindingPhase: DivBindingPhase? = null
    var stateId = DivData.INVALID_STATE_ID
    var deferredBinding: SingleTimeOnAttachCallback? = null
        private set
    private val bindOnAttachEnabled get() = divView.div2Component.isBindOnAttachEnabled

    fun completeDeferredBindingOnAttach() { deferredBinding?.onAttach() }
    fun cancelDeferredBinding() {
        if (bindingPhase == DivBindingPhase.PREPARED) {
            bindingPhase = null
        }
        deferredBinding?.cancel()
    }
    fun findCurrentState(data: DivData?): DivData.State? = data?.state()

    fun switchToState(
        stateId: Long,
        temporary: Boolean
    ): Unit = with(divView) {
        bindingDispatcher.withLock(Unit, MainThreadTaskPolicy.DEFER) {
            if (stateId != DivData.INVALID_STATE_ID) {
                cancelDeferredBinding()
                forceSwitchToState(stateId, temporary)
            }
        }
    }

    fun switchToInitialState(): Unit = with(divView) {
        val data = divData ?: return
        var stateId = data.getInitialStateId()
        val viewState = currentState
        if (viewState != null) {
            stateId = viewState.currentDivStateId
        }
        switchToState(stateId)
    }

    fun switchToState(
        path: DivStatePath,
        temporary: Boolean
    ): Unit = with(divView) {
        bindingDispatcher.withLock(Unit, MainThreadTaskPolicy.DEFER) {
            val state = divData?.states?.firstOrNull { it.stateId == path.topLevelStateId }
            viewComponent.bulkActionHandler.switchState(state, path, temporary)
        }
    }

    /**
     * Switch view to states
     * @param pathList - states. See {@link com.yandex.div.core.state.DivStatePath}
     */
    fun switchToMultipleStates(pathList: List<DivStatePath>, temporary: Boolean): Unit = with(divView) {
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
            viewComponent.bulkActionHandler.switchMultipleStates(state, pathList, temporary)
        } else {
            pathList.forEach { path ->
                dataComponent.stateManager.updateStates(path, temporary)
            }
            switchToState(firstPath.topLevelStateId)
        }
    }

    fun isInState(statePath: DivStatePath): Boolean = with(divView) {
        dataComponent.stateManager.getState(statePath.pathToLastState.toString()) == statePath.lastStateId
    }

    fun resetToInitialState(): Unit = with(divView) {
        bindingDispatcher.withLock(Unit, MainThreadTaskPolicy.DEFER) {
            val viewState = currentState
            viewState?.reset()
            dataComponent.stateManager.reset()

            switchToInitialState()
        }
    }

    fun bindData(oldData: DivData?, newData: DivData, reporter: ForceRebindReporter): Boolean = with(divView) {
        val oldState = oldData?.state()
        val newState = newData.state()

        this.stateId = newData.stateId()

        if (newState == null) {
            reporter.onForceRebindFatalNoState()
            return false
        }

        val newDivBlock = newState.toDivBlock()
        val newStateView = buildViewAsyncAndUpdateState(stateId, newDivBlock, newData)

        oldState?.let { divView.discardStateVisibility(it) }

        val allowsTransition = oldData?.allowsTransitionsOnDataChange(oldExpressionResolver) == true ||
            newData.allowsTransitionsOnDataChange(expressionResolver)

        publishRootState(oldData, newData, oldState?.div, newState, newStateView, allowsTransition)

        if (oldData != null) {
            reporter.onForceRebindSuccess()
        } else {
            reporter.onFirstBindingCompleted()
        }

        return true
    }

    private fun publishRootState(
        oldData: DivData?,
        newData: DivData,
        oldDiv: Div?,
        newState: DivData.State,
        newStateView: View,
        allowsTransition: Boolean,
    ): Unit = with(divView) {
        runMainThreadAction(BindingTaskType.DATA) {
            val preparedPhase = bindingPhase
            if (newData === divData) {
                bindingPhase = null
            }
            addNewStateViewWithTransition(oldData, newData, oldDiv, newState,
                newStateView, allowsTransition, bindBeforeViewAdded = false)
            if (newData === divData && getChildAt(0) === newStateView) {
                bindingPhase = if (preparedPhase == DivBindingPhase.APPLIED) {
                    DivBindingPhase.APPLIED
                } else {
                    DivBindingPhase.PREPARED
                }
            }
        }
    }

    private fun DivData.stateId(): Long =
        divView.currentState?.currentDivStateId ?: getInitialStateId()

    private fun DivData.state(): DivData.State? {
        val stateId = stateId()
        return states.firstOrNull { it.stateId == stateId }
    }

    internal fun rootDiv(): Div? = with(divView) {
        divData?.state()?.div
    }

    private fun forceSwitchToState(stateId: Long, temporary: Boolean): Boolean = with(divView) {
        this.stateId = stateId

        val currentStateId = currentState?.currentDivStateId
        val data = divData ?: return false

        val currentState = data.states.firstOrNull { it.stateId == currentStateId }
        val newState = (data.states.firstOrNull { it.stateId == stateId }) ?: return false

        bindingPhase = null

        currentState?.let { divView.discardStateVisibility(it) }

        val rootView: View? = getChildAt(0)
        rootView?.clearTreeAnimations()

        val newDivBlock = newState.toDivBlock()
        val isReplaceable = rootView != null && DivComparator.areDivsReplaceable(rootView.divBlock, newDivBlock)
        val newStateView = if (isReplaceable) {
            updateState(stateId, temporary)
        } else {
            buildViewAndUpdateState(stateId, temporary, newDivBlock)
        }

        addNewStateViewWithTransition(
            data, data, currentState?.div, newState, newStateView,
            data.allowsTransitionsOnDataChange(expressionResolver),
            bindBeforeViewAdded = isReplaceable,
            newDivBlock = if (isReplaceable) newDivBlock else null,
        )

        onBindingComplete(data)
        return true
    }

    private fun addNewStateViewWithTransition(
        oldData: DivData?, newData: DivData, oldDiv: Div?, newState: DivData.State,
        newStateView: View, allowsTransition: Boolean, bindBeforeViewAdded: Boolean,
        newDivBlock: DivBlock? = null,
    ): Unit = with(divView) {
        val transition = if (allowsTransition) {
            prepareTransition(oldData, newData, oldDiv, newState.div)
        } else {
            null
        }

        if (transition != null) {
            val currentScene = Scene.getCurrentScene(this)
            currentScene?.setExitAction {
                releaseAndRemoveChildren(this)
            }
        } else {
            releaseAndRemoveChildren(this)
        }

        if (bindBeforeViewAdded && newDivBlock != null) {
            div2Component.divBinder.bind(newStateView, newDivBlock, this)
        }

        if (transition != null) {
            TransitionManager.endTransitions(this)
            val newStateScene = Scene(this, newStateView)
            SceneRootWatcher.watchFor(newStateScene, transition)
            TransitionManager.go(newStateScene, transition)
        } else {
            addView(newStateView)
            viewComponent.errorMonitor.connect(this)
        }

        divView.trackStateVisibility(newState)
    }

    private fun updateState(
        stateId: Long,
        temporary: Boolean,
    ): View = with(divView) {
        val rootView = view.getChildAt(0)
        dataComponent.stateManager.updateState(stateId, temporary)
        div2Component.divBinder.attachIndicators(this)
        return rootView
    }

    private fun buildViewAndUpdateState(
        stateId: Long,
        isUpdateTemporary: Boolean = true,
        divBlock: DivBlock,
    ): View = with(divView) {
        dataComponent.stateManager.updateState(stateId, isUpdateTemporary)
        return div2Component.div2Builder.buildView(divBlock, this).also {
            div2Component.divBinder.attachIndicators(this)
        }
    }

    private fun buildViewAsyncAndUpdateState(
        stateId: Long,
        divBlock: DivBlock,
        data: DivData,
        isUpdateTemporary: Boolean = true
    ): View = with(divView) {
        dataComponent.stateManager.updateState(stateId, isUpdateTemporary)
        val view = div2Component.div2Builder.createView(divBlock.div, divBlock.expressionResolver, divBlock.path, this)
        if (bindOnAttachEnabled) {
            deferredBinding = SingleTimeOnAttachCallback(this) {
                if (data === divData) {
                    bindingPhase = null
                }
                suppressExpressionErrors {
                    div2Component.divBinder.bind(view, divBlock, this)
                }
                div2Component.divBinder.attachIndicators(this)
                onBindingComplete(data)
            }
            if (deferredBinding?.isPending == true) {
                histogramReporter.onBindingDeferred()
            }
        } else {
            histogramReporter.onBindViewsStarted()
            div2Component.divBinder.bind(view, divBlock, this)
            histogramReporter.onBindViewsFinished()
            doOnAttach {
                div2Component.divBinder.attachIndicators(this)
            }
            onBindingComplete(data)
        }
        return view
    }

    fun onBindingComplete(data: DivData): Unit = with(divView) {
        runMainThreadAction(BindingTaskType.DATA) {
            if (data === divData) {
                bindingPhase = DivBindingPhase.APPLIED
            }
        }
    }

    private fun prepareTransition(
        oldData: DivData?,
        newData: DivData,
        oldDiv: Div?,
        newDiv: Div?,
    ): Transition? = with(divView) {
        if (oldDiv === newDiv) {
            return null
        }

        if (!div2Component.animationsEnabledController.isEnabled()) {
            return null
        }

        val transition = viewComponent.transitionBuilder.buildTransitions(
            from = oldDiv?.let { transitionSequence(oldData, it, oldExpressionResolver, isIncoming = false) },
            to = newDiv?.let { transitionSequence(newData, it, expressionResolver, isIncoming = true) },
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

    private fun transitionSequence(
        divData: DivData?,
        div: Div,
        resolver: ExpressionResolver,
        isIncoming: Boolean,
    ): Sequence<TransitionData> = with(divView) {
        val selectors = ArrayDeque<DivTransitionSelector>().apply {
            addLast(divData?.transitionAnimationSelector?.evaluate(resolver) ?: DivTransitionSelector.NONE)
        }
        val inheritedChange = ArrayDeque<DivChangeTransition?>()

        return div.walk(resolver, currentRootPath) { item ->
            item.toTransitionData(isIncoming, inheritedChange.lastOrNull()) { div ->
                div.transitionTriggers?.allowsTransitionsOnDataChange()
                    ?: selectors.lastOrNull()?.allowsTransitionsOnDataChange()
                    ?: false
            }
        }.onEnter { div ->
            if (div is Div.State) {
                selectors.addLast(div.value.transitionAnimationSelector.evaluate(resolver))
                inheritedChange.addLast(null)
            } else {
                inheritedChange.addLast(div.value().transitionChange ?: inheritedChange.lastOrNull())
            }
            true
        }.onLeave { div ->
            if (div is Div.State) {
                selectors.removeLast()
            }
            inheritedChange.removeLast()
        }
    }

    val currentRootPath: DivStatePath
        get() = with(divView) {
            return@with divData?.states?.firstOrNull { it.stateId == stateId }?.let {
                DivStatePath.fromState(it)
            } ?: DivStatePath.fromState(stateId)
        }

    fun getCurrentState(): DivViewState? = with(divView) {
        val data = divData ?: return null
        val currentState = dataComponent.stateManager.state
        return if (data.states.any { it.stateId == currentState?.currentDivStateId }) {
            currentState
        } else {
            null
        }
    }

    fun createDivBlock(
        div: Div,
        path: DivStatePath,
        runtimeStore: RuntimeStore = divView.runtimeStore,
    ): DivBlock = with(divView) {
        val resolver = runtimeStore.getOrCreateRuntime(path.fullPath, div, expressionResolver).expressionResolver
        return DivBlock.create(div, resolver, path)
    }

    fun createRootDivBlock(
        data: DivData,
        runtimeStore: RuntimeStore = divView.runtimeStore,
    ): DivBlock? = with(divView) {
        data.stateToBind?.toDivBlock(runtimeStore)
    }

    private fun DivData.State.toDivBlock(runtimeStore: RuntimeStore = divView.runtimeStore) =
        createDivBlock(div, DivStatePath.fromState(this), runtimeStore)
    private val DivData.stateToBind get() = states.find { it.stateId == divView.stateId } ?: states.firstOrNull()

    fun stateToBind(divData: DivData): DivData.State? = with(divView) {
        divData.stateToBind
    }
}

