package com.yandex.div.compose

import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.gallery
import com.yandex.div.test.data.image
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DivViewAccessibilityTest {

    @get:Rule
    val rule = createComposeRule()

    private val configuration = DivComposeConfiguration(reporter = TestReporter())

    private val tag = "tag"
    private val stubImageUrl = constant(Uri.parse("https://example.com/image.png"))

    @Test
    fun `description is set as content description`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Photo of a cat"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Photo of a cat"))
    }

    @Test
    fun `hint is set as content description when no description`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            hint = constant("Tap to open"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Tap to open"))
    }

    @Test
    fun `description and hint are combined with newline`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Submit"),
            hint = constant("Double tap to submit"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Submit\nDouble tap to submit"))
    }

    @Test
    fun `identical description and hint are not duplicated`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Submit"),
            hint = constant("Submit"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Submit"))
    }

    @Test
    fun `state description is applied`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Loading"),
            stateDescription = constant("50% complete"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasStateDescription("50% complete"))
    }

    @Test
    fun `type button sets button role`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Submit"),
            type = DivAccessibility.Type.BUTTON,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Button))
    }

    @Test
    fun `type image sets image role`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("A photo"),
            type = DivAccessibility.Type.IMAGE,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Image))
    }

    @Test
    fun `type checkbox sets checkbox role`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Accept terms"),
            type = DivAccessibility.Type.CHECKBOX,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Checkbox))
    }

    @Test
    fun `type radio sets radio button role`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Option A"),
            type = DivAccessibility.Type.RADIO,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.RadioButton))
    }

    @Test
    fun `type tab_bar sets tab role`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Home tab"),
            type = DivAccessibility.Type.TAB_BAR,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Tab))
    }

    @Test
    fun `type select sets dropdown list role`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Choose country"),
            type = DivAccessibility.Type.SELECT,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.DropdownList))
    }

    @Test
    fun `type header sets heading`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Section title"),
            type = DivAccessibility.Type.HEADER,
        )))

        rule.onNodeWithTag(tag)
            .assert(isHeading())
    }

    @Test
    fun `checkbox with is_checked true has toggleable state on`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Accept terms"),
            type = DivAccessibility.Type.CHECKBOX,
            isChecked = constant(true),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasToggleableState(ToggleableState.On))
    }

    @Test
    fun `checkbox with is_checked false has toggleable state off`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Accept terms"),
            type = DivAccessibility.Type.CHECKBOX,
            isChecked = constant(false),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasToggleableState(ToggleableState.Off))
    }

    @Test
    fun `is_checked is ignored for non-checkable type`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Submit"),
            type = DivAccessibility.Type.BUTTON,
            isChecked = constant(true),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Button) and !hasToggleableState(ToggleableState.On))
    }

    @Test
    fun `mode exclude hides element from accessibility tree`() {
        setContent(text(text = "Test", accessibility = DivAccessibility(
            description = constant("Hidden text"),
            mode = constant(DivAccessibility.Mode.EXCLUDE),
        )))

        rule.onNode(hasContentDescription("Hidden text"))
            .assertDoesNotExist()
    }

    @Test
    fun `mode merge preserves content description`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Merged label"),
            mode = constant(DivAccessibility.Mode.MERGE),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Merged label"))
            .assertIsDisplayed()
    }

    @Test
    fun `element without accessibility has no content description`() {
        setContent(text(id = tag, text = "Test"))

        rule.onNodeWithTag(tag)
            .assert(!SemanticsMatcher.keyIsDefined(SemanticsProperties.ContentDescription))
    }

    @Test
    fun `auto mode resolves no role for text`() {
        setContent(text(id = tag, text = "Test", accessibility = DivAccessibility(
            description = constant("Label"),
            type = DivAccessibility.Type.AUTO,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Label"))
            .assert(!SemanticsMatcher.keyIsDefined(SemanticsProperties.Role))
    }

    @Test
    fun `auto mode resolves image role for image`() {
        setContent(image(id = tag, imageUrl = stubImageUrl, accessibility = DivAccessibility(
            description = constant("A photo"),
            type = DivAccessibility.Type.AUTO,
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Image))
    }

    @Test
    fun `auto mode without explicit type resolves image role for image`() {
        setContent(image(id = tag, imageUrl = stubImageUrl, accessibility = DivAccessibility(
            description = constant("A photo"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasRole(Role.Image))
    }

    @Test
    fun `auto mode resolves no role for gallery with description`() {
        setContent(gallery(id = tag, items = listOf(text(text = "Item")), accessibility = DivAccessibility(
            description = constant("Photo carousel"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Photo carousel"))
            .assert(!SemanticsMatcher.keyIsDefined(SemanticsProperties.Role))
    }

    @Test
    fun `auto mode resolves no semantics for gallery without description`() {
        setContent(gallery(id = tag, items = listOf(text(text = "Item")), accessibility = DivAccessibility(
            hint = constant("Swipe to browse"),
        )))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Swipe to browse"))
            .assert(!SemanticsMatcher.keyIsDefined(SemanticsProperties.Role))
    }

    @Test
    fun `auto mode resolves no role for container`() {
        setContent(container(id = tag, accessibility = DivAccessibility(
            description = constant("Section"),
        ), items = listOf(text(text = "Child"))))

        rule.onNodeWithTag(tag)
            .assert(hasContentDescription("Section"))
            .assert(!SemanticsMatcher.keyIsDefined(SemanticsProperties.Role))
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

private fun isHeading() =
    SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)
