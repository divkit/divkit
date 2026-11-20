package com.yandex.div.compose.views.transition

import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.click
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.DivView
import com.yandex.div.compose.LocalDivContext
import com.yandex.div.compose.TestReporter
import com.yandex.div.compose.divConfiguration
import com.yandex.div.core.DivAnimationsEnabledProvider
import com.yandex.div.core.expression.variables.DivVariableController
import com.yandex.div.data.Variable
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.fixed
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.matchParent
import com.yandex.div.test.data.select
import com.yandex.div.test.data.selectOption
import com.yandex.div.test.data.setVariableAction
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div.test.data.textRange
import com.yandex.div.test.data.typedValue
import com.yandex.div.test.data.visibilityExpression
import com.yandex.div.test.data.wrapContent
import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimationInterpolator
import com.yandex.div2.DivAppearanceSetTransition
import com.yandex.div2.DivAppearanceTransition
import com.yandex.div2.DivContainer
import com.yandex.div2.DivData
import com.yandex.div2.DivDimension
import com.yandex.div2.DivFadeTransition
import com.yandex.div2.DivPatch
import com.yandex.div2.DivSize
import com.yandex.div2.DivScaleTransition
import com.yandex.div2.DivSlideTransition
import com.yandex.div2.DivTooltip
import com.yandex.div2.DivTransitionTrigger
import com.yandex.div2.DivVisibility
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class DivVisibilityTransitionTest {
    @get:Rule
    val rule = createAndroidComposeRule<ComponentActivity>()

    private val visibility = Variable.StringVariable("visibility", "visible")
    private val clicked = Variable.BooleanVariable("clicked", false)
    private val underlyingClicked = Variable.BooleanVariable("underlying_clicked", false)
    private val choice = Variable.StringVariable("choice", "first")
    private val variableController = DivVariableController().apply {
        declare(visibility)
        declare(clicked)
        declare(underlyingClicked)
        declare(choice)
    }
    private val reporter = TestReporter()
    private val animationsEnabled = MutableStateFlow(true)
    private val divContext by lazy {
        DivContext(rule.activity, divConfiguration {
            reporter = this@DivVisibilityTransitionTest.reporter
            variableController = this@DivVisibilityTransitionTest.variableController
            animationsEnabledProvider = object : DivAnimationsEnabledProvider {
                override val animationsEnabled = this@DivVisibilityTransitionTest.animationsEnabled
            }
        })
    }
    private lateinit var card: DivData

    @Test
    fun `content slides in when visibility becomes visible`() {
        visibility.set("gone")
        setContent(transitioningText(transitionIn = DivAppearanceTransition.Slide(
            DivSlideTransition(
                distance = DivDimension(value = constant(100.0)),
                edge = constant(DivSlideTransition.Edge.RIGHT),
                duration = constant(1_000L),
                interpolator = constant(DivAnimationInterpolator.LINEAR),
            )
        )))

        setVisibility("visible")
        rule.mainClock.advanceTimeBy(250)

        val left = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(left in 65.dp..85.dp, "Enter transition left position: $left")
    }

    @Test
    fun `implicit slide starts at the immediate parent edge`() {
        visibility.set("gone")
        setContent(container(
            width = fixed(100),
            items = listOf(text(
                text = constant("Content"),
                width = fixed(40),
                visibility = visibilityExpression("@{visibility}"),
                transitionIn = DivAppearanceTransition.Slide(DivSlideTransition(
                    edge = constant(DivSlideTransition.Edge.RIGHT),
                    duration = constant(1_000L),
                    interpolator = constant(DivAnimationInterpolator.LINEAR),
                )),
            )),
        ))

        setVisibility("visible")

        val left = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(left in 95.dp..100.dp, "First enter frame: $left")
    }

    @Test
    fun `finished implicit exit slide stays at the parent edge while fade runs`() {
        setContent(container(
            width = fixed(100),
            items = listOf(text(
                text = constant("Content"),
                width = fixed(40),
                visibility = visibilityExpression("@{visibility}"),
                transitionOut = DivAppearanceTransition.Set(DivAppearanceSetTransition(
                    items = listOf(
                        DivAppearanceTransition.Slide(DivSlideTransition(
                            edge = constant(DivSlideTransition.Edge.RIGHT),
                            duration = constant(400L),
                        )),
                        fade(),
                    ),
                )),
            )),
        ))

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(600)

        val left = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(left in 99.dp..101.dp, "Completed implicit slide position: $left")
    }

    @Test
    fun `fade exit preserves the position of an interrupted slide enter`() {
        visibility.set("gone")
        setContent(transitioningText(transitionIn = DivAppearanceTransition.Slide(
            DivSlideTransition(
                distance = DivDimension(value = constant(100.0)),
                edge = constant(DivSlideTransition.Edge.RIGHT),
                duration = constant(1_000L),
                interpolator = constant(DivAnimationInterpolator.LINEAR),
            )
        )))
        setVisibility("visible")
        rule.mainClock.advanceTimeBy(320)
        val beforeExit = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left

        setVisibility("gone")

        val afterExit = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(
            afterExit in (beforeExit - 3.dp)..(beforeExit + 3.dp),
            "Exit position jumped from $beforeExit to $afterExit",
        )
    }

    @Test
    fun `content remains rendered while visibility exit is running`() {
        setContent(transitioningText())

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun `content is removed when visibility exit finishes`() {
        setContent(transitioningText())
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)
        rule.onNodeWithText("Content").assertExists()

        rule.mainClock.advanceTimeBy(600)

        rule.onNodeWithText("Content").assertDoesNotExist()
    }

    @Test
    fun `tooltip content slides in when it becomes visible`() {
        visibility.set("gone")
        showTooltip(transitioningText(transitionIn = slide()))

        setVisibility("visible")
        rule.mainClock.advanceTimeBy(250)
        val entering = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        rule.mainClock.advanceTimeBy(1_000)

        val resting = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(entering - resting in 65.dp..85.dp, "Enter offset: ${entering - resting}")
    }

    @Test
    fun `tooltip content slides out while its exit is running`() {
        showTooltip(transitioningText(transitionOut = slide()))
        val resting = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        val exiting = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(exiting - resting in 40.dp..60.dp, "Exit offset: ${exiting - resting}")
    }

    @Test
    fun `tooltip content is removed when visibility exit finishes`() {
        showTooltip(transitioningText(transitionOut = slide()))
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)
        rule.onNodeWithText("Content").assertExists()

        rule.mainClock.advanceTimeBy(600)

        rule.onNodeWithText("Content").assertDoesNotExist()
    }

    @Test
    fun `content with only an enter transition animates on every appearance`() {
        visibility.set("gone")
        setContent(container(items = listOf(
            transitioningText(transitionIn = slide(), transitionOut = null),
        )))
        setVisibility("visible")
        rule.mainClock.advanceTimeBy(1_100)
        setVisibility("gone")
        rule.onNodeWithText("Content").assertDoesNotExist()

        setVisibility("visible")
        rule.mainClock.advanceTimeBy(250)

        val left = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(left in 65.dp..85.dp, "Second enter transition left position: $left")
    }

    @Test
    fun `implicit slide of a gallery item starts at the gallery edge`() {
        visibility.set("gone")
        setContent(gallery(
            width = fixed(300),
            height = fixed(100),
            items = listOf(text(
                text = constant("Content"),
                width = fixed(40),
                visibility = visibilityExpression("@{visibility}"),
                transitionIn = slide(distance = null),
            )),
        ))

        setVisibility("visible")

        val left = rule.onNodeWithText("Content").getUnclippedBoundsInRoot().left
        assertTrue(left in 290.dp..300.dp, "First enter frame: $left")
    }

    @Test
    fun `exiting content ignores taps after its fade finishes`() {
        setContent(transitioningText(actions = tapAction("clicked"), transitionOut = fadeThenScale()))
        val tapPosition = rule.onNodeWithText("Content").fetchSemanticsNode().boundsInRoot.center
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(600)

        tapAt(tapPosition)

        assertFalse(clicked.getValue() as Boolean)
    }

    @Test
    fun `tap on exiting content reaches the element under it`() {
        setContent(container(
            orientation = DivContainer.Orientation.OVERLAP,
            items = listOf(
                text(
                    text = constant("Underlying"),
                    actions = tapAction("underlying_clicked"),
                    width = fixed(100),
                    height = fixed(40),
                ),
                transitioningText(
                    actions = tapAction("clicked"),
                    transitionOut = fadeThenScale(),
                    width = fixed(100),
                ),
            ),
        ))
        val tapPosition = rule.onNodeWithText("Content").fetchSemanticsNode().boundsInRoot.center
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(600)

        tapAt(tapPosition)

        assertEquals(true, underlyingClicked.getValue())
    }

    @Test
    fun `transparent exiting text does not fire range action`() {
        setContent(text(
            id = "exiting",
            text = constant("Content"),
            width = wrapContent(),
            visibility = visibilityExpression("@{visibility}"),
            transitionOut = fadeThenScale(),
            ranges = listOf(textRange(actions = tapAction("clicked"))),
        ))
        val tapPosition = rule.onNodeWithTag("exiting").fetchSemanticsNode().boundsInRoot.center
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(600)

        tapAt(tapPosition)

        assertFalse(clicked.getValue() as Boolean)
    }

    @Test
    fun `transparent exiting container passes tap to lower sibling`() {
        setContent(container(
            id = "overlap",
            orientation = DivContainer.Orientation.OVERLAP,
            items = listOf(
                text(
                    id = "underlying",
                    text = constant("Underlying"),
                    width = fixed(100),
                    height = fixed(30),
                    actions = tapAction("underlying_clicked"),
                ),
                container(
                    id = "exiting",
                    width = fixed(100),
                    height = fixed(40),
                    visibility = visibilityExpression("@{visibility}"),
                    transitionOut = fadeThenScale(),
                    items = listOf(text(text = "Content")),
                ),
            ),
        ))
        val tapPosition = rule.onNodeWithTag("exiting").fetchSemanticsNode().boundsInRoot.center
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(600)

        tapAt(tapPosition)

        rule.onNodeWithTag("overlap").assertHeightIsEqualTo(40.dp)
        assertTrue(underlyingClicked.getValue() as Boolean)
    }

    @Test
    fun `content disappears immediately when visibility trigger is excluded`() {
        setContent(transitioningText(transitionTriggers = listOf(DivTransitionTrigger.STATE_CHANGE)))

        setVisibility("gone")

        rule.onNodeWithText("Content").assertDoesNotExist()
    }

    @Test
    fun `content disappears immediately when animations are disabled`() {
        animationsEnabled.value = false
        setContent(transitioningText())

        setVisibility("gone")

        rule.onNodeWithText("Content").assertDoesNotExist()
    }

    @Test
    fun `invisible content keeps its layout after exit finishes`() {
        setContent(container(id = "parent", items = listOf(transitioningText())))

        setVisibility("invisible")
        rule.mainClock.advanceTimeBy(1_100)

        rule.onNodeWithTag("parent").assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun `collection child remains rendered while visibility exit is running`() {
        setContent(container(items = listOf(transitioningText())))

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun `match parent overlap child remains rendered while visibility exit is running`() {
        setContent(container(
            orientation = DivContainer.Orientation.OVERLAP,
            width = wrapContent(),
            items = listOf(
                text(text = constant("Sizer"), width = fixed(100)),
                transitioningText(),
            ),
        ))

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun `returning visible content survives the cancelled exit`() {
        setContent(transitioningText())
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        setVisibility("visible")
        rule.mainClock.advanceTimeBy(1_100)

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun `visibility exit runs inside a state without changing its state id`() {
        setContent(state(
            id = "state",
            states = listOf(state(stateId = "first", div = transitioningText())),
        ))

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun `select menu closes when the select starts its visibility exit`() {
        setContent(choiceSelect(
            transitionOut = fade(),
            visibility = visibilityExpression("@{visibility}"),
        ))
        openSelectMenu()

        setVisibility("gone")
        advanceWithPopups(400)

        rule.onNodeWithTag("select").assertExists()
        rule.onNodeWithText("Second").assertDoesNotExist()
    }

    @Test
    fun `select menu closes when its parent starts a visibility exit`() {
        setContent(container(
            items = listOf(choiceSelect()),
            transitionOut = fade(),
            visibility = visibilityExpression("@{visibility}"),
        ))
        openSelectMenu()

        setVisibility("gone")
        advanceWithPopups(400)

        rule.onNodeWithTag("select").assertExists()
        rule.onNodeWithText("Second").assertDoesNotExist()
    }

    @Test
    fun `exiting select does not reopen its menu`() {
        setContent(choiceSelect(
            transitionOut = fade(),
            visibility = visibilityExpression("@{visibility}"),
        ))
        setVisibility("gone")
        advanceWithPopups(400)

        rule.onNodeWithTag("select").performSemanticsAction(SemanticsActions.OnClick) { it() }
        advanceWithPopups(400)

        rule.onNodeWithText("Second").assertDoesNotExist()
    }

    @Test
    fun `sibling patch preserves a running visibility exit`() {
        setContent(container(items = listOf(
            transitioningText(),
            text(id = "sibling", text = "Before patch"),
        )))
        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        patch(DivPatch.Change("sibling", listOf(text(id = "sibling", text = "After patch"))))
        rule.mainClock.advanceTimeBy(100)

        rule.onNodeWithText("Content").assertExists()
    }

    @Test
    fun `replacement with the same id runs its visibility exit`() {
        setContent(transitioningText())
        patch(DivPatch.Change("content", listOf(text(
            id = "content",
            text = "Replacement",
            visibility = visibilityExpression("@{visibility}"),
            transitionOut = fade(),
        ))))

        setVisibility("gone")
        rule.mainClock.advanceTimeBy(500)

        rule.onNodeWithText("Replacement").assertExists()
    }

    private fun transitioningText(
        id: String? = "content",
        actions: List<DivAction>? = null,
        transitionIn: DivAppearanceTransition = fade(),
        transitionOut: DivAppearanceTransition? = fade(),
        transitionTriggers: List<DivTransitionTrigger>? = null,
        width: DivSize = matchParent(),
    ): Div = text(
        id = id,
        text = constant("Content"),
        actions = actions,
        height = fixed(40),
        visibility = visibilityExpression("@{visibility}"),
        transitionIn = transitionIn,
        transitionOut = transitionOut,
        transitionTriggers = transitionTriggers,
        width = width,
    )

    private fun slide(distance: Long? = 100) = DivAppearanceTransition.Slide(
        DivSlideTransition(
            distance = distance?.let { DivDimension(value = constant(it.toDouble())) },
            edge = constant(DivSlideTransition.Edge.RIGHT),
            duration = constant(1_000L),
            interpolator = constant(DivAnimationInterpolator.LINEAR),
        )
    )

    private fun fadeThenScale() = DivAppearanceTransition.Set(DivAppearanceSetTransition(
        items = listOf(
            DivAppearanceTransition.Fade(DivFadeTransition(duration = constant(400L))),
            DivAppearanceTransition.Scale(DivScaleTransition(duration = constant(1_000L))),
        ),
    ))

    private fun choiceSelect(
        transitionOut: DivAppearanceTransition? = null,
        visibility: Expression<DivVisibility> = constant(DivVisibility.VISIBLE),
    ) = select(
        id = "select",
        options = listOf(selectOption("first", "First"), selectOption("second", "Second")),
        transitionOut = transitionOut,
        valueVariable = "choice",
        visibility = visibility,
    )

    private fun openSelectMenu() {
        rule.onNodeWithTag("select").performSemanticsAction(SemanticsActions.OnClick) { it() }
        advanceWithPopups(400)
        rule.onNodeWithText("Second").assertExists()
    }

    // A popup window is added and removed only while the test waits for idle between frames.
    private fun advanceWithPopups(millis: Long) {
        repeat((millis / POPUP_STEP_MILLIS).toInt()) {
            rule.mainClock.advanceTimeBy(POPUP_STEP_MILLIS)
            rule.waitForIdle()
        }
    }

    private fun tapAction(variable: String) = listOf(
        action(typed = setVariableAction(name = variable, value = typedValue(true)))
    )

    private fun tapAt(position: Offset) {
        rule.onRoot().performTouchInput { click(position) }
        rule.waitForIdle()
    }

    private fun showTooltip(content: Div) {
        setContent(text(
            text = "Anchor",
            tooltips = listOf(DivTooltip(
                id = "tooltip",
                position = constant(DivTooltip.Position.CENTER),
                duration = constant(0L),
                div = content,
            )),
        ))
        rule.mainClock.autoAdvance = true
        rule.runOnIdle {
            assertTrue(requireNotNull(divContext.getTooltipController(card.logId)).showTooltip("tooltip", false))
        }
        rule.waitForIdle()
        rule.mainClock.autoAdvance = false
    }

    private fun fade() = DivAppearanceTransition.Fade(
        DivFadeTransition(duration = constant(1_000L))
    )

    private fun setContent(content: Div) {
        card = data(content)
        rule.mainClock.autoAdvance = false
        rule.activity.setContentView(
            ComposeView(divContext.component.baseContext).apply {
                setContent {
                    CompositionLocalProvider(LocalDivContext provides divContext) {
                        DivView(card)
                    }
                }
            }
        )
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun setVisibility(value: String) {
        rule.runOnIdle { visibility.set(value) }
        rule.waitForIdle()
        rule.mainClock.advanceTimeByFrame()
        rule.waitForIdle()
    }

    private fun patch(change: DivPatch.Change) {
        rule.runOnIdle {
            runBlocking { assertTrue(divContext.patcher.applyPatch(card, DivPatch(listOf(change)))) }
        }
        rule.waitForIdle()
        rule.mainClock.advanceTimeByFrame()
    }
}

private const val POPUP_STEP_MILLIS = 100L
