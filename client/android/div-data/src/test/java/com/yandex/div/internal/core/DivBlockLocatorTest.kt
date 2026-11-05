package com.yandex.div.internal.core

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.container
import com.yandex.div.test.data.custom
import com.yandex.div.test.data.data
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import org.mockito.kotlin.mock

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
    fun `null id selects the identical model among equal models without ids`() {
        val root = container(
            items = listOf(text(text = "Target"), text(text = "Target")),
        ) as Div.Container
        val target = root.value.items!![1]

        val result = findTarget(root = root, divId = null, matches = { it.div === target })

        assertSame(target, result.getOrThrow().div)
    }

    @Test
    fun `null id does not restrict matches to models without ids`() {
        val target = text(id = "target", text = "Target")

        val result = findTarget(root = target, divId = null,
            matches = { it.div === target })

        assertSame(target, result.getOrThrow().div)
    }

    @Test
    fun `null id reports a missing model despite an equal model in the tree`() {
        val target = text(text = "Target")

        val result = findTarget(root = text(text = "Target"), divId = null,
            matches = { it.div === target })

        assertFailure("Element without id not found", result)
    }

    @Test
    fun `null id reports ambiguous occurrences of the identical model`() {
        val target = text(text = "Target")
        val repeated = container(items = listOf(target, target))

        val result = findTarget(root = repeated, divId = null,
            matches = { it.div === target })

        assertFailure("Element without id is ambiguous", result)
    }

    @Test
    fun `scope selects an occurrence of the identical model without an id`() {
        val target = text(text = "Target")
        val root = container(items = listOf(
            target,
            container(id = "scope", items = listOf(target)),
        ))

        val result = findTarget(root = root, divId = null, scopeId = "scope", matches = { it.div === target })

        assertEquals("0/scope/child#0", result.getOrThrow().path.fullPath)
    }

    @Test
    fun `missing target is reported`() {
        val result = findTarget(root = container())

        assertFailure("Element with id 'target' not found", result)
    }

    @Test
    fun `missing state is reported`() {
        val locator = DivBlockLocator(
            environment = DivModelSearchEnvironment(state = null, rootResolver = resolver),
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
        val root = state(id = "state", states = listOf(
            state(stateId = "active", div = activeTarget),
            state(stateId = "inactive", div = text(id = "target", text = "Inactive")),
        ))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(DivStatePath.parse("0/state/active")),
        )

        assertSame(activeTarget, result.getOrThrow().div)
    }

    @Test
    fun `nested active states select one of duplicate targets`() {
        val activeTarget = text(id = "target", text = "Active")
        val innerState = state(id = "inner", states = listOf(
            state(stateId = "active", div = activeTarget),
            state(stateId = "inactive", div = text(id = "target", text = "Inner inactive")),
        ))
        val root = state(id = "outer", states = listOf(
            state(stateId = "active", div = innerState),
            state(stateId = "inactive", div = text(id = "target", text = "Outer inactive")),
        ))

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
        val root = state(id = "state", states = listOf(
            state(stateId = "active", div = container()),
            state(stateId = "inactive", div = inactiveTarget),
        ))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(DivStatePath.parse("0/state/active")),
        )

        assertSame(inactiveTarget, result.getOrThrow().div)
    }

    @Test
    fun `duplicate targets only in inactive state are not returned`() {
        val root = state(id = "state", states = listOf(
            state(stateId = "active", div = container()),
            state(stateId = "inactive", div = container(items = listOf(
                text(id = "target", text = "First"),
                text(id = "target", text = "Second"),
            ))),
        ))

        val result = findTarget(
            root = root,
            activeStatePaths = setOf(DivStatePath.parse("0/state/active")),
        )

        assertFailure("Element with id 'target' not found", result)
    }

    @Test
    fun `same generated item is preferred before resolver identity`() {
        val actionResolver = mock<ExpressionResolver>()
        val sameItemTarget = text(id = "target", text = "Same item")
        val sameItemBlock = block(sameItemTarget, mock(), "0/root/item#1/target")
        val sameResolverBlock = block(
            text(id = "target", text = "Same resolver"),
            actionResolver,
            "0/root/item#0/target",
        )
        val environment = TestSearchEnvironment(
            blocks = listOf(sameItemBlock, sameResolverBlock),
            itemSegment = "item#1",
        )

        val result = findTarget(environment, actionResolver)

        assertSame(sameItemTarget, result.getOrThrow().div)
    }

    @Test
    fun `active state is preferred before generated item ownership`() {
        val actionResolver = mock<ExpressionResolver>()
        val activeTarget = text(id = "target", text = "Active")
        val activeBlock = DivBlock.create(
            activeTarget,
            mock(),
            DivStatePath.fromState(0).append("state", "active", "target"),
        )
        val sameItemBlock = DivBlock.create(
            text(id = "target", text = "Inactive same item"),
            actionResolver,
            DivStatePath.fromState(0).append("state", "inactive", "target"),
        )
        val environment = TestSearchEnvironment(
            blocks = listOf(activeBlock, sameItemBlock),
            itemSegment = "inactive",
            activeStates = setOf(DivStatePath.parse("0/state/active")),
        )

        val result = findTarget(
            environment,
            actionResolver,
        )

        assertSame(activeTarget, result.getOrThrow().div)
    }

    @Test
    fun `unknown generated item ownership falls back to resolver identity`() {
        val actionResolver = mock<ExpressionResolver>()
        val sameResolverTarget = text(id = "target", text = "Same resolver")
        val environment = TestSearchEnvironment(blocks = listOf(
            block(text(id = "target", text = "Other resolver"), mock(), "0/root/item#1/target"),
            block(sameResolverTarget, actionResolver, "0/root/item#0/target"),
        ))

        val result = findTarget(environment, actionResolver)

        assertSame(sameResolverTarget, result.getOrThrow().div)
    }

    @Test
    fun `empty environment paths fall back to resolver block paths`() {
        val actionResolver = mock<ExpressionResolver>()
        val targetResolver = mock<ExpressionResolver>()
        val rightTarget = text(id = "target", text = "Right")
        val environment = TestSearchEnvironment(blocks = listOf(
            block(text(id = "target", text = "Left"), targetResolver, "0/root/left/target"),
            block(rightTarget, targetResolver, "0/root/right/target"),
            block(text(id = "source", text = "Source"), actionResolver, "0/root/right/source"),
        ))

        val result = findTarget(environment, actionResolver)

        assertSame(rightTarget, result.getOrThrow().div)
    }

    @Test
    fun `common typed source path selects the nearest target`() {
        val actionResolver = mock<ExpressionResolver>()
        val targetResolver = mock<ExpressionResolver>()
        val rightTarget = text(id = "target", text = "Right")
        val rightPath = DivStatePath.fromState(0, "root").appendDiv("right")
        val environment = TestSearchEnvironment(
            blocks = listOf(
                block(text(id = "target", text = "Left"), targetResolver, "0:root/left/target"),
                block(rightTarget, targetResolver, "0:root/right/target"),
            ),
            sourcePaths = listOf(
                rightPath.appendDiv("first-source"),
                rightPath.appendDiv("second-source"),
            ),
        )

        val result = findTarget(environment, actionResolver)

        assertSame(rightTarget, result.getOrThrow().div)
    }

    @Test
    fun `resolver identity is preferred before source path proximity`() {
        val actionResolver = mock<ExpressionResolver>()
        val sameResolverTarget = text(id = "target", text = "Same resolver")
        val rightPath = DivStatePath.fromState(0, "root").appendDiv("right")
        val environment = TestSearchEnvironment(
            blocks = listOf(
                block(sameResolverTarget, actionResolver, "0:root/left/target"),
                block(text(id = "target", text = "Nearest"), mock(), "0:root/right/target"),
            ),
            sourcePaths = listOf(rightPath.appendDiv("source")),
        )

        val result = findTarget(environment, actionResolver)

        assertSame(sameResolverTarget, result.getOrThrow().div)
    }

    @Test
    fun `extension path includes the containing model branch`() {
        val root = container(
            id = "root",
            items = listOf(
                container(
                    id = "branch",
                    items = listOf(
                        text(
                            id = "target",
                            text = "Target",
                            extensions = listOf(DivExtension(id = "counter")),
                        ),
                    ),
                ),
            ),
        ) as Div.Container
        val target = (root.value.items!!.single() as Div.Container).value.items!!.single()

        val path = resolveExtensionPath(root, target)

        assertEquals(DivStatePath.fromState(0).appendDiv("root").appendDiv("branch").appendDiv("target"), path)
    }

    @Test
    fun `extension path retains nested state ancestry`() {
        val root = state(
            id = "outer",
            states = listOf(
                state(
                    stateId = "visible",
                    div = state(
                        id = "inner",
                        states = listOf(
                            state(
                                stateId = "selected",
                                div = text(
                                    id = "target",
                                    text = "Target",
                                    extensions = listOf(DivExtension(id = "counter")),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ) as Div.State
        val inner = root.value.states.single().div as Div.State
        val target = inner.value.states.single().div!!

        val path = resolveExtensionPath(root, target)

        assertEquals(DivStatePath.fromState(0).appendDiv("outer")
            .append("outer", "visible", "inner").append("inner", "selected", "target"), path)
    }

    @Test
    fun `extension path uses the root state div id`() {
        val root = state(
            id = "rendered",
            divId = "runtime",
            states = listOf(
                state(
                    stateId = "selected",
                    div = text(
                        id = "target",
                        text = "Target",
                        extensions = listOf(DivExtension(id = "counter")),
                    ),
                ),
            ),
        ) as Div.State
        val target = root.value.states.single().div!!

        val path = resolveExtensionPath(root, target)

        assertEquals(DivStatePath.fromState(0).appendDiv("runtime").append("runtime", "selected", "target"), path)
    }

    @Test
    fun `custom children with ids resolve to their model path`() {
        val root = custom(
            type = "nested",
            id = "custom",
            items = listOf(
                text(
                    id = "target",
                    text = "Target",
                    extensions = listOf(DivExtension(id = "counter")),
                ),
            ),
        ) as Div.Custom
        val target = root.value.items!!.single()

        val path = resolveExtensionPath(root, target, DivStatePath.fromState(0).appendDiv("custom"))

        assertEquals("0/custom/target", path.fullPath)
    }

    private fun resolveExtensionPath(
        root: Div,
        target: Div,
        localPath: DivStatePath = DivStatePath.fromState(0),
    ): DivStatePath {
        val locator = DivBlockLocator(
            environment = DivModelSearchEnvironment(DivData.State(stateId = 0, div = root), resolver),
        )
        return locator.resolveDivPath(target, resolver, localPath).getOrThrow()
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
            environment = DivModelSearchEnvironment(
                data(root).states.single(), rootResolver, activeStatePaths,
            ),
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

    private fun findTarget(
        environment: DivBlockSearchEnvironment,
        actionResolver: ExpressionResolver,
    ): Result<DivBlock> {
        return DivBlockLocator(environment).findTarget(
            divId = "target",
            scopeId = null,
            actionResolver = actionResolver,
            matches = { true },
        )
    }

    private fun block(div: Div, resolver: ExpressionResolver, path: String): DivBlock {
        val segments = path.split('/')
        val stateId = segments.first().substringBefore(':').toLong()
        return DivBlock.create(div, resolver, DivStatePath(stateId, path = segments))
    }
}

private class TestSearchEnvironment(
    var blocks: List<DivBlock>,
    private val sourcePaths: Collection<DivStatePath> = emptyList(),
    private val itemSegment: String? = null,
    private val activeStates: Set<DivStatePath> = emptySet(),
) : DivBlockSearchEnvironment {
    override fun rootBlock(): DivBlock? = blocks.firstOrNull()

    override fun findDescendants(root: DivBlock): Iterable<DivBlock> = blocks

    override fun activeStatesPaths(): Set<DivStatePath> = activeStates

    override fun findPathsFor(resolver: ExpressionResolver): Collection<DivStatePath> = sourcePaths

    override fun itemBuilderSegment(resolver: ExpressionResolver): String? = itemSegment
}
