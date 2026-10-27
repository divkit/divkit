package com.yandex.div.compose.animation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivView
import com.yandex.div.compose.TestExternalActionHandler
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.actionData
import com.yandex.div.compose.actions.DivActionSource
import com.yandex.div.compose.setContent
import com.yandex.div.compose.setContentWithDivContext
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.animatorStartAction
import com.yandex.div.test.data.animatorStopAction
import com.yandex.div.test.data.container
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.data
import com.yandex.div.test.data.doubleExpression
import com.yandex.div.test.data.expression
import com.yandex.div.test.data.fixedCount
import com.yandex.div.test.data.infinityCount
import com.yandex.div.test.data.numberAnimator
import com.yandex.div.test.data.text
import com.yandex.div.test.data.trigger
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivAnimationDirection
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivAnimator
import com.yandex.div2.DivColorAnimator
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivViewWithAnimatorsTest {

    private val motionDurationScale = TestMotionDurationScale()

    @get:Rule
    val rule = createComposeRule(effectContext = motionDurationScale)

    private val integerValue = Variable.IntegerVariable(VALUE, 0L)
    private val doubleValue = Variable.DoubleVariable(DOUBLE_VALUE, 0.0)
    private val stringValue = Variable.StringVariable(STRING_VALUE, "initial")
    private val variableController = DivVariableController().apply {
        declare(integerValue)
        declare(doubleValue)
        declare(stringValue)
    }
    private val reporter = TestReporter()
    private val externalActionHandler = TestExternalActionHandler()
    private val configuration = configuration()

    @BeforeTest
    fun setUp() {
        motionDurationScale.scaleFactor = 1f
        rule.mainClock.autoAdvance = false
    }

    @Test
    fun `number animator updates integer variable and runs end actions`() {
        setContent(target(
            animator = numberAnimator(
                duration = 1024,
                endActions = listOf(action(url = END_URL)),
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(512)

        assertEquals(50L, integerValue.getValue())

        advanceAnimationBy(512)

        assertEquals(100L, integerValue.getValue())
        assertEquals(
            listOf(actionData(source = DivActionSource.ANIMATION, url = END_URL)),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `start action overrides timing values and interpolator for double variable`() {
        setContent(target(
            animator = numberAnimator(
                duration = 1000,
                endValue = 10.0,
                id = ANIMATOR_ID,
                interpolator = DivAnimationInterpolator.EASE_IN,
                startDelay = 500,
                variableName = DOUBLE_VALUE,
            ),
            startAction = animatorStartAction(
                animatorId = ANIMATOR_ID,
                duration = 160,
                endValue = typedValue(6.0),
                interpolator = DivAnimationInterpolator.LINEAR,
                startDelay = 96,
                startValue = typedValue(2.0),
            ),
        ))

        startTargetAnimation()
        assertEquals(2.0, doubleValue.getValue())
        beginAnimation()
        advanceAnimationBy(48)

        assertEquals(2.0, doubleValue.getValue())

        advanceAnimationBy(128)

        assertEquals(4.0, doubleValue.getValue() as Double, absoluteTolerance = 0.01)

        advanceAnimationBy(80)

        assertEquals(6.0, doubleValue.getValue() as Double, absoluteTolerance = 0.01)
    }

    @Test
    fun `system duration scale applies to delay and duration`() {
        motionDurationScale.scaleFactor = 2f
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startDelay = 80,
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(160)

        assertEquals(0L, integerValue.getValue())

        advanceAnimationBy(160)

        assertEquals(50L, integerValue.getValue())

        advanceAnimationBy(160)

        assertEquals(100L, integerValue.getValue())
    }

    @Test
    fun `root trigger starts animator after target registration`() {
        rule.setContent(
            configuration = configuration,
            data = data(
                content = target(numberAnimator(
                    duration = 160,
                    endValue = 100.0,
                    id = ANIMATOR_ID,
                    startValue = 0.0,
                    variableName = VALUE,
                )),
                triggers = listOf(trigger(
                    action = startAction(),
                    condition = "@{true}",
                )),
            ),
        )

        beginAnimation()
        beginAnimation()
        advanceAnimationBy(160)

        assertEquals(100L, integerValue.getValue())
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `local trigger starts animator after target registration`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            ),
            triggers = listOf(trigger(
                action = startAction(),
                condition = "@{true}",
            )),
        ))

        beginAnimation()
        beginAnimation()
        advanceAnimationBy(160)

        assertEquals(100L, integerValue.getValue())
        assertEquals(emptyList(), reporter.errors)
    }

    @Test
    fun `zero duration animator waits for start delay`() {
        setContent(target(
            animator = numberAnimator(
                duration = 0,
                endActions = listOf(action(url = END_URL)),
                endValue = 100.0,
                id = ANIMATOR_ID,
                startDelay = 32,
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(16)

        assertEquals(0L, integerValue.getValue())
        assertEquals(emptyList(), externalActionHandler.handledActions)

        advanceAnimationBy(16)

        assertEquals(100L, integerValue.getValue())
        assertEquals(
            listOf(actionData(source = DivActionSource.ANIMATION, url = END_URL)),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `zero duration animator ignores fixed repeat count`() {
        setContent(target(
            animator = numberAnimator(
                direction = DivAnimationDirection.ALTERNATE,
                duration = 0,
                endValue = 100.0,
                id = ANIMATOR_ID,
                repeatCount = fixedCount(2),
                startDelay = 32,
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(32)

        assertEquals(100L, integerValue.getValue())
    }

    @Test
    fun `zero duration infinite animator completes`() {
        setContent(target(
            animator = numberAnimator(
                duration = 0,
                endActions = listOf(action(url = END_URL)),
                endValue = 100.0,
                id = ANIMATOR_ID,
                repeatCount = infinityCount(),
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()

        assertEquals(
            listOf(actionData(source = DivActionSource.ANIMATION, url = END_URL)),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `animator without start value uses current variable value`() {
        integerValue.set(20L)
        setContent(target(
            animator = numberAnimator(
                duration = 1024,
                endValue = 100.0,
                id = ANIMATOR_ID,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(512)

        assertEquals(60L, integerValue.getValue())
    }

    @Test
    fun `integer end value is evaluated before explicit start`() {
        integerValue.set(100L)
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = doubleExpression("@{$VALUE + 100.0}"),
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(160)

        assertEquals(200L, integerValue.getValue())
    }

    @Test
    fun `double end value is evaluated before explicit start`() {
        doubleValue.set(100.0)
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = doubleExpression("@{$DOUBLE_VALUE + 100}"),
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = DOUBLE_VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(160)

        assertEquals(200.0, doubleValue.getValue() as Double, absoluteTolerance = 0.01)
    }

    @Test
    fun `integer start value is captured after delay`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startDelay = 96,
                variableName = VALUE,
            )
        ))
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(48)

        rule.runOnIdle { integerValue.set(40L) }
        advanceAnimationBy(48)
        advanceAnimationBy(80)

        assertEquals(70L, integerValue.getValue())
    }

    @Test
    fun `double start value is captured after delay`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startDelay = 96,
                variableName = DOUBLE_VALUE,
            )
        ))
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(48)

        rule.runOnIdle { doubleValue.set(40.0) }
        advanceAnimationBy(48)
        advanceAnimationBy(80)

        assertEquals(70.0, doubleValue.getValue() as Double, absoluteTolerance = 0.01)
    }

    @Test
    fun `reverse action starts at end and finishes at start`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            ),
            startAction = animatorStartAction(
                animatorId = ANIMATOR_ID,
                direction = DivAnimationDirection.REVERSE,
            ),
        ))

        startTargetAnimation()
        beginAnimation()

        assertEquals(100L, integerValue.getValue())

        advanceAnimationBy(160)

        assertEquals(0L, integerValue.getValue())
    }

    @Test
    fun `reverse animator applies initial value synchronously`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            ),
            startAction = animatorStartAction(
                animatorId = ANIMATOR_ID,
                direction = DivAnimationDirection.REVERSE,
            ),
        ))

        startTargetAnimation()

        assertEquals(100L, integerValue.getValue())
    }

    @Test
    fun `restart repeat keeps end value at exact iteration boundary`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                repeatCount = fixedCount(2),
                startValue = 0.0,
                variableName = VALUE,
            )
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(160)

        assertEquals(100L, integerValue.getValue())
    }

    @Test
    fun `fixed alternate action completes at the start value`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            ),
            startAction = animatorStartAction(
                animatorId = ANIMATOR_ID,
                direction = DivAnimationDirection.ALTERNATE,
                repeatCount = fixedCount(2),
            ),
        ))

        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(320)

        assertEquals(0L, integerValue.getValue())
    }

    @Test
    fun `fixed alternate reverse action starts and completes at the end value`() {
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            ),
            startAction = animatorStartAction(
                animatorId = ANIMATOR_ID,
                direction = DivAnimationDirection.ALTERNATE_REVERSE,
                repeatCount = fixedCount(2),
            ),
        ))

        startTargetAnimation()
        beginAnimation()
        assertEquals(100L, integerValue.getValue())

        advanceAnimationBy(160)
        assertEquals(0L, integerValue.getValue())

        advanceAnimationBy(160)
        assertEquals(100L, integerValue.getValue())
    }

    @Test
    fun `stop cancels infinite alternating animator before running end actions`() {
        setContent(container(items = listOf(
            target(
                animator = numberAnimator(
                    cancelActions = listOf(action(url = CANCEL_URL)),
                    direction = DivAnimationDirection.ALTERNATE,
                    duration = 160,
                    endActions = listOf(action(url = END_URL)),
                    endValue = 100.0,
                    id = ANIMATOR_ID,
                    repeatCount = infinityCount(),
                    startValue = 0.0,
                    variableName = VALUE,
                )
            ),
            text(
                action = action(
                    scopeId = TARGET_ID,
                    typed = animatorStopAction(ANIMATOR_ID),
                ),
                id = STOP_ID,
                text = "Stop",
            ),
        )))
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(240)

        assertEquals(50L, integerValue.getValue())

        rule.onNodeWithTag(STOP_ID).performClick()
        val stoppedValue = integerValue.getValue()
        advanceAnimationBy(160)

        assertEquals(stoppedValue, integerValue.getValue())
        assertEquals(
            listOf(
                actionData(source = DivActionSource.ANIMATION, url = CANCEL_URL),
                actionData(source = DivActionSource.ANIMATION, url = END_URL),
            ),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `restart cancels the previous animator before starting again`() {
        setContent(target(
            animator = numberAnimator(
                cancelActions = listOf(action(url = CANCEL_URL)),
                duration = 160,
                endActions = listOf(action(url = END_URL)),
                endValue = 100.0,
                id = ANIMATOR_ID,
                repeatCount = infinityCount(),
                startValue = 0.0,
                variableName = VALUE,
            )
        ))
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(80)

        startTargetAnimation()

        assertEquals(0L, integerValue.getValue())
        assertEquals(
            listOf(
                actionData(source = DivActionSource.ANIMATION, url = CANCEL_URL),
                actionData(source = DivActionSource.ANIMATION, url = END_URL),
            ),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `scope target finds animator on its ancestor`() {
        setContent(container(
            animators = listOf(numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            )),
            items = listOf(text(
                action = startAction(scopeId = CHILD_ID),
                id = CHILD_ID,
                text = expression("@{$VALUE}"),
                variables = listOf(variable(VALUE, 0L)),
            )),
        ))

        rule.onNodeWithTag(CHILD_ID).performClick()
        beginAnimation()
        advanceAnimationBy(160)

        rule.onNodeWithTag(CHILD_ID).assertTextEquals("100")
        assertEquals(0L, integerValue.getValue())
    }

    @Test
    fun `disabling animations finishes running animator without cancellation`() {
        val animationsEnabled = MutableStateFlow(true)
        setContent(
            target(
                animator = numberAnimator(
                    cancelActions = listOf(action(url = CANCEL_URL)),
                    duration = 160,
                    endActions = listOf(action(url = END_URL)),
                    endValue = 100.0,
                    id = ANIMATOR_ID,
                    startValue = 0.0,
                    variableName = VALUE,
                )
            ),
            configuration(animationsEnabled),
        )
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(80)

        rule.runOnIdle { animationsEnabled.value = false }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()

        assertEquals(100L, integerValue.getValue())
        assertEquals(
            listOf(actionData(source = DivActionSource.ANIMATION, url = END_URL)),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `disabling animations finishes infinite alternate animator at end value`() {
        val animationsEnabled = MutableStateFlow(true)
        setContent(
            target(
                animator = numberAnimator(
                    direction = DivAnimationDirection.ALTERNATE,
                    duration = 160,
                    endValue = 100.0,
                    id = ANIMATOR_ID,
                    repeatCount = infinityCount(),
                    startValue = 0.0,
                    variableName = VALUE,
                )
            ),
            configuration(animationsEnabled),
        )
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(240)

        rule.runOnIdle { animationsEnabled.value = false }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()

        assertEquals(100L, integerValue.getValue())
    }

    @Test
    fun `leaving composition cancels running animator`() {
        val data = data(target(
            animator = numberAnimator(
                cancelActions = listOf(action(url = CANCEL_URL)),
                duration = 160,
                endActions = listOf(action(url = END_URL)),
                endValue = 100.0,
                id = ANIMATOR_ID,
                startValue = 0.0,
                variableName = VALUE,
            )
        ))
        var visible by mutableStateOf(true)
        rule.setContentWithDivContext(configuration = configuration) {
            if (visible) DivView(data)
        }
        startTargetAnimation()
        beginAnimation()
        advanceAnimationBy(80)

        rule.runOnIdle { visible = false }
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()

        assertEquals(
            listOf(
                actionData(source = DivActionSource.ANIMATION, url = CANCEL_URL),
                actionData(source = DivActionSource.ANIMATION, url = END_URL),
            ),
            externalActionHandler.handledActions,
        )
    }

    @Test
    fun `missing animator reports an error`() {
        reporter.failOnError = false
        setContent(text(
            action = startAction(),
            animators = listOf(numberAnimator(
                duration = 100,
                endValue = 1.0,
                id = "other",
                variableName = VALUE,
            )),
            id = TARGET_ID,
            text = "Start",
        ))

        startTargetAnimation()

        assertEquals(listOf("Animator with id '$ANIMATOR_ID' not found"), reporter.errors)
    }

    @Test
    fun `incompatible animator variable reports an error`() {
        reporter.failOnError = false
        setContent(target(
            animator = numberAnimator(
                duration = 100,
                endValue = 100.0,
                id = ANIMATOR_ID,
                variableName = STRING_VALUE,
            )
        ))

        startTargetAnimation()

        assertEquals(
            listOf("Number animator variable has incompatible type: '$STRING_VALUE'"),
            reporter.errors,
        )
    }

    @Test
    fun `missing animator variable reports an error`() {
        reporter.failOnError = false
        setContent(target(
            animator = numberAnimator(
                duration = 160,
                endValue = 100.0,
                id = ANIMATOR_ID,
                variableName = "missing",
            )
        ))

        startTargetAnimation()

        assertEquals(listOf("Number animator variable not found: 'missing'"), reporter.errors)
    }

    @Test
    fun `duplicate animator id reports an error`() {
        reporter.failOnError = false
        val animator = numberAnimator(
            duration = 160,
            endValue = 100.0,
            id = ANIMATOR_ID,
            variableName = VALUE,
        )
        setContent(text(
            action = startAction(),
            animators = listOf(animator, animator),
            id = TARGET_ID,
            text = "Start",
        ))

        startTargetAnimation()

        assertEquals(listOf("Animator with id '$ANIMATOR_ID' is ambiguous"), reporter.errors)
    }

    @Test
    fun `color animator reports an unsupported error`() {
        reporter.failOnError = false
        setContent(target(
            animator = DivAnimator.Color(DivColorAnimator(
                duration = constant(160L),
                endValue = constant(0xFFFF0000.toInt()),
                id = ANIMATOR_ID,
                variableName = VALUE,
            ))
        ))

        startTargetAnimation()

        assertEquals(listOf("Color animator not supported: '$ANIMATOR_ID'"), reporter.errors)
    }

    @Test
    fun `ambiguous scope reports an error`() {
        reporter.failOnError = false
        setContent(container(
            animators = listOf(numberAnimator(
                duration = 100,
                endValue = 1.0,
                id = ANIMATOR_ID,
                variableName = VALUE,
            )),
            items = listOf(
                text(id = TARGET_ID, text = "First"),
                text(id = TARGET_ID, text = "Second"),
                text(action = startAction(), id = START_ID, text = "Start"),
            ),
        ))

        rule.onNodeWithTag(START_ID).performClick()

        assertEquals(listOf("Scope with id '$TARGET_ID' is ambiguous"), reporter.errors)
    }

    @Test
    fun `missing scope reports an error after target registration`() {
        reporter.failOnError = false
        setContent(text(
            action = startAction(scopeId = MISSING_SCOPE_ID),
            id = START_ID,
            text = "Start",
        ))

        rule.onNodeWithTag(START_ID).performClick()
        beginAnimation()

        assertEquals(listOf("Scope with id '$MISSING_SCOPE_ID' not found"), reporter.errors)
    }

    @Test
    fun `animator action without scope is ignored`() {
        setContent(target(
            animator = numberAnimator(
                duration = 100,
                endValue = 100.0,
                id = ANIMATOR_ID,
                variableName = VALUE,
            ),
            scopeId = null,
        ))

        startTargetAnimation()
        rule.mainClock.advanceTimeBy(200)

        assertEquals(0L, integerValue.getValue())
        assertEquals(emptyList(), reporter.errors)
    }

    private fun configuration(
        animationsEnabled: MutableStateFlow<Boolean>? = null,
    ): DivConfiguration = DivConfiguration(
        actionHandler = externalActionHandler,
        animationsEnabledProvider = animationsEnabled?.let { state ->
            object : DivAnimationsEnabledProvider {
                override val animationsEnabled = state
            }
        } ?: DivAnimationsEnabledProvider.DEFAULT,
        reporter = reporter,
        variableController = variableController,
    )

    private fun target(
        animator: com.yandex.div2.DivAnimator,
        scopeId: String? = TARGET_ID,
        startAction: com.yandex.div2.DivActionTyped = animatorStartAction(ANIMATOR_ID),
        triggers: List<com.yandex.div2.DivTrigger>? = null,
    ): Div = text(
        action = action(scopeId = scopeId, typed = startAction),
        animators = listOf(animator),
        id = TARGET_ID,
        text = "Start",
        triggers = triggers,
    )

    private fun startAction(scopeId: String = TARGET_ID) = action(
        scopeId = scopeId,
        typed = animatorStartAction(ANIMATOR_ID),
    )

    private fun setContent(content: Div, configuration: DivConfiguration = this.configuration) {
        rule.setContent(configuration = configuration, data = data(content))
    }

    private fun startTargetAnimation() {
        rule.onNodeWithTag(TARGET_ID).performClick()
    }

    private fun beginAnimation() {
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun advanceAnimationBy(durationMillis: Long) {
        rule.mainClock.advanceTimeBy(durationMillis)
        rule.waitForIdle()
    }

    private class TestMotionDurationScale(
        override var scaleFactor: Float = 1f,
    ) : MotionDurationScale

    private companion object {
        const val ANIMATOR_ID = "rotation"
        const val CANCEL_URL = "test://cancel"
        const val CHILD_ID = "child"
        const val DOUBLE_VALUE = "double_value"
        const val END_URL = "test://end"
        const val MISSING_SCOPE_ID = "missing"
        const val START_ID = "start"
        const val STOP_ID = "stop"
        const val STRING_VALUE = "string_value"
        const val TARGET_ID = "target"
        const val VALUE = "value"
    }
}
