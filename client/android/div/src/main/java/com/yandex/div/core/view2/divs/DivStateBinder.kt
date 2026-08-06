package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AnimationSet
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.isNotEmpty
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.local.DivRuntimeVisitor
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.state.DivPathUtils.append
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.clearTreeAnimations
import com.yandex.div.core.util.containsStateInnerTransitions
import com.yandex.div.core.util.getDefaultState
import com.yandex.div.core.util.toAlignmentHorizontal
import com.yandex.div.core.util.toAlignmentVertical
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivTransitionBuilder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivAnimationsEnabledController
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.animations.DivTransition
import com.yandex.div.core.view2.animations.Fade
import com.yandex.div.core.view2.animations.Scale
import com.yandex.div.core.view2.animations.SceneRootWatcher
import com.yandex.div.core.view2.animations.TransitionData
import com.yandex.div.core.view2.animations.VerticalTranslation
import com.yandex.div.core.view2.animations.allowsTransitionsOnStateChange
import com.yandex.div.core.view2.animations.toTransitionData
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.view2.state.DivStateTransitionHolder
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.WRAP_CONTENT_CONSTRAINED
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.missingValue
import com.yandex.div2.Div
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivChangeTransition
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivState
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivStateBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val viewBinder: Provider<DivBinder>,
    private val stateManager: DivStateManager,
    private val actionPerformer: DivActionPerformer,
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val errorCollectors: ErrorCollectors,
    private val variableBinder: TwoWayStringVariableBinder,
    private val runtimeVisitor: DivRuntimeVisitor,
    private val animationsEnabledController: DivAnimationsEnabledController,
) : DivViewBinder<DivBlock.State, DivStateLayout>(baseBinder) {

    override fun bindView(view: DivStateLayout, divBlock: DivBlock.State, divView: Div2View) {
        val divValue = divBlock.divValue
        val oldDivBlock = view.divBlock

        val resolver = divBlock.expressionResolver
        val path = divBlock.path
        val id = divValue.getId {
            errorCollectors.getOrCreate(divView.dataTag, divView.divData)
                .logError(missingValue("id", path.toString()))
        }
        val oldState = divValue.states.find { it.stateId == view.stateId }
            ?: divValue.getDefaultState(resolver)
        val statePath = "${path.statesString}/$id"
        val currentStateId = stateManager.getState(divValue, divView, resolver, statePath)
        val newState = divValue.states.find { it.stateId == currentStateId }
            ?: divValue.getDefaultState(resolver)
        if (oldState == null || newState == null) return

        val oldDivStateBlock = view.activeStateDivBlock
        if (oldDivBlock?.div !== divBlock.div) {
            baseBinder.bindView(view, divBlock, oldDivBlock, divView)
            view.bind(divBlock, oldDivBlock?.divValue, newState, divView)
        }

        view.bindState(divValue, oldDivBlock?.divValue, newState, oldState, oldDivStateBlock, path, resolver, id, divView)
    }

    private fun DivStateLayout.bind(
        divBlock: DivBlock.State,
        oldDiv: DivState?,
        newState: DivState.State,
        divView: Div2View,
    ) {
        val div = divBlock.divValue
        val resolver = divBlock.expressionResolver
        applyDivActions(
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.captureFocusOnAction,
            resolver,
            divView,
        )

        fixAlignment(div, oldDiv, resolver)
        observeStateIdVariable(divBlock, divView)
        bindClipChildren(div.clipToBounds, oldDiv?.clipToBounds, resolver)
        swipeOutCallback = newState.swipeOutActions?.let {
            { actionPerformer.performSwipeOutActions(divView, resolver, this, it) }
        }
    }

    private fun DivStateLayout.bindState(
        divState: DivState, oldDivState: DivState?,
        newState: DivState.State, oldState: DivState.State,
        oldDivStateBlock: DivBlock?,
        path: DivStatePath,
        resolver: ExpressionResolver,
        id: String,
        divView: Div2View,
    ) {
        val newStateDiv = newState.div
        val newStateDivValue = newStateDiv?.value()
        val currentPath = path.append(id, newState, newState.stateId)
        val newStateDivBlock = newStateDiv?.let { DivBlock.create(it, resolver, currentPath) }

        val outgoing = if (isNotEmpty()) getChildAt(0) else null
        val incoming: View?
        val reusableIncomingView = newStateDiv?.let {
            divView.currentRebindReusableList?.getUniqueView(newStateDiv)
        }
        val previousStateId = stateId

        // Publish the new state before binding children so nested set_state (e.g. video
        // fatal/buffering actions) does not re-enter this binder as another state switch and
        // recreate the child that will spawn a loop.
        activeStateDivBlock = newStateDivBlock
        currentStatePath = currentPath

        if (previousStateId != newState.stateId) {
            incoming = newStateDiv?.let { getIncomingView(reusableIncomingView, it, resolver) }

            val (sceneRoot, parentTransitions) = outgoing?.let {
                oldState.div?.value()?.transitionChange?.let { transitionChange ->
                    findTopWrapContentParent(listOf(DivTransition.Change(transitionChange)))
                }
            } ?: (this to null)

            replaceViewsAnimated(
                divState,
                newState, oldState,
                incoming, outgoing,
                resolver, oldDivStateBlock?.expressionResolver,
                parentTransitions,
                divView,
                path,
            )?.let { transition ->
                TransitionManager.endTransitions(sceneRoot)
                SceneRootWatcher.watchFor(sceneRoot, transition)
                TransitionManager.beginDelayedTransition(sceneRoot, transition)
            }
            releaseAndRemoveChildren(divView)
            incoming?.let {
                addView(incoming)
                newStateDivBlock?.let { viewBinder.get().bind(incoming, it, divView) }
            }
            if (outgoing != null) {
                divView.divTransitionHandler.runTransitions(root = sceneRoot, endTransitions = false)
            }
        } else if (newStateDivValue != null) {
            val areDivsReplaceable = outgoing != null &&
                DivComparator.areDivsReplaceable(oldDivStateBlock, newStateDivBlock)
            incoming =
                if (areDivsReplaceable) outgoing else getIncomingView(reusableIncomingView, newStateDiv, resolver)
            if (!areDivsReplaceable) {
                releaseAndRemoveChildren(divView)
                addView(incoming)
            }
            if (incoming != null && newStateDivBlock!= null) {
                viewBinder.get().bind(incoming, newStateDivBlock, divView)
            }
        } else {
            releaseAndRemoveChildren(divView)
            incoming = null
        }

        outgoing?.let {
            // I can't explain this. It's black magic.
            outgoing.startAnimation(AnimationSet(false))
            // Sometimes we receive same state and do not want to untrack visibility actions
            if (oldDivState != divState || newState != oldState) {
                divView.unbindViewFromDiv(outgoing)
                oldDivStateBlock?.let {
                    // We pass null instead of outgoing view to mark previous state as invisible
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, it.expressionResolver, null, it.div)
                    untrackRecursively(outgoing, divView, it.expressionResolver)
                }
            }
        }
        if (incoming != null && newStateDivValue != null) {
            if (newStateDivValue.visibilityAction != null || newStateDivValue.visibilityActions != null) {
                divView.bindViewToDiv(incoming, newStateDiv)
                incoming.doOnNextLayout {
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, resolver, incoming, newStateDiv)
                }
            }
        }

        if (outgoing != null) {
            runtimeVisitor.createAndAttachRuntimesToState(divView, divState, path, resolver)
        }
    }

    private fun ViewGroup.findTopWrapContentParent(
        changeTransition: List<DivTransition.Change>,
        childItems: Sequence<TransitionData>? = null,
        childHasFixedWidth: Boolean = false,
        childHasFixedHeight: Boolean = false,
    ): Pair<ViewGroup, Sequence<TransitionData>?> {
        val lp = layoutParams ?: return this to childItems
        val hasFixedWidth = childHasFixedWidth || (lp.width != WRAP_CONTENT && lp.width != WRAP_CONTENT_CONSTRAINED)
        val hasFixedHeight = childHasFixedHeight || (lp.height != WRAP_CONTENT && lp.height != WRAP_CONTENT_CONSTRAINED)
        if (hasFixedWidth && hasFixedHeight) return this to childItems

        val divBlock = divBlock ?: return this to childItems
        val id = divBlock.div.value().id ?: return this to childItems

        val item = sequenceOf(TransitionData(id, changeTransition, divBlock.expressionResolver))
        val items = childItems?.let { item + it } ?: item

        val parent = parent as? ViewGroup ?: return this to items
        return parent.findTopWrapContentParent(changeTransition, items, hasFixedWidth, hasFixedHeight)
    }

    private fun getIncomingView(reusableIncomingView: View?, div: Div, resolver: ExpressionResolver) =
        reusableIncomingView ?: viewCreator.create(div, resolver).apply { createLayoutParams() }

    private fun DivStateLayout.fixAlignment(
        div: DivState,
        oldDiv: DivState?,
        resolver: ExpressionResolver,
    ) {
        val horizontalAlignment = div.alignmentHorizontal
        val verticalAlignment = div.alignmentVertical

        if (horizontalAlignment != oldDiv?.alignmentHorizontal ||
            verticalAlignment != oldDiv?.alignmentVertical) {
            val resolvedHorizontalAlignment = horizontalAlignment?.evaluate(resolver)
                ?: extractParentContentAlignmentHorizontal(resolver)?.toAlignmentHorizontal()
            val resolvedVerticalAlignment = verticalAlignment?.evaluate(resolver)
                ?: extractParentContentAlignmentVertical(resolver)?.toAlignmentVertical()
            applyAlignment(resolvedHorizontalAlignment, resolvedVerticalAlignment)
        }
    }

    private fun View.extractParentContentAlignmentVertical(
        resolver: ExpressionResolver
    ): DivContentAlignmentVertical? {
        val divBlock = (parent as? DivHolderView<*>)?.divBlock ?: return null
        val div = divBlock.div as? Div.Container ?: return null
        return div.value.contentAlignmentVertical.evaluate(resolver)
    }

    private fun View.extractParentContentAlignmentHorizontal(
        resolver: ExpressionResolver
    ): DivContentAlignmentHorizontal? {
        val divBlock = (parent as? DivHolderView<*>)?.divBlock ?: return null
        val div = divBlock.div as? Div.Container ?: return null
        return div.value.contentAlignmentHorizontal.evaluate(resolver)
    }

    private fun DivStateLayout.observeStateIdVariable(
        divBlock: DivBlock.State,
        divView: Div2View,
    ) {
        val div = divBlock.divValue
        val stateIdVariable = div.stateIdVariable ?: return

        val subscription = variableBinder.bindVariable(
            stateIdVariable,
            divBlock.expressionResolver,
            divView,
            callbacks = object : TwoWayStringVariableBinder.Callbacks {
                override fun onVariableChanged(value: String?) {
                    if (value == null || stateId == null || value == stateId) return
                    val state = div.states.find { it.stateId == value }
                    val newDivStatePath = divBlock.path.append(div.getId(), state, value)
                    divView.switchToState(newDivStatePath, true)
                }

                override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                    stateManager.bindVariable(divView.dataTag.id, divBlock.path, valueUpdater)
                }
            },
        )
        addSubscription(subscription)
    }

    private fun untrackRecursively(outgoing: View?, divView: Div2View, resolver: ExpressionResolver) {
        if (outgoing is ViewGroup) {
            // Also, unbind every child
            outgoing.children.forEach { childView: View ->
                val childDiv: Div? = divView.unbindViewFromDiv(childView)
                if (childDiv != null) {
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, resolver, null, childDiv)
                }
                untrackRecursively(childView, divView, resolver)
            }
        }
    }

    private fun replaceViewsAnimated(
        divState: DivState,
        incomingState: DivState.State, outgoingState: DivState.State,
        incoming: View?, outgoing: View?,
        resolver: ExpressionResolver, oldResolver: ExpressionResolver?,
        parentItems: Sequence<TransitionData>?,
        divView: Div2View,
        path: DivStatePath,
    ): Transition? {
        if (!animationsEnabledController.isEnabled()) return null

        oldResolver ?: return setupAnimation(incomingState, outgoingState, incoming, outgoing, resolver, null)

        return if (divState.allowsTransitionsOnStateChange(resolver)
            && (outgoingState.div?.containsStateInnerTransitions(oldResolver, path) == true
                || incomingState.div?.containsStateInnerTransitions(resolver, path) == true)) {
            setupTransitions(
                divView.viewComponent.transitionBuilder,
                divView.viewComponent.stateTransitionHolder,
                incomingState, outgoingState,
                resolver, oldResolver,
                parentItems,
                path,
            )
        } else {
            setupAnimation(incomingState, outgoingState, incoming, outgoing, resolver, oldResolver)
        }
    }

    private fun View.createLayoutParams() {
        layoutParams = DivLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, WRAP_CONTENT)
    }

    private fun setupTransitions(
        transitionBuilder: DivTransitionBuilder,
        transitionHolder: DivStateTransitionHolder,
        incomingState: DivState.State, outgoingState: DivState.State,
        incomingResolver: ExpressionResolver, outgoingResolver: ExpressionResolver,
        parentTransitions: Sequence<TransitionData>?,
        path: DivStatePath,
    ) : Transition? {
        if (incomingState == outgoingState) {
            return null
        }

        val outgoingTransitions = outgoingState.toTransitionSequence(outgoingResolver, path, isIncoming = false)
        val from = when {
            parentTransitions == null -> outgoingTransitions
            outgoingTransitions == null -> parentTransitions
            else -> parentTransitions + outgoingTransitions
        }
        val transition = transitionBuilder.buildTransitions(
            from = from,
            to = incomingState.toTransitionSequence(incomingResolver, path, isIncoming = true)
        )

        transitionHolder.append(transition)
        return transition
    }

    private fun DivState.State.toTransitionSequence(
        resolver: ExpressionResolver,
        path: DivStatePath,
        isIncoming: Boolean
    ): Sequence<TransitionData>? {
        val inheritedChange = ArrayDeque<DivChangeTransition?>()
        return div?.walk(resolver, path) { item ->
            item.toTransitionData(isIncoming, inheritedChange.lastOrNull()) { div ->
                div.transitionTriggers?.allowsTransitionsOnStateChange() ?: true
            }
        }?.onEnter { div ->
            if (div is Div.State) return@onEnter false
            inheritedChange.addLast(div.value().transitionChange ?: inheritedChange.lastOrNull())
            true
        }?.onLeave { div ->
            if (div !is Div.State) inheritedChange.removeLast()
        }
    }

    private fun setupAnimation(
        incomingState: DivState.State, outgoingState: DivState.State,
        incoming: View?, outgoing: View?,
        resolver: ExpressionResolver, outResolver: ExpressionResolver?,
    ): Transition? {
        val animationIn = incomingState.animationIn
        val animationOut = outgoingState.animationOut
        if (animationIn != null || animationOut != null ) {
            val transition = TransitionSet()
            if (animationIn != null && incoming != null) {
                val animationsIn = if (animationIn.name.evaluate(resolver) != DivAnimation.Name.SET) {
                    listOf(animationIn)
                } else {
                    animationIn.items.orEmpty()
                }

                for (animation in animationsIn) {
                    animation.toTransition(incoming = true, resolver)?.let {
                        transition.addTransition(it
                            .addTarget(incoming)
                            .setDuration(animation.duration.evaluate(resolver))
                            .setStartDelay(animation.startDelay.evaluate(resolver))
                            .setInterpolator(animation.interpolator.evaluate(resolver).androidInterpolator)
                        )
                    }
                }
            }

            if (animationOut != null && outgoing != null && outResolver != null) {
                val animationsOut = if (animationOut.name.evaluate(outResolver) != DivAnimation.Name.SET) {
                    listOf(animationOut)
                } else {
                    animationOut.items.orEmpty()
                }

                for (animation in animationsOut) {
                    animation.toTransition(incoming = false, outResolver)?.let {
                        transition.addTransition(it
                            .addTarget(outgoing)
                            .setDuration(animation.duration.evaluate(outResolver))
                            .setStartDelay(animation.startDelay.evaluate(outResolver))
                            .setInterpolator(animation.interpolator.evaluate(outResolver).androidInterpolator)
                        )
                    }
                }
            }

            outgoing?.clearTreeAnimations()
            return transition
        }
        return null
    }
}

