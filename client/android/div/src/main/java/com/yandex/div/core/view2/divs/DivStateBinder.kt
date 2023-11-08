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
     * @param layout layout with path 0/content/expanded/comments/expanded/comment_03/{any_state_here}.
     * @param div [DivState], corresponding to path 0/content/expanded/comments/expanded/comment_03,
     * Exact stateId will be set via [DivStateCache] or [TemporaryDivStateCache], and this class will
     * handle receiving corresponding to the state [Div] by itself.
     * @param divView papa-view, it always the same.
     * @param divStatePath path 0/content/expanded/comments/expanded, so to previous [DivStateLayout].
     */
    fun bindView(
        layout: DivStateLayout,
        div: DivState,
        divView: Div2View,
        divStatePath: DivStatePath
    ) {
        val oldDivState = layout.div
        val oldDiv = layout.activeStateDiv
        baseBinder.bindView(layout, div, oldDivState, divView)

        val resolver = divView.expressionResolver
        val cardId = divView.divTag.id
        val id = div.getId {
            errorCollectors.getOrCreate(divView.dataTag, divView.divData)
                .logError(missingValue("id", divStatePath.toString()))
        }
        val path = "$divStatePath/$id"
        val stateId = temporaryStateCache.getState(cardId, path) ?: divStateCache.getState(cardId, path)
        stateId?.let { layout.valueUpdater?.invoke(it) }
        layout.observeStateIdVariable(div, divView, divStatePath)

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
        if (layout.stateId != newState.stateId) {
            incoming = if (newStateDiv != null) {
                viewCreator.create(newStateDiv, resolver).apply { createLayoutParams() }
            } else {
                null
            }
            val transition = replaceViewsAnimated(divView, div, newState, oldState, incoming, outgoing)

            if (transition != null) {
                TransitionManager.endTransitions(layout)
                TransitionManager.beginDelayedTransition(layout, transition)
            }
            layout.releaseAndRemoveChildren(divView)
            if (incoming != null) {
                layout.addView(incoming)
                if (newStateDiv != null) {
                    viewBinder.get().bind(incoming, newStateDiv, divView, currentPath)
                }
            }
            if (outgoing != null) {
                divView.divTransitionHandler.runTransitions(root = layout, endTransitions = false)
            }
        } else if (newStateDivValue != null) {
            val areDivsReplaceable = outgoing != null && DivComparator.areDivsReplaceable(oldDiv, newStateDiv, resolver)
            incoming = if (areDivsReplaceable) {
                outgoing
            } else {
                viewCreator.create(newStateDiv, resolver).apply { createLayoutParams() }
            }
            if (!areDivsReplaceable) {
                layout.releaseAndRemoveChildren(divView)
                layout.addView(incoming)
            }
            if (incoming != null) {
                viewBinder.get().bind(incoming, newStateDiv, divView, currentPath)
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
                if (oldDiv != null) {
                    // We pass null instead of outgoing view to mark previous state as invisible
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, null, oldDiv)
                    untrackRecursively(outgoing, divView)
                }
            }
        }
        if (incoming != null && newStateDivValue != null) {
            if (newStateDivValue.visibilityAction != null || newStateDivValue.visibilityActions != null) {
                divView.bindViewToDiv(incoming, newStateDiv)
                incoming.doOnNextLayout {
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, incoming, newStateDiv)
                }
            }
        }

        val childDiv = layout.activeStateDiv
        val childDivId = childDiv?.value()?.id

        // applying div patch
        if (childDivId != null) {
            val patchView = divPatchManager.createViewsForId(divView, childDivId)?.firstOrNull()
            val patchDiv = divPatchCache.getPatchDivListById(divView.dataTag, childDivId)?.firstOrNull()
            if (patchView != null && patchDiv != null) {
                layout.releaseAndRemoveChildren(divView)
                layout.addView(patchView)
                if (patchDiv.value().hasSightActions) {
                    divView.bindViewToDiv(patchView, patchDiv)
                }
                viewBinder.get().bind(patchView, patchDiv, divView, currentPath)
            }
        }

        val actions = newState.swipeOutActions
        if (actions != null) {
            layout.swipeOutCallback = {
                divView.bulkActions {
                    actions.forEach {
                        divActionBinder.handleAction(divView, it)
                        div2Logger.logSwipedAway(divView, layout, it)
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

    private fun DivStateLayout.observeStateIdVariable(div: DivState,
                                       divView: Div2View,
                                       divStatePath: DivStatePath) {
        val stateIdVariable = div.stateIdVariable ?: return

        val subscription = variableBinder.bindVariable(
                divView,
                stateIdVariable,
                callbacks = object : TwoWayStringVariableBinder.Callbacks {
                    override fun onVariableChanged(value: String?) {
                        if (value == null) return
                        val newDivStatePath = divStatePath.append(div.getId(), value)
                        divView.switchToState(newDivStatePath, true)
                    }

                    override fun setViewStateChangeListener(valueUpdater: (String) -> Unit) {
                        this@observeStateIdVariable.valueUpdater = valueUpdater
                    }
                })
        addSubscription(subscription)
    }

    private fun untrackRecursively(outgoing: View?, divView: Div2View) {
        if (outgoing is ViewGroup) {
            // Also, unbind every child
            outgoing.children.forEach { childView: View ->
                val childDiv: Div? = divView.unbindViewFromDiv(childView)
                if (childDiv != null) {
                    divVisibilityActionTracker.trackVisibilityActionsOf(divView, null, childDiv)
                }
                untrackRecursively(childView, divView)
            }
        }
    }

    private fun replaceViewsAnimated(
        divView: Div2View,
        divState: DivState,
        incomingState: DivState.State,
        outgoingState: DivState.State?,
        incoming: View?,
        outgoing: View?
    ): Transition? {
        val outgoingDiv = outgoingState?.div
        val incomingDiv = incomingState.div
        val resolver = divView.expressionResolver
        val transition = if (divState.allowsTransitionsOnStateChange(resolver)
            && (outgoingDiv?.containsStateInnerTransitions(resolver) == true
                    || incomingDiv?.containsStateInnerTransitions(resolver) == true)) {
            val transitionBuilder = divView.viewComponent.transitionBuilder
            val transitionHolder = divView.viewComponent.stateTransitionHolder
            setupTransitions(transitionBuilder, transitionHolder, incomingState, outgoingState, resolver)
        } else {
            setupAnimation(divView, incomingState, outgoingState, incoming, outgoing)
        }
        return transition
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
        resolver: ExpressionResolver,
    ) : Transition? {
        if (incomingState == outgoingState) {
            return null
        }

        val transition = transitionBuilder.buildTransitions(
            from = outgoingState?.div?.walk(resolver)
                ?.onEnter { div -> div !is Div.State }
                ?.filter { div ->
                    div.value().transitionTriggers?.allowsTransitionsOnStateChange() ?: true
                },
            to = incomingState.div?.walk(resolver)
                ?.onEnter { div -> div !is Div.State }
                ?.filter { div ->
                    div.value().transitionTriggers?.allowsTransitionsOnStateChange() ?: true
                },
            resolver = resolver
        )

        transitionHolder.append(transition)
        return transition
    }

    private fun setupAnimation(
        divView: Div2View,
        incomingState: DivState.State,
        outgoingState: DivState.State?,
        incoming: View?,
        outgoing: View?
    ): Transition? {
        val resolver = divView.expressionResolver
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
                            .setDuration(animation.duration.evaluate(resolver).toLong())
                            .setStartDelay(animation.startDelay.evaluate(resolver).toLong())
                            .setInterpolator(animation.interpolator.evaluate(resolver).androidInterpolator)
                        )
                    }
                }
            }

            if (animationOut != null && outgoing != null) {
                val animationsOut = if (animationOut.name.evaluate(resolver) != DivAnimation.Name.SET) {
                    listOf(animationOut)
                } else {
                    animationOut.items.orEmpty()
                }

                for (animation in animationsOut) {
                    animation.toTransition(incoming = false, resolver)?.let {
                        transition.addTransition(it
                            .addTarget(outgoing)
                            .setDuration(animation.duration.evaluate(resolver).toLong())
                            .setStartDelay(animation.startDelay.evaluate(resolver).toLong())
                            .setInterpolator(animation.interpolator.evaluate(resolver).androidInterpolator)
                        )
                    }
                }
            }

            outgoing?.clearAnimation()
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
