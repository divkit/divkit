package com.yandex.div.compose.views.transition

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.divConfiguration
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.mockLocalComponent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivBase
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnitValue
import com.yandex.div2.DivState
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivVisibility
import com.yandex.div2.DivWrapContentSize
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class StateChangeTransitionTestBase {
    @get:Rule
    val composeRule = createComposeRule()

    internal val reporter = TestReporter()
    internal val localComponent = mockLocalComponent(reporter = reporter)

    protected fun setContent(
        target: MutableState<Boolean>,
        outgoing: DivBase,
        incoming: DivBase,
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                AnimatedContent(
                    targetState = target.value,
                    transitionSpec = {
                        (EnterTransition.None togetherWith
                            ExitTransition.KeepUntilTransitionsFinished).using(null)
                    },
                    label = "Test state content",
                ) { isIncoming ->
                    CompositionLocalProvider(
                        LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                            scope = StateChangeTransitionScope(
                                enterScope = this,
                                exitScope = this,
                                enteringIds = setOf(INCOMING_ID),
                                exitingIds = setOf(OUTGOING_ID),
                            )
                        )
                    ) {
                        val data = if (isIncoming) incoming else outgoing
                        val label = if (isIncoming) INCOMING else OUTGOING
                        Box(
                            Modifier
                                .applyStateChangeTransition(data, DivVisibility.VISIBLE)
                                .modifier
                                .semantics { contentDescription = label }
                        )
                    }
                }
            }
        }
    }

    protected fun renderStateChange(
        selector: DivTransitionSelector?,
        animationsEnabled: Boolean = true,
        customViewFactories: Map<String, DivCustomViewFactory> = emptyMap(),
        outgoing: Div = text(
            text = OUTGOING,
            id = OUTGOING_ID,
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
        ),
        incoming: Div = text(
            text = INCOMING,
            id = INCOMING_ID,
            transitionIn = fade(duration = TRANSITION_DURATION_MS),
        ),
        extraVariables: List<Variable> = emptyList(),
        extensionHandlers: Map<String, DivExtensionHandler> = emptyMap(),
        stateWidth: DivSize = matchParent(),
        modifier: Modifier = Modifier,
    ): Variable.StringVariable {
        return renderStates(
            selector = selector,
            animationsEnabled = animationsEnabled,
            states = listOf(
                state(
                    stateId = OUTGOING,
                    div = outgoing,
                ),
                state(
                    stateId = INCOMING,
                    div = incoming,
                ),
            ),
            customViewFactories = customViewFactories,
            extraVariables = extraVariables,
            extensionHandlers = extensionHandlers,
            stateWidth = stateWidth,
            modifier = modifier,
        )
    }

    protected fun renderStates(
        states: List<DivState.State>,
        selector: DivTransitionSelector? = DivTransitionSelector.STATE_CHANGE,
        animationsEnabled: Boolean = true,
        customViewFactories: Map<String, DivCustomViewFactory> = emptyMap(),
        extraVariables: List<Variable> = emptyList(),
        extensionHandlers: Map<String, DivExtensionHandler> = emptyMap(),
        stateWidth: DivSize = matchParent(),
        modifier: Modifier = Modifier,
    ): Variable.StringVariable {
        val state = Variable.StringVariable(STATE_VARIABLE, states.first().stateId)
        val variableController = DivVariableController().apply {
            declare(state)
            extraVariables.forEach { declare(it) }
        }
        val configuration = divConfiguration {
            reporter = this@StateChangeTransitionTestBase.reporter
            animationsEnabledProvider = object : DivAnimationsEnabledProvider {
                override val animationsEnabled = MutableStateFlow(animationsEnabled)
            }
            this.customViewFactories = customViewFactories
            this.variableController = variableController
            this.extensionHandlers = extensionHandlers
        }
        val divState = state(
            id = "state",
            stateIdVariable = STATE_VARIABLE,
            states = states,
            transitionAnimationSelector = selector ?: DivTransitionSelector.STATE_CHANGE,
            width = stateWidth,
        )

        composeRule.mainClock.autoAdvance = false
        composeRule.setContentWithDivContext(configuration) {
            DivView(data = data(divState), modifier = modifier)
        }
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        return state
    }

    protected fun fade(
        duration: Long,
        alpha: Double = 0.0,
    ): DivAppearanceTransition {
        return DivAppearanceTransition.Fade(
            DivFadeTransition(
                alpha = constant(alpha),
                duration = constant(duration),
            )
        )
    }

    protected fun linearFade(
        duration: Long,
        alpha: Double,
        startDelay: Long = 0L,
    ): DivAppearanceTransition {
        return DivAppearanceTransition.Fade(
            DivFadeTransition(
                alpha = constant(alpha),
                duration = constant(duration),
                startDelay = constant(startDelay),
                interpolator = constant(DivAnimationInterpolator.LINEAR),
            )
        )
    }

    protected fun wrapContentUpTo(maxSizeDp: Int): DivSize = DivSize.WrapContent(
        DivWrapContentSize(maxSize = DivSizeUnitValue(value = constant(maxSizeDp.toLong())))
    )

    protected fun assertStateWidth(expectedWidthDp: Int) {
        val actual = composeRule.onNodeWithTag("state").fetchSemanticsNode().size.width
        val expected = with(composeRule.density) { expectedWidthDp.dp.roundToPx() }
        assertEquals(expected, actual)
    }

    protected class TrackingExtensionHandler(
        private val composedIds: MutableSet<String>,
    ) : DivExtensionHandler {
        private val compositionCountById = mutableMapOf<String, Int>()
        private val lifetimeCompositionCountById = mutableMapOf<String, Int>()
        private val transitionStates = mutableMapOf<String, StateChangeTransitionState>()

        fun compositionsOf(id: String): Int = lifetimeCompositionCountById.getOrDefault(id, 0)

        internal fun transitionStateOf(id: String): StateChangeTransitionState = transitionStates.getValue(id)

        @Composable
        override fun Content(
            modifier: Modifier,
            environment: DivExtensionEnvironment,
            content: @Composable (modifier: Modifier) -> Unit,
        ) {
            val id = environment.data.value().id.orEmpty()
            val transitionState = LocalStateChangeTransitionContext.current.scope?.transitionState
            SideEffect {
                if (transitionState != null) transitionStates[id] = transitionState
            }
            DisposableEffect(id) {
                compositionCountById[id] = compositionCountById.getOrDefault(id, 0) + 1
                lifetimeCompositionCountById[id] = lifetimeCompositionCountById.getOrDefault(id, 0) + 1
                composedIds += id
                onDispose {
                    val remainingCount = compositionCountById.getValue(id) - 1
                    if (remainingCount == 0) {
                        compositionCountById -= id
                        composedIds -= id
                    } else {
                        compositionCountById[id] = remainingCount
                    }
                }
            }
            content(modifier)
        }
    }

    protected companion object {
        const val OUTGOING = "outgoing"
        const val INCOMING = "incoming"
        const val MIDDLE = "middle"
        const val EMPTY = "empty"
        const val STATE_VARIABLE = "current_state"
        const val NESTED_STATE_VARIABLE = "nested_state"
        const val CONDITION_VARIABLE = "condition"
        const val COUNTER_VARIABLE = "counter"
        const val CLICKED_VARIABLE = "clicked"
        const val OUTGOING_CLICKED_VARIABLE = "outgoing_clicked"
        const val VISIBILITY_VARIABLE = "visibility"
        const val LOCAL_VISIBILITY_VARIABLE = "local_visibility"
        const val DESCENDANT = "descendant"
        const val OUTGOING_ID = "outgoing_id"
        const val INCOMING_ID = "incoming_id"
        const val MIDDLE_ID = "middle_id"
        const val PERSISTENT_ID = "persistent_id"
        const val NESTED_TRANSITION_ID = "nested_transition"
        const val TRACKING_EXTENSION_ID = "tracking"
        const val TRANSITION_DURATION_MS = 1_000L
        const val BASE_ALPHA = 0.5f
        const val TRANSITION_ALPHA = 0.8f
        const val TRANSITION_SCALE = 0.4f
        const val TRANSITION_PIVOT_X = 0.25f
        const val TRANSITION_PIVOT_Y = 0.75f
        const val SLIDE_DISTANCE_DP = 12
        const val CONTENT_WIDTH_DP = 40
        const val OUTGOING_WIDTH_DP = 80
        const val MAX_STATE_WIDTH_DP = 100
    }
}