private fun DivAnimation.toTransition(incoming: Boolean, resolver: ExpressionResolver): Transition? {
    return when(this.name.evaluate(resolver)) {
        DivAnimation.Name.TRANSLATE -> {
            val translated = if (incoming) {
                this.startValue?.evaluate(resolver)?.translateValue()
            } else {
                this.endValue?.evaluate(resolver)?.translateValue()
            }
            val stable = if (incoming) {
                this.endValue?.evaluate(resolver).translateValue()
            } else {
                this.startValue?.evaluate(resolver).translateValue()
            }
            VerticalTranslation(
                translatedValue = translated ?: VerticalTranslation.DEFAULT_TRANSLATED_VALUE,
                stableValue = stable ?: VerticalTranslation.DEFAULT_STABLE_VALUE
            )
        }
        DivAnimation.Name.SCALE -> {
            val scaleFactor = if (incoming) {
                this.startValue?.evaluate(resolver).scaleValue()
            } else {
                this.endValue?.evaluate(resolver).scaleValue()
            }
            Scale(scaleFactor ?: 1f)
        }
        DivAnimation.Name.NO_ANIMATION -> null
        else -> {
            val alpha = if (incoming) {
                this.startValue?.evaluate(resolver).alphaValue()
            } else {
                this.endValue?.evaluate(resolver).alphaValue()
            }
            Fade(alpha = alpha ?: 1.0f).apply {
                mode = if (incoming) Visibility.MODE_IN else Visibility.MODE_OUT
            }
        }
    }
}

private fun Double?.translateValue(): Float? {
    return this?.toFloat()?.coerceIn(minimumValue = -1.0f, maximumValue = 1.0f)
}

private fun Double?.scaleValue(): Float? {
    return this?.toFloat()?.coerceAtLeast(minimumValue = 0.0f)
}

private fun Double?.alphaValue(): Float? {
    return this?.toFloat()?.coerceIn(minimumValue = 0.0f, maximumValue = 1.0f)
}
