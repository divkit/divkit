package com.yandex.div.core.view2.divs

import android.view.ContextThemeWrapper
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.internal.widget.menu.OverflowMenuWrapper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.action
import com.yandex.div.test.data.constant
import com.yandex.div2.DivAction
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import kotlin.test.Test
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class DivActionBinderMenuTest : DivBinderTest() {
    private val errorCollector = mock<ErrorCollector>()
    private val menuDivView =
        mock<Div2View> {
            on { bulkActions(any()) } doAnswer {
                it.getArgument<() -> Unit>(0).invoke()
            }
            on { errorCollector } doReturn errorCollector
        }
    private val underTest =
        DivActionBinder(
            actionPerformer = actionPerformer,
            logger = mock(),
            divActionBeaconSender = mock(),
            longtapActionsPassToChild = false,
            shouldIgnoreActionMenuItems = false,
        )

    @Test
    fun `disabled menu item is excluded without changing visible item order`() {
        val popupMenu =
            createPopupMenu(
                menuItem(action = action(), text = "First enabled"),
                menuItem(action = action(isEnabled = false), text = "Disabled"),
                menuItem(action = action(), text = "Second enabled"),
            )

        assertEquals(listOf("First enabled", "Second enabled"), popupMenu.titles)
    }

    @Test
    fun `menu item is shown only when selected action list contains enabled action`() {
        val enabledAction = action(isEnabled = true)
        val disabledAction = action(isEnabled = false)
        val popupMenu =
            createPopupMenu(
                menuItem(text = "Partially enabled", actions = listOf(disabledAction, enabledAction)),
                menuItem(text = "All disabled", actions = listOf(disabledAction)),
                menuItem(text = "Actions take priority", action = enabledAction, actions = listOf(disabledAction)),
                menuItem(text = "No actions"),
            )

        assertEquals(listOf("Partially enabled"), popupMenu.titles)
    }

    @Test
    fun `click passes visible item index and its selected actions`() {
        val enabledAction = action(id = "enabled")
        val visibleItem = menuItem(text = "Enabled", actions = listOf(enabledAction))
        val popupMenu =
            createPopupMenu(
                menuItem(action = action(isEnabled = false), text = "Disabled"),
                visibleItem,
            )

        popupMenu.menu.performIdentifierAction(0, 0)

        verify(actionPerformer).performMenuActions(
            eq(menuDivView),
            eq(resolver),
            eq(listOf(enabledAction)),
            eq(0),
            eq(visibleItem),
        )
    }

    @Test
    fun `enabled expression is evaluated when menu is created`() {
        var isEnabled = false
        val enabledExpression =
            mock<Expression<Boolean>> {
                onGeneric { evaluate(any()) } doAnswer { isEnabled }
            }
        val dynamicItem =
            menuItem(
                action = DivAction(isEnabled = enabledExpression),
                text = "Dynamic",
            )

        val hiddenTitles = createPopupMenu(dynamicItem).titles
        isEnabled = true
        val visibleTitles = createPopupMenu(dynamicItem).titles

        assertEquals(listOf(emptyList(), listOf("Dynamic")), listOf(hiddenTitles, visibleTitles))
    }

    @Test
    fun `error is reported when menu item has no actions`() {
        createPopupMenu(menuItem(text = "No actions"))

        verify(errorCollector).logError(any())
    }

    private fun createPopupMenu(
        vararg items: DivAction.MenuItem,
        expressionResolver: ExpressionResolver = resolver,
    ): PopupMenu {
        val popupContext = ContextThemeWrapper(context, context.theme)
        return PopupMenu(popupContext, View(popupContext)).also {
            createMenuWrapperListener(items.toList(), expressionResolver).onMenuCreated(it)
        }
    }

    private fun createMenuWrapperListener(
        items: List<DivAction.MenuItem>,
        expressionResolver: ExpressionResolver,
    ): OverflowMenuWrapper.Listener =
        underTest.MenuWrapperListener(items, expressionResolver, menuDivView)

    private fun menuItem(
        text: String,
        action: DivAction? = null,
        actions: List<DivAction>? = null,
    ) = DivAction.MenuItem(
        action = action,
        actions = actions,
        text = constant(text),
    )

    private val PopupMenu.titles: List<String>
        get() = (0 until menu.size()).map { menu.getItem(it).title.toString() }
}
