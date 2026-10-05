package com.yandex.div.core.util

import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivState
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class DivBlockLocatorTest {

    private val resolver = ExpressionResolver.EMPTY

    @Test
    fun `unique matching target is returned without scope`() {
        val target = text(id = "target", text = "Target")

        val result = findTarget(root = container(items = listOf(target)))

        assertSame(target, result.getOrThrow().div)
    }

    @Test
    fun `matches filters blocks with the same id`() {
        val target = text(id = "target", text = "Target")
        val root = container(items = listOf(
            container(id = "target"),
            target,
        ))

        val result = findTarget(root = root, matches = { block -> block.div is Div.Text })

        assertSame(target, result.getOrThrow().div)
    }

    @Test
    fun `missing target is reported`() {
        val result = findTarget(root = container())

        assertFailure("Element with id 'target' not found", result)
    }

    @Test
    fun `missing state is reported`() {
        val locator = DivBlockLocator(
            state = null,
            rootResolver = resolver,
            activeStates = ActiveStateProvider { emptySet() },
        )

        val result = locator.findTarget(
            divId = "target",
            scopeId = null,
            actionResolver = resolver,
            matches = { true },
        )

        assertFailure("Element with id 'target' not found", result)
    }

    @Test
    fun `duplicate target is reported`() {
        val root = container(items = listOf(
            text(id = "target", text = "First"),
            text(id = "target", text = "Second"),
        ))

        val result = findTarget(root = root)

        assertFailure("Element with id 'target' is ambiguous", result)
    }

    @Test
    fun `scope selects target from its subtree`() {
        val targetInsideScope = text(id = "target", text = "Inside")
        val root = container(items = listOf(
            text(id = "target", text = "Outside"),
            container(id = "scope", items = listOf(targetInsideScope)),
        ))

        val result = findTarget(root = root, scopeId = "scope")

        assertSame(targetInsideScope, result.getOrThrow().div)
    }

    @Test
    fun `target outside existing scope is not returned`() {
        val root = container(items = listOf(
            text(id = "target", text = "Outside"),
            container(id = "scope"),
        ))

        val result = findTarget(root = root, scopeId = "scope")

        assertFailure("Element with id 'target' not found in scope", result)
    }

    @Test
    fun `duplicate target inside scope is reported`() {
        val root = container(items = listOf(container(
            id = "scope",
            items = listOf(
                text(id = "target", text = "First"),
                text(id = "target", text = "Second"),
            ),
        )))

        val result = findTarget(root = root, scopeId = "scope")

        assertFailure("Element with id 'target' is ambiguous in scope", result)
    }

    @Test
    fun `duplicate scope is reported`() {
        val root = container(items = listOf(
            container(id = "scope"),
            container(id = "scope"),
        ))

        val result = findTarget(root = root, scopeId = "scope")

        assertFailure("Scope with id 'scope' is ambiguous", result)
    }

    @Test
    fun `missing scope warns and falls back to unique target`() {
        val target = text(id = "target", text = "Target")
        val warnings = mutableListOf<Throwable>()

        val result = findTarget(
            root = container(items = listOf(target)),
            scopeId = "scope",
            reportWarning = warnings::add,
        )

        assertSame(target, result.getOrThrow().div)
        assertEquals("Scope with id 'scope' not found", warnings.single().message)
    }

    @Test
    fun `active state selects one of duplicate targets`() {
        val activeTarget = text(id = "target", text = "Active")
        val root = Div.State(state(id = "state", states = listOf(
            DivState.State(stateId = "active", div = activeTarget),
            DivState.State(stateId = "inactive", div = text(id = "target", text = "Inactive")),
        )))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(DivStatePath.parse("0/state/active")),
        )

        assertSame(activeTarget, result.getOrThrow().div)
    }

    @Test
    fun `nested active states select one of duplicate targets`() {
        val activeTarget = text(id = "target", text = "Active")
        val innerState = Div.State(state(id = "inner", states = listOf(
            DivState.State(stateId = "active", div = activeTarget),
            DivState.State(stateId = "inactive", div = text(id = "target", text = "Inner inactive")),
        )))
        val root = Div.State(state(id = "outer", states = listOf(
            DivState.State(stateId = "active", div = innerState),
            DivState.State(stateId = "inactive", div = text(id = "target", text = "Outer inactive")),
        )))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(
                DivStatePath.parse("0/outer/active"),
                DivStatePath.parse("0/outer/active/inner/active"),
            ),
        )

        assertSame(activeTarget, result.getOrThrow().div)
    }

    @Test
    fun `unique target in inactive state remains addressable`() {
        val inactiveTarget = text(id = "target", text = "Inactive")
        val root = Div.State(state(id = "state", states = listOf(
            DivState.State(stateId = "active", div = container()),
            DivState.State(stateId = "inactive", div = inactiveTarget),
        )))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(DivStatePath.parse("0/state/active")),
        )

        assertSame(inactiveTarget, result.getOrThrow().div)
    }

    @Test
    fun `duplicate targets only in inactive state are not returned`() {
        val root = Div.State(state(id = "state", states = listOf(
            DivState.State(stateId = "active", div = container()),
            DivState.State(stateId = "inactive", div = container(items = listOf(
                text(id = "target", text = "First"),
                text(id = "target", text = "Second"),
            ))),
        )))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(DivStatePath.parse("0/state/active")),
        )

        assertFailure("Element with id 'target' not found", result)
    }

    @Test
    fun `scope does not include target from inactive state with the same path`() {
        val root = Div.State(state(id = "state", states = listOf(
            DivState.State(stateId = "active", div = container(
                id = "scope",
                items = listOf(text(id = "placeholder", text = "Placeholder")),
            )),
            DivState.State(stateId = "inactive", div = container(
                id = "scope",
                items = listOf(text(id = "target", text = "Target")),
            )),
        )))
        val cardState = data(root).states.single()
        val scopePaths = root.walk(resolver, DivStatePath.fromState(cardState))
            .filter { block -> block.div.value().id == "scope" }
            .map { block -> block.path.fullPath }
            .toList()
        val locator = DivBlockLocator(
            state = cardState,
            rootResolver = resolver,
            activeStates = ActiveStateProvider {
                setOf(DivStatePath.parse("0/state/active"))
            },
        )

        val result = locator.findTarget(
            divId = "target",
            scopeId = "scope",
            actionResolver = resolver,
            matches = { true },
        )

        assertEquals(1, scopePaths.distinct().size)
        assertFailure("Element with id 'target' not found in scope", result)
    }

    @Test
    fun `item builder source selects its repeated target`() {
        val resolvers = createRepeatedResolvers()
        val item = container(id = "scope", items = listOf(text(id = "target", text = "Target")))
        val builder = DivCollectionItemBuilder(
            data = constant(JSONArray().put(JSONObject()).put(JSONObject())),
            prototypes = listOf(DivCollectionItemBuilder.Prototype(item, id = constant("scope"))),
        )
        val root = Div.Container((container(id = "root") as Div.Container).value.copy(
            itemBuilder = builder,
        ))

        val result = findTarget(
            root = root,
            rootResolver = resolvers.root,
            actionResolver = resolvers.secondItem,
        )

        assertTrue("scope#1" in result.getOrThrow().path.fullPath)
    }

    @Test
    fun `source resolver selects target from its branch`() {
        val store = mock<RuntimeStore>()
        val rootResolver = mock<ExpressionResolverImpl>()
        val leftResolver = mock<ExpressionResolverImpl>()
        val rightResolver = mock<ExpressionResolverImpl>()
        bindStore(store, rootResolver, leftResolver, rightResolver)
        whenever(store.getUniquePathsAndRuntimes()).thenReturn(emptyMap())
        whenever(store.getOrCreateRuntime(any(), any(), any())).thenAnswer { invocation ->
            val path = invocation.getArgument<String>(0)
            val targetResolver = when {
                "/left" in path -> leftResolver
                "/right" in path -> rightResolver
                else -> rootResolver
            }
            ExpressionsRuntime(targetResolver)
        }
        val rightTarget = text(id = "target", text = "Right")
        val root = container(id = "root", items = listOf(
            container(id = "left", items = listOf(text(id = "target", text = "Left"))),
            container(id = "right", items = listOf(rightTarget)),
        ))

        val result = findTarget(
            root = root,
            rootResolver = rootResolver,
            actionResolver = rightResolver,
        )

        assertSame(rightTarget, result.getOrThrow().div)
    }

    @Test
    fun `source path selects nearest target`() {
        val store = mock<RuntimeStore>()
        val rootResolver = mock<ExpressionResolverImpl>()
        val actionResolver = mock<ExpressionResolverImpl>()
        bindStore(store, rootResolver, actionResolver)
        whenever(store.getOrCreateRuntime(any(), any(), any())).thenReturn(ExpressionsRuntime(rootResolver))
        whenever(store.getUniquePathsAndRuntimes()).thenReturn(mapOf(
            "0:root/right/source" to ExpressionsRuntime(actionResolver),
        ))
        val rightTarget = text(id = "target", text = "Right")
        val root = container(id = "root", items = listOf(
            container(id = "left", items = listOf(text(id = "target", text = "Left"))),
            container(id = "right", items = listOf(rightTarget)),
        ))

        val result = findTarget(
            root = root,
            rootResolver = rootResolver,
            actionResolver = actionResolver,
        )

        assertSame(rightTarget, result.getOrThrow().div)
    }

    private fun findTarget(
        root: Div,
        scopeId: String? = null,
        activeStatePaths: Set<DivStatePath> = emptySet(),
        rootResolver: ExpressionResolver = resolver,
        actionResolver: ExpressionResolver = rootResolver,
        matches: (DivBlock) -> Boolean = { true },
        reportWarning: (Throwable) -> Unit = {},
    ): Result<DivBlock> {
        val locator = DivBlockLocator(
            state = data(root).states.single(),
            rootResolver = rootResolver,
            activeStates = ActiveStateProvider { activeStatePaths },
        )
        return locator.findTarget(
            divId = "target",
            scopeId = scopeId,
            actionResolver = actionResolver,
            matches = matches,
            reportWarning = reportWarning,
        )
    }

    private fun assertFailure(expectedMessage: String, result: Result<DivBlock>) {
        assertEquals(expectedMessage, result.exceptionOrNull()?.message)
    }

    private fun createRepeatedResolvers(): RepeatedResolvers {
        val store = mock<RuntimeStore>()
        val root = mock<ExpressionResolverImpl>()
        val firstItem = mock<ExpressionResolverImpl>()
        val secondItem = mock<ExpressionResolverImpl>()
        bindStore(store, root, firstItem, secondItem)
        whenever(root.validateItemBuilderDataElement(any(), any())).thenAnswer { invocation ->
            invocation.getArgument(0)
        }
        whenever(firstItem.itemBuilderData).thenReturn("data:0")
        whenever(secondItem.itemBuilderData).thenReturn("data:1")
        whenever(store.getUniquePathsAndRuntimes()).thenReturn(emptyMap())
        whenever(store.getOrPutItemBuilderResolver(any(), any())).thenAnswer { invocation ->
            val path = invocation.getArgument<String>(0)
            if (path.endsWith(":0")) {
                firstItem
            } else {
                secondItem
            }
        }
        whenever(store.getOrCreateRuntime(any(), any(), any())).thenAnswer { invocation ->
            ExpressionsRuntime(invocation.getArgument(2))
        }
        whenever(store.resolveRuntimeWith(any(), any(), any(), any())).thenAnswer { invocation ->
            ExpressionsRuntime(invocation.getArgument(2))
        }
        return RepeatedResolvers(root, secondItem)
    }

    private fun bindStore(store: RuntimeStore, vararg resolvers: ExpressionResolverImpl) {
        resolvers.forEach { resolver ->
            whenever(resolver.runtimeStore).thenReturn(store)
        }
    }

    private data class RepeatedResolvers(
        val root: ExpressionResolverImpl,
        val secondItem: ExpressionResolverImpl,
    )
}
