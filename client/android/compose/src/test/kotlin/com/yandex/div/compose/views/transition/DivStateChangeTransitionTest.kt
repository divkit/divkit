package com.yandex.div.compose.views.transition

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.setContent
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div2.DivAppearanceSetTransition
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivDimension
import com.yandex.div2.DivExtension
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivSlideTransition
import com.yandex.div2.DivTransitionSelector
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivStateChangeTransitionTest : StateChangeTransitionTestBase() {
    @Test
    fun `outgoing node is retained until fade finishes`() {
        val target = mutableStateOf(false)
        val outgoing = text(
            text = OUTGOING,
            id = OUTGOING_ID,
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
        ).value()
        val incoming = text(
            text = INCOMING,
            id = INCOMING_ID,
            transitionIn = fade(duration = TRANSITION_DURATION_MS),
        ).value()

        setContent(target, outgoing, incoming)
        composeRule.runOnIdle { target.value = true }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.onNodeWithContentDescription(OUTGOING).assertExists()
        composeRule.onNodeWithContentDescription(INCOMING).assertExists()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 2)
        composeRule.onNodeWithContentDescription(OUTGOING).assertExists()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.onNodeWithContentDescription(OUTGOING).assertDoesNotExist()
        composeRule.onNodeWithContentDescription(INCOMING).assertExists()
    }

    @Test
    fun `fade alpha uses absolute transition endpoints`() {
        val data = text(
            text = INCOMING,
            id = INCOMING_ID,
            alpha = constant(BASE_ALPHA.toDouble()),
            transitionIn = fade(
                duration = TRANSITION_DURATION_MS,
                alpha = TRANSITION_ALPHA.toDouble(),
            ),
        ).value()
        var appearanceAlpha: Float? = null
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                val visibleState = remember {
                    MutableTransitionState(false).apply { targetState = true }
                }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None,
                ) {
                    CompositionLocalProvider(
                        LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                            scope = StateChangeTransitionScope(
                                enterScope = this,
                                exitScope = this,
                                enteringIds = setOf(INCOMING_ID),
                                exitingIds = emptySet(),
                            )
                        )
                    ) {
                        val transition = Modifier.applyStateChangeTransition(
                            data,
                            DivVisibility.VISIBLE,
                        )
                        SideEffect { appearanceAlpha = transition.appearanceAlpha }
                        Box(transition.modifier)
                    }
                }
            }
        }

        composeRule.waitForIdle()
        assertEquals(TRANSITION_ALPHA, appearanceAlpha)

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        assertEquals(BASE_ALPHA, appearanceAlpha)
    }

    @Test
    fun `set combines fade scale and slide transitions`() {
        val data = text(
            text = INCOMING,
            id = INCOMING_ID,
            transitionIn = DivAppearanceTransition.Set(
                DivAppearanceSetTransition(
                    items = listOf(
                        fade(
                            duration = TRANSITION_DURATION_MS,
                            alpha = TRANSITION_ALPHA.toDouble(),
                        ),
                        DivAppearanceTransition.Scale(
                            DivScaleTransition(
                                duration = constant(TRANSITION_DURATION_MS),
                                pivotX = constant(TRANSITION_PIVOT_X.toDouble()),
                                pivotY = constant(TRANSITION_PIVOT_Y.toDouble()),
                                scale = constant(TRANSITION_SCALE.toDouble()),
                            )
                        ),
                        DivAppearanceTransition.Slide(
                            DivSlideTransition(
                                distance = DivDimension(value = constant(SLIDE_DISTANCE_DP.toDouble())),
                                duration = constant(TRANSITION_DURATION_MS),
                                edge = constant(DivSlideTransition.Edge.RIGHT),
                            )
                        ),
                    )
                )
            ),
        ).value()
        val transitionState = StateChangeTransitionState()
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                val visibleState = remember {
                    MutableTransitionState(false).apply { targetState = true }
                }
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None,
                ) {
                    CompositionLocalProvider(
                        LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                            scope = StateChangeTransitionScope(
                                enterScope = this,
                                exitScope = this,
                                enteringIds = setOf(INCOMING_ID),
                                exitingIds = emptySet(),
                                transitionState = transitionState,
                            )
                        )
                    ) {
                        val transition = Modifier.applyStateChangeTransition(
                            data,
                            DivVisibility.VISIBLE,
                        )
                        Box(transition.modifier)
                    }
                }
            }
        }

        val slideDistance = with(composeRule.density) { SLIDE_DISTANCE_DP.dp.toPx() }
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            transitionState.captureExitValues(setOf(INCOMING_ID))
            assertEquals(
                StateChangeVisualState(
                    alpha = TRANSITION_ALPHA,
                    scale = TRANSITION_SCALE,
                    pivotX = TRANSITION_PIVOT_X,
                    pivotY = TRANSITION_PIVOT_Y,
                    translationX = slideDistance,
                ),
                transitionState.getExitValues(INCOMING_ID),
            )
        }

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.runOnIdle {
            transitionState.captureExitValues(setOf(INCOMING_ID))
            assertEquals(
                StateChangeVisualState(
                    alpha = 1f,
                ),
                transitionState.getExitValues(INCOMING_ID),
            )
        }
    }

    @Test
    fun `scale transition does not wait for slide geometry`() {
        val animation = StateChangeAnimation(
            appearance = AnimatedAppearance(
                scales = listOf(
                    AnimatedScale(
                        value = TRANSITION_SCALE,
                        pivotX = TRANSITION_PIVOT_X,
                        pivotY = TRANSITION_PIVOT_Y,
                        timing = AnimatedTiming.NotStarted,
                        orderingDurationMillis = TRANSITION_DURATION_MS,
                    )
                )
            ),
            visualState = StateChangeVisualState(alpha = 1f),
            slideGeometry = null,
            exitLayout = null,
            isExiting = false,
        )

        assertEquals(1f, animation.resolveAppearanceAlpha(capturesOverlay = false))
    }

    @Test
    fun `finished explicit distance slide does not wait for geometry`() {
        val animation = StateChangeAnimation(
            appearance = AnimatedAppearance(
                slides = listOf(
                    AnimatedSlide(
                        progress = 1f,
                        edge = DivSlideTransition.Edge.RIGHT,
                        distance = 10f,
                        exitTranslationX = 0f,
                        exitTranslationY = 0f,
                        timing = AnimatedTiming(
                            phase = 1f,
                            startTimeMillis = 0L,
                            endTimeMillis = TRANSITION_DURATION_MS,
                            hasAnimator = true,
                        ),
                        orderingDurationMillis = TRANSITION_DURATION_MS,
                    )
                )
            ),
            visualState = StateChangeVisualState(alpha = 1f),
            slideGeometry = null,
            exitLayout = null,
            isExiting = false,
        )

        assertEquals(1f, animation.resolveAppearanceAlpha(capturesOverlay = false))
    }

    @Test
    fun `default slide distance reaches every scene edge`() {
        val sceneSize = IntSize(width = 300, height = 400)
        val bounds = ElementBounds(left = 40f, top = 60f, width = 100f, height = 80f)

        assertEquals(
            Offset(-140f, 0f),
            AnimatedSlide(
                progress = 1f,
                edge = DivSlideTransition.Edge.LEFT,
                distance = null,
                exitTranslationX = 0f,
                exitTranslationY = 0f,
            ).resolveTranslation(sceneSize, bounds, isExiting = false),
        )
        assertEquals(
            Offset(0f, -140f),
            AnimatedSlide(
                progress = 1f,
                edge = DivSlideTransition.Edge.TOP,
                distance = null,
                exitTranslationX = 0f,
                exitTranslationY = 0f,
            ).resolveTranslation(sceneSize, bounds, isExiting = false),
        )
        assertEquals(
            Offset(260f, 0f),
            AnimatedSlide(
                progress = 1f,
                edge = DivSlideTransition.Edge.RIGHT,
                distance = null,
                exitTranslationX = 0f,
                exitTranslationY = 0f,
            ).resolveTranslation(sceneSize, bounds, isExiting = false),
        )
        assertEquals(
            Offset(0f, 340f),
            AnimatedSlide(
                progress = 1f,
                edge = DivSlideTransition.Edge.BOTTOM,
                distance = null,
                exitTranslationX = 0f,
                exitTranslationY = 0f,
            ).resolveTranslation(sceneSize, bounds, isExiting = false),
        )
        assertEquals(
            Offset(140f, 60f),
            AnimatedSlide(
                progress = 1f,
                edge = DivSlideTransition.Edge.RIGHT,
                distance = 100f,
                exitTranslationX = 40f,
                exitTranslationY = 60f,
            ).resolveTranslation(sceneSize, bounds, isExiting = true),
        )
    }

    @Test
    fun `explicit trigger without state change disables fade`() {
        val target = mutableStateOf(false)
        val outgoing = text(
            text = OUTGOING,
            id = OUTGOING_ID,
            transitionOut = fade(duration = TRANSITION_DURATION_MS),
            transitionTriggers = listOf(DivTransitionTrigger.DATA_CHANGE),
        ).value()

        setContent(target, outgoing, text(text = INCOMING).value())
        composeRule.runOnIdle { target.value = true }
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.onNodeWithContentDescription(OUTGOING).assertDoesNotExist()
        composeRule.onNodeWithContentDescription(INCOMING).assertExists()
    }

    @Test
    fun `default selector enables state transition`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val handler = TrackingExtensionHandler(composedIds)
        val state = renderStateChange(
            selector = null,
            outgoing = text(
                text = OUTGOING,
                id = OUTGOING_ID,
                extensions = listOf(extension),
                transitionOut = DivAppearanceTransition.Scale(
                    DivScaleTransition(duration = constant(TRANSITION_DURATION_MS))
                ),
            ),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to handler
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS / 2)
        val layer = composeRule.runOnIdle {
            assertEquals(emptySet(), composedIds)
            val snapshot = handler.transitionStateOf(OUTGOING_ID).snapshots.exiting.single()
            assertEquals(OUTGOING_ID, snapshot.id)
            assertFalse(snapshot.layer.isReleased)
            assertTrue(snapshot.layer.scaleX in 0.01f..0.99f)
            snapshot.layer
        }

        composeRule.mainClock.advanceTimeBy(TRANSITION_DURATION_MS)
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.runOnIdle { assertTrue(layer.isReleased) }
    }

    @Test
    fun `appearance transition requires element id`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                extensions = listOf(extension),
                transitionOut = DivAppearanceTransition.Scale(DivScaleTransition()),
            ),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to TrackingExtensionHandler(composedIds)
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.waitForIdle()
        assertEquals(emptySet(), composedIds)
        composeRule.onNodeWithText(INCOMING).assertExists()
    }

    @Test
    fun `same element id does not trigger appearance transition`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            outgoing = text(
                text = OUTGOING,
                id = PERSISTENT_ID,
                extensions = listOf(extension),
                transitionOut = DivAppearanceTransition.Scale(DivScaleTransition()),
            ),
            incoming = text(
                text = constant(INCOMING),
                id = PERSISTENT_ID,
                transitionIn = DivAppearanceTransition.Scale(DivScaleTransition()),
            ),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to TrackingExtensionHandler(composedIds)
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.waitForIdle()
        assertEquals(emptySet(), composedIds)
        composeRule.onNodeWithText(INCOMING).assertExists()
    }

    @Test
    fun `zero distance left slide does not retain outgoing content`() {
        val data = text(
            text = OUTGOING,
            id = OUTGOING_ID,
            transitionOut = DivAppearanceTransition.Slide(
                DivSlideTransition(
                    distance = DivDimension(value = constant(0.0)),
                    duration = constant(TRANSITION_DURATION_MS),
                    edge = constant(DivSlideTransition.Edge.LEFT),
                )
            ),
        ).value()
        val transitionState = StateChangeTransitionState().apply {
            updateCurrent(OUTGOING_ID, StateChangeVisualState(alpha = 1f))
            captureExitValues(setOf(OUTGOING_ID))
        }
        lateinit var exitVisibility: MutableTransitionState<Boolean>
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            CompositionLocalProvider(LocalComponent provides localComponent) {
                exitVisibility = remember {
                    MutableTransitionState(true).apply { targetState = false }
                }
                AnimatedVisibility(
                    visibleState = exitVisibility,
                    enter = EnterTransition.None,
                    exit = ExitTransition.None,
                ) {
                    CompositionLocalProvider(
                        LocalStateChangeTransitionContext provides StateChangeTransitionContext(
                            scope = StateChangeTransitionScope(
                                enterScope = this,
                                exitScope = this,
                                enteringIds = emptySet(),
                                exitingIds = setOf(OUTGOING_ID),
                                transitionState = transitionState,
                            )
                        )
                    ) {
                        Box(
                            Modifier.applyStateChangeTransition(
                                data,
                                DivVisibility.VISIBLE,
                            ).modifier
                        )
                    }
                }
            }
        }

        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()

        composeRule.runOnIdle {
            assertTrue(exitVisibility.isIdle)
            assertFalse(exitVisibility.currentState)
        }
    }

    @Test
    fun `globally disabled animations disable state transition`() {
        val composedIds = mutableSetOf<String>()
        val extension = DivExtension(id = TRACKING_EXTENSION_ID)
        val state = renderStateChange(
            selector = DivTransitionSelector.STATE_CHANGE,
            animationsEnabled = false,
            outgoing = text(
                text = OUTGOING,
                id = OUTGOING_ID,
                extensions = listOf(extension),
                transitionOut = fade(duration = TRANSITION_DURATION_MS),
            ),
            extensionHandlers = mapOf(
                TRACKING_EXTENSION_ID to TrackingExtensionHandler(composedIds)
            ),
        )

        state.set(INCOMING)
        composeRule.waitForIdle()
        composeRule.mainClock.advanceTimeByFrame()

        composeRule.waitForIdle()
        assertEquals(emptySet(), composedIds)
        composeRule.onNodeWithText(INCOMING).assertExists()
    }
}
