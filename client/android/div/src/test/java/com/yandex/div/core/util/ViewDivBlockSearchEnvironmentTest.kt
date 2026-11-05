package com.yandex.div.core.util

import com.yandex.div.core.actions.actionTargetBlockLocator
import com.yandex.div.core.dagger.DivDataComponent
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.core.expression.local.RuntimeStore
import com.yandex.div.core.state.DivPathUtils.fromState
import com.yandex.div.core.state.DivStateManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.DivBlockLocator
import com.yandex.div.internal.core.DivBlockSearchEnvironment
import com.yandex.div.internal.core.buildItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivData
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.json.JSONArray
import org.json.JSONObject
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class ViewDivBlockSearchEnvironmentTest {

    private val resolver = ExpressionResolver.EMPTY

    @Test
    fun `reused action locator selects target from the newly active state`() {
        val selectedTarget = text(id = "target", text = "Selected")
        val card = data(state(id = "switcher", states = listOf(
            state(stateId = "initial", div = text(id = "target", text = "Initial")),
            state(stateId = "selected", div = selectedTarget),
        )))
        val stateManager = DivStateManager("card", mock())
        val component = mock<DivDataComponent> { on { this.stateManager } doReturn stateManager }
        val divView = mock<Div2View> {
            on { divData } doReturn card
            on { stateId } doReturn 0L
            on { expressionResolver } doReturn resolver
            on { dataComponent } doReturn component
        }
        val locator = requireNotNull(divView.actionTargetBlockLocator())
        locator.findTarget("target", null, resolver, matches = { true }).getOrThrow()
        stateManager.updateStates(DivStatePath.parse("0/switcher/selected"), temporary = true)

        val result = locator.findTarget("target", null, resolver, matches = { true })

        assertSame(selectedTarget, result.getOrThrow().div)
    }

    @Test
    fun `scope does not include target from inactive state with the same path`() {
        val root = state(id = "state", states = listOf(
            state(stateId = "active", div = container(
                id = "scope",
                items = listOf(text(id = "placeholder", text = "Placeholder")),
            )),
            state(stateId = "inactive", div = container(
                id = "scope",
                items = listOf(text(id = "target", text = "Target")),
            )),
        ))
        val cardState = data(root).states.single()
        val scopePaths = root.walk(resolver, DivStatePath.fromState(cardState))
            .filter { block -> block.div.value().id == "scope" }
            .map { block -> block.path.fullPath }
            .toList()
        val locator = DivBlockLocator(
            environment = createEnvironment(
                cardState, resolver, activeStates = setOf(DivStatePath.parse("0/state/active")),
            ),
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
    fun `generated model fallback preserves paths from the View item builder`() {
        val resolvers = createRepeatedResolvers()
        val card = data(
            container(
                id = "root",
                itemBuilder = DivCollectionItemBuilder(
                    data = constant(JSONArray("[{}, {}]")),
                    prototypes = listOf(
                        DivCollectionItemBuilder.Prototype(
                            div = text(text = "Item"),
                            id = constant("item"),
                        ),
                    ),
                ),
            ),
        )
        val cardState = card.states.single()
        val root = cardState.div as Div.Container
        val items = root.value.buildItems(resolvers.root, DivStatePath.fromState(cardState))
        val locator = DivBlockLocator(createEnvironment(cardState, resolvers.root))

        val paths = items.map {
            locator.resolveDivPath(it.div, it.expressionResolver, it.path).getOrThrow().fullPath
        }

        assertEquals(listOf("0:root/item#0", "0:root/item#1"), paths)
        assertEquals(items.map { it.path.fullPath }, paths)
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

    @Test
    fun `runtime paths are converted to typed paths and unsupported paths are ignored`() {
        val store = mock<RuntimeStore>()
        val actionResolver = mock<ExpressionResolverImpl>()
        bindStore(store, actionResolver)
        whenever(store.getUniquePathsAndRuntimes()).thenReturn(linkedMapOf(
            "" to ExpressionsRuntime(actionResolver),
            "unsupported/root" to ExpressionsRuntime(actionResolver),
            "0:root/right/source" to ExpressionsRuntime(actionResolver),
        ))
        val environment = ViewDivBlockSearchEnvironment(mock())

        val paths = environment.findPathsFor(actionResolver)

        assertEquals(listOf("0:root/right/source"), paths.map { it.fullPath })
    }

    @Test
    fun `walk includes the origin and preserves depth first search order`() {
        val root = container(id = "root", items = listOf(
            container(id = "left", items = listOf(text(id = "nested", text = "Nested"))),
            text(id = "right", text = "Right"),
        ))
        val environment = createEnvironment(data(root).states.single(), resolver)

        val ids = environment.findDescendants(environment.rootBlock()!!).map { it.div.value().id }

        assertEquals(listOf("root", "left", "nested", "right"), ids)
    }

    private fun findTarget(
        root: Div,
        divId: String? = "target",
        scopeId: String? = null,
        activeStatePaths: Set<DivStatePath> = emptySet(),
        rootResolver: ExpressionResolver = resolver,
        actionResolver: ExpressionResolver = rootResolver,
        matches: (DivBlock) -> Boolean = { true },
        reportWarning: (Throwable) -> Unit = {},
    ): Result<DivBlock> {
        val locator = DivBlockLocator(
            environment = createEnvironment(data(root).states.single(), rootResolver, activeStatePaths),
        )
        return locator.findTarget(
            divId = divId,
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

    private fun createEnvironment(
        state: DivData.State,
        rootResolver: ExpressionResolver,
        activeStates: Set<DivStatePath> = emptySet(),
    ): DivBlockSearchEnvironment {
        val environment = ViewDivBlockSearchEnvironment(mock())
        return object : DivBlockSearchEnvironment by environment {
            override fun activeStatesPaths(): Set<DivStatePath> = activeStates

            override fun rootBlock(): DivBlock {
                return DivBlock.create(state.div, rootResolver, DivStatePath.fromState(state))
            }
        }
    }

    private data class RepeatedResolvers(
        val root: ExpressionResolverImpl,
        val secondItem: ExpressionResolverImpl,
    )
}
