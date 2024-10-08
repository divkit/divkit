package com.yandex.div.core.view2.divs

import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.transition.Visibility
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.expression.variables.TwoWayStringVariableBinder
import com.yandex.div.core.state.DivPathUtils.getId
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.state.TemporaryDivStateCache
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.util.containsStateInnerTransitions
import com.yandex.div.core.util.getDefaultState
import com.yandex.div.core.util.walk
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivTransitionBuilder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.DivVisibilityActionTracker
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.animations.Fade
import com.yandex.div.core.view2.animations.Scale
import com.yandex.div.core.view2.animations.VerticalTranslation
import com.yandex.div.core.view2.animations.allowsTransitionsOnStateChange
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.view2.divs.widgets.ReleaseUtils.releaseAndRemoveChildren
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.view2.state.DivStateTransitionHolder
import com.yandex.div.internal.KLog
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.missingValue
import com.yandex.div.state.DivStateCache
import com.yandex.div2.Div
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivState
import javax.inject.Inject
import javax.inject.Provider

@DivScope
internal class DivStateBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val viewCreator: DivViewCreator,
    private val viewBinder: Provider<DivBinder>,
    private val divStateCache: DivStateCache,
    private val temporaryStateCache: TemporaryDivStateCache,
    private val divActionBinder: DivActionBinder,
    private val divActionBeaconSender: DivActionBeaconSender,
    private val divPatchManager: DivPatchManager,
    private val divPatchCache: DivPatchCache,
    private val div2Logger: Div2Logger,
    private val divVisibilityActionTracker: DivVisibilityActionTracker,
    private val errorCollectors: ErrorCollectors,
    private val variableBinder: TwoWayStringVariableBinder,
) {

    /**
     * For example, to bind state to path 0/content/expanded/comments/expanded/comment_03/collapsed you should have:
     * @param context includes Div2View and ExpressionResolver for current Div.
     * @param layout layout with path 0/content/expanded/comments/expanded/comment_03/{any_state_here}.
     * @param div [DivState], corresponding to path 0/content/expanded/comments/expanded/comment_03,
     * Exact stateId will be set via [DivStateCache] or [TemporaryDivStateCache], and this class will
     * handle receiving corresponding to the state [Div] by itself.
     * @param divStatePath path 0/content/expanded/comments/expanded, so to previous [DivStateLayout].
     */
    fun bindView(
        context: BindingContext,
        layout: DivStateLayout,
        div: DivState,
        divStatePath: DivStatePath
    ) {
        val oldDivState = layout.div
        val oldDiv = layout.activeStateDiv
        val oldResolver = layout.bindingContext?.expressionResolver
        val divView = context.divView
        baseBinder.bindView(context, layout, div, oldDivState)

        val resolver = context.expressionResolver
        layout.fixAlignment(div, oldDivState, resolver)
        val cardId = divView.divTag.id
        val id = div.getId {
            errorCollectors.getOrCreate(divView.dataTag, divView.divData)
                .logError(missingValue("id", divStatePath.toString()))
        }
        val path = "$divStatePath/$id"
        var stateId = temporaryStateCache.getState(cardId, path) ?: divStateCache.getState(cardId, path)
        stateId?.let { layout.valueUpdater?.invoke(it) }
        layout.observeStateIdVariable(div, context, divStatePath, stateId)

        if (stateId == null) {
            div.stateIdVariable?.let { variableName ->
                stateId = getValueFromVariable(context, variableName)
            }
        }

        val oldState = div.states.find { it.stateId == layout.stateId }
            ?: div.getDefaultState(resolver)
        val newState = div.states.find { it.stateId == stateId }
            ?: div.getDefaultState(resolver)
        if (oldState == null || newState == null) return

        val currentPath = divStatePath.append(id, newState.stateId)
        val newStateDiv = newState.div
        val newStateDivValue = newStateDiv?.value()

        val outgoing = if (layout.childCount > 0) layout.getChildAt(0) else null
        val incoming: View?
        val reusableIncomingView = newStateDiv?.let {
            divView.currentRebindReusableList?.getUniqueViewForDiv(newStateDiv)
        }

        if (layout.stateId != newState.stateId) {
            incoming = if (newStateDiv != null) {
                reusableIncomingView ?: viewCreator.create(newStateDiv, resolver).apply { createLayoutParams() }
            } else {
                null
            }
            val transition = replaceViewsAnimated(context, div, newState, oldState, incoming, outgoing)

            if (transition != null) {
                TransitionManager.endTransitions(layout)
                TransitionManager.beginDelayedTransition(layout, transition)
            }
            layout.releaseAndRemoveChildren(divView)
            if (incoming != null) {
                layout.addView(incoming)
                if (newStateDiv != null) {
                    viewBinder.get().bind(context, incoming, newStateDiv, currentPath)
                }
            }
            if (outgoing != null) {
                divView.divTransitionHandler.runTransitions(root = layout, endTransitions = false)
            }
        } else if (newStateDivValue != null) {
            val areDivsReplaceable = outgoing != null && oldResolver != null &&
                DivComparator.areDivsReplaceable(oldDiv, newStateDiv, oldResolver, resolver)
            incoming = if (areDivsReplaceable) {
                outgoing
            } else {
                reusableIncomingView ?: viewCreator.create(newStateDiv, resolver).apply { createLayoutParams() }
            }
            if (!areDivsReplaceable) {
                layout.releaseAndRemoveChildren(divView)
                layout.addView(incoming)
            }
            if (incoming != null) {
                viewBinder.get().bind(context, incoming, newStateDiv, currentPath)
            }
        } else {
            layout.releaseAndRemoveChildren(divView)
            incoming = null
        }

        if (outgoing != null) {
            // I can't explain this. It's black magic.
            outgoing.startAnimation(AnimationSet(false))
            // Sometimes we receive same state and do not want to untrack visibility actions
            if (oldDivState != div || newState != oldState) {
                divView.unbindViewFromDiv(outgoing)
                if (oldDiv != null && oldResolver != null) {
                    // We pass null instead of outgoing view to mark previous state as invisible
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, oldResolver, null, oldDiv)
                    untrackRecursively(outgoing, divView, oldResolver)
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

        val childDiv = layout.activeStateDiv
        val childDivId = childDiv?.value()?.id

        // applying div patch
        if (childDivId != null) {
            val patchView = divPatchManager.buildViewsForId(context, childDivId)?.let { views ->
                if (views.size > 1) {
                    KLog.e(TAG) { "Unable to patch state because there is more than 1 div in the patch" }
                    null
                } else {
                    views.firstOrNull()
                }
            }
            val patchDiv = divPatchCache.getPatchDivListById(divView.dataTag, childDivId)?.firstOrNull()
            if (patchView != null && patchDiv != null) {
                layout.releaseAndRemoveChildren(divView)
                layout.addView(patchView)
                if (patchDiv.value().hasSightActions) {
                    divView.bindViewToDiv(patchView, patchDiv)
                }
                viewBinder.get().bind(context, patchView, patchDiv, currentPath)
            }
        }

        val actions = newState.swipeOutActions
        if (actions != null) {
            layout.swipeOutCallback = {
                divView.bulkActions {
                    divActionBinder.handleActions(divView, resolver, actions, DivActionReason.STATE_SWIPE_OUT) {
                        div2Logger.logSwipedAway(divView, resolver, layout, it)
                        divActionBeaconSender.sendSwipeOutActionBeacon(it, resolver)
                    }
                }
            }
        } else {
            layout.swipeOutCallback = null
        }


        layout.activeStateDiv = newStateDiv
        layout.path = currentPath
    }

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

    private fun getValueFromVariable(context: BindingContext, variableName: String): String? {
        val variableController = context.runtimeStore?.getRuntimeWithOrNull(context.expressionResolver)?.variableController
            ?: context.divView.expressionsRuntime?.variableController ?: return null

        return variableController.getMutableVariable(variableName)?.getValue()?.toString()
    }

    private fun DivStateLayout.observeStateIdVariable(
        div: DivState,
        bindingContext: BindingContext,
        divStatePath: DivStatePath,
        currentStateId: String?
    ) {
        val stateIdVariable = div.stateIdVariable ?: return

        val subscription = variableBinder.bindVariable(
                bindingContext,
                stateIdVariable,
                callbacks = object : TwoWayStringVariableBinder.Callbacks {
                    override fun onVariableChanged(value: String?) {
                        if (value == null || value == currentStateId) return
                        val newDivStatePath = divStatePath.append(div.getId(), value)
                        bindingContext.divView.switchToState(newDivStatePath, true)
                    }

                    override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                        this@observeStateIdVariable.valueUpdater = valueUpdater
                    }
                },
                path = divStatePath
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
        context: BindingContext,
        divState: DivState,
        incomingState: DivState.State,
        outgoingState: DivState.State?,
        incoming: View?,
        outgoing: View?
    ): Transition? {
        val oldResolver = outgoing?.bindingContext?.expressionResolver
            ?: return setupAnimation(context, incomingState, outgoingState, incoming, outgoing)

        val resolver = context.expressionResolver
        return if (divState.allowsTransitionsOnStateChange(resolver)
            && (outgoingState?.div?.containsStateInnerTransitions(oldResolver) == true
                    || incomingState.div?.containsStateInnerTransitions(resolver) == true)) {
            setupTransitions(
                context.divView.viewComponent.transitionBuilder,
                context.divView.viewComponent.stateTransitionHolder,
                incomingState,
                outgoingState,
                resolver,
                oldResolver
            )
        } else {
            setupAnimation(context, incomingState, outgoingState, incoming, outgoing)
        }
    }

    private fun View.createLayoutParams() {
        layoutParams = DivLayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun setupTransitions(
        transitionBuilder: DivTransitionBuilder,
        transitionHolder: DivStateTransitionHolder,
        incomingState: DivState.State,
        outgoingState: DivState.State?,
        incomingResolver: ExpressionResolver,
        outgoingResolver: ExpressionResolver,
    ) : Transition? {
        if (incomingState == outgoingState) {
            return null
        }

        val transition = transitionBuilder.buildTransitions(
            from = outgoingState?.div?.walk(outgoingResolver)
                ?.onEnter { div -> div !is Div.State }
                ?.filter { item ->
                    item.div.value().transitionTriggers?.allowsTransitionsOnStateChange() ?: true
                },
            to = incomingState.div?.walk(incomingResolver)
                ?.onEnter { div -> div !is Div.State }
                ?.filter { item ->
                    item.div.value().transitionTriggers?.allowsTransitionsOnStateChange() ?: true
                },
            fromResolver = outgoingResolver,
            toResolver = incomingResolver
        )

        transitionHolder.append(transition)
        return transition
    }

    private fun setupAnimation(
        context: BindingContext,
        incomingState: DivState.State,
        outgoingState: DivState.State?,
        incoming: View?,
        outgoing: View?
    ): Transition? {
        val resolver = context.expressionResolver
        val animationIn = incomingState.animationIn
        val animationOut = outgoingState?.animationOut
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

            val outResolver = outgoing?.bindingContext?.expressionResolver
            if (animationOut != null && outResolver != null) {
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

            outgoing?.clearAnimation()
            return transition
        }
        return null
    }

    private companion object {
        const val TAG = "DivStateBinder"
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
