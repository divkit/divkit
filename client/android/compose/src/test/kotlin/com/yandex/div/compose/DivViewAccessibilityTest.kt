package com.yandex.div.compose

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsMatcher.Companion.keyIsDefined
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.core.net.toUri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.accessibility
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.image
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility.Mode
import com.yandex.div2.DivAccessibility.Type
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewAccessibilityTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = DivComposeConfiguration(reporter = TestReporter())
    private val tag = "tag"

    @Test
    fun `description is set as content description`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(description = "Photo of a cat")
            )
        )

        rule.onNodeWithTag(tag).assert(hasContentDescription("Photo of a cat"))
    }

    @Test
    fun `hint is set as content description when no description`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(hint = "Tap to open")
            )
        )

        rule.onNodeWithTag(tag).assert(hasContentDescription("Tap to open"))
    }

    @Test
    fun `description and hint are combined with newline`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(
                    description = "Submit",
                    hint = "Tap to submit"
                )
            )
        )

        rule.onNodeWithTag(tag).assert(hasContentDescription("Submit\nTap to submit"))
    }

    @Test
    fun `identical description and hint are not duplicated`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(
                    description = "Submit",
                    hint = "Submit"
                )
            )
        )

        rule.onNodeWithTag(tag).assert(hasContentDescription("Submit"))
    }

    @Test
    fun `state description is applied`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(
                    description = "Loading",
                    stateDescription = "50% complete"
                )
            )
        )

        rule.onNodeWithTag(tag).assert(hasStateDescription("50% complete"))
    }

    @Test
    fun `type button sets Button role`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.BUTTON)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.Button))
    }

    @Test
    fun `type image sets Image role`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.IMAGE)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.Image))
    }

    @Test
    fun `type checkbox sets Checkbox role`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.CHECKBOX)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.Checkbox))
    }

    @Test
    fun `type radio sets RadioButton role`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.RADIO)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.RadioButton))
    }

    @Test
    fun `type tab_bar sets Tab role`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.TAB_BAR)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.Tab))
    }

    @Test
    fun `type select sets DropdownList role`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.SELECT)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.DropdownList))
    }

    @Test
    fun `type header sets heading`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.HEADER)
            )
        )

        rule.onNodeWithTag(tag).assert(keyIsDefined(SemanticsProperties.Heading))
    }

    @Test
    fun `checkbox with is_checked true has toggleable state on`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(
                    type = Type.CHECKBOX,
                    isChecked = true
                )
            )
        )

        rule.onNodeWithTag(tag).assert(hasToggleableState(ToggleableState.On))
    }

    @Test
    fun `checkbox with is_checked false has toggleable state off`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(
                    type = Type.CHECKBOX,
                    isChecked = false
                )
            )
        )

        rule.onNodeWithTag(tag).assert(hasToggleableState(ToggleableState.Off))
    }

    @Test
    fun `is_checked is ignored for non-checkable type`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(
                    type = Type.BUTTON,
                    isChecked = true
                )
            )
        )

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Button))
            .assert(!hasToggleableState(ToggleableState.On))
    }

    @Test
    fun `accessibility is ignored for elements with mode=exclude`() {
        setContent(
            text(
                text = "Test",
                accessibility = accessibility(
                    description = "Hidden text",
                    mode = Mode.EXCLUDE
                )
            )
        )

        rule.onNode(hasContentDescription("Hidden text")).assertDoesNotExist()
    }

    @Test
    fun `accessibility is ignored for elements inside container with mode=exclude`() {
        setContent(
            container(
                items = listOf(
                    text(
                        text = "Item 1",
                        accessibility = accessibility(description = "Item description")
                    )
                ),
                accessibility = accessibility(mode = Mode.EXCLUDE)
            )
        )

        rule.onNode(hasContentDescription("Item description")).assertDoesNotExist()
    }

    @Test
    fun `testTag is preserved for elements with mode=exclude`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(mode = Mode.EXCLUDE)
            )
        )

        rule.onNodeWithTag(tag).assertIsDisplayed()
    }

    @Test
    fun `element inside container with mode=exclude is available by testTag`() {
        setContent(
            container(
                items = listOf(
                    text(id = "item", text = "Item 1")
                ),
                accessibility = accessibility(mode = Mode.EXCLUDE)
            )
        )

        rule.onNodeWithTag("item", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `content description is preserved for elements with mode=merge`() {
        setContent(
            container(
                id = tag,
                items = listOf(text(text = "Item 1")),
                accessibility = accessibility(
                    description = "Container description",
                    mode = Mode.MERGE
                )
            )
        )

        rule.onNodeWithTag(tag).assert(hasContentDescription("Container description"))
    }

    @Test
    fun `accessibility is ignored for elements inside container with mode=merge`() {
        setContent(
            container(
                items = listOf(
                    text(
                        text = "Item 1",
                        accessibility = accessibility(description = "Item description")
                    )
                ),
                accessibility = accessibility(
                    description = "Container description",
                    mode = Mode.MERGE
                )
            )
        )

        rule.onNode(hasContentDescription("Item description")).assertDoesNotExist()
    }

    @Test
    fun `element inside container with mode=merge is available by testTag`() {
        setContent(
            container(
                items = listOf(
                    text(id = "item", text = "Item 1")
                ),
                accessibility = accessibility(
                    description = "Container description",
                    mode = Mode.MERGE
                )
            )
        )

        rule.onNodeWithTag("item", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `element without accessibility has no content description`() {
        setContent(
            text(id = tag, text = "Test")
        )

        rule.onNodeWithTag(tag).assert(!keyIsDefined(SemanticsProperties.ContentDescription))
    }

    @Test
    fun `auto mode resolves no role for text`() {
        setContent(
            text(
                id = tag,
                text = "Test",
                accessibility = accessibility(type = Type.AUTO)
            )
        )

        rule.onNodeWithTag(tag).assert(!keyIsDefined(SemanticsProperties.Role))
    }

    @Test
    fun `auto mode resolves Image role for image`() {
        setContent(
            image(
                id = tag,
                imageUrl = constant("https://example.com/image.png".toUri()),
                accessibility = accessibility(type = Type.AUTO)
            )
        )

        rule.onNodeWithTag(tag).assert(hasRole(Role.Image))
    }

    @Test
    fun `auto mode resolves no role for container`() {
        setContent(
            container(
                id = tag,
                items = listOf(text(text = "Item")),
                accessibility = accessibility(
                    description = "Container",
                    type = Type.AUTO
                )
            )
        )

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Container"))
            .assert(!keyIsDefined(SemanticsProperties.Role))
    }

    private fun setContent(content: Div) {
        rule.setContent(configuration = configuration, data = data(content))
    }
}

private fun hasRole(role: Role) =
    SemanticsMatcher.expectValue(SemanticsProperties.Role, role)

private fun hasToggleableState(state: ToggleableState) =
    SemanticsMatcher.expectValue(SemanticsProperties.ToggleableState, state)

private fun hasStateDescription(value: String) =
    SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, value)
