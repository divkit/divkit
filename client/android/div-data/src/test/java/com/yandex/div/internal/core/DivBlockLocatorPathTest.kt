package com.yandex.div.internal.core

import com.yandex.div.core.state.DivStatePath
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.test.data.constant
import com.yandex.div.test.data.container
import com.yandex.div.test.data.state
import com.yandex.div.test.data.text
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivExtension
import com.yandex.div2.DivVisibility
import kotlin.test.Test
import kotlin.test.assertEquals

class DivBlockLocatorPathTest {
    private val resolver = ExpressionResolver.EMPTY
    private val localPath = DivStatePath.fromState(0)

    @Test
    fun `siblings without ids have distinct model paths`() {
        val root = container(
            id = "root",
            items = listOf(text(text = "First"), text(text = "Second")),
        ) as Div.Container
        val children = root.value.items!!
        val locator = createLocator(root)

        val paths = children.map {
            locator.resolveDivPath(it, resolver, localPath).getOrThrow().fullPath
        }

        assertEquals(listOf("0/root/child#0", "0/root/child#1"), paths)
    }

    @Test
    fun `local paths select models among duplicate ids in different containers`() {
        val root = container(
            id = "root",
            items = listOf(
                container(id = "left", items = listOf(text(id = "target", text = "Left"))),
                container(id = "right", items = listOf(text(id = "target", text = "Right"))),
            ),
        ) as Div.Container
        val left = (root.value.items!![0] as Div.Container).value.items!!.single()
        val right = (root.value.items!![1] as Div.Container).value.items!!.single()
        val locator = createLocator(root)

        val paths = listOf(
            locator.resolveDivPath(left, resolver, DivStatePath.parse("0/root/left")).getOrThrow().fullPath,
            locator.resolveDivPath(right, resolver, DivStatePath.parse("0/root/right")).getOrThrow().fullPath,
        )

        assertEquals(listOf("0/root/left/target", "0/root/right/target"), paths)
    }

    @Test
    fun `identity selects a model with a duplicate id under a shared local prefix`() {
        val root = container(
            id = "root",
            items = listOf(
                text(
                    id = "target",
                    text = "First",
                    extensions = listOf(DivExtension(id = "counter")),
                ),
                text(
                    id = "target",
                    text = "Second",
                    extensions = listOf(DivExtension(id = "counter")),
                ),
            ),
        ) as Div.Container
        val second = root.value.items!![1]
        val locator = createLocator(root)

        val result = locator.resolveDivPath(second, resolver, localPath)

        assertEquals("0/root/target#1", result.getOrThrow().fullPath)
    }

    @Test
    fun `equal model without id does not match an absent instance`() {
        val locator = createLocator(container(items = listOf(text(text = "Absent"))))

        val result = locator.resolveDivPath(text(text = "Absent"), resolver, localPath)

        assertEquals("Element without id not found", result.exceptionOrNull()?.message)
    }

    @Test
    fun `ambiguous model without id fails instead of retaining the state path`() {
        val target = text(text = "Repeated")
        val locator = createLocator(repeatTarget(target))

        val result = locator.resolveDivPath(target, resolver, localPath)

        assertEquals("Element without id is ambiguous", result.exceptionOrNull()?.message)
    }

    @Test
    fun `repeated resolution preserves the path of a model without id`() {
        val root = container(id = "root", items = listOf(text(text = "Target"))) as Div.Container
        val target = root.value.items!!.single()
        val locator = createLocator(root)
        val firstPath = locator.resolveDivPath(target, resolver, localPath).getOrThrow()

        val secondPath = locator.resolveDivPath(target, resolver, localPath).getOrThrow()

        assertEquals(firstPath, secondPath)
    }

    @Test
    fun `local path does not disambiguate a shared model without id`() {
        val target = text(text = "Repeated")
        val locator = createLocator(repeatTarget(target, id = "root"))
        val childPath = localPath.appendDiv("root").appendDiv("child#1")

        val result = locator.resolveDivPath(target, resolver, childPath)

        assertEquals("Element without id is ambiguous", result.exceptionOrNull()?.message)
    }

    @Test
    fun `locator preserves child path segments including hidden siblings`() {
        val paths = createLocator(siblings)

        val segments = resolveChildSegments(paths, localPath.appendDiv("root"))

        assertEquals(listOf("child#0", "child#1", "duplicate#2", "duplicate#3"), segments)
    }

    @Test
    fun `model path is resolved without extensions`() {
        val root = container(
            id = "root",
            items = listOf(
                container(id = "branch", items = listOf(text(id = "target", text = "Target"))),
            ),
        ) as Div.Container
        val target = (root.value.items!!.single() as Div.Container).value.items!!.single()
        val locator = createLocator(root)

        val path = locator.resolveDivPath(target, resolver, localPath)

        assertEquals("0/root/branch/target", path.getOrThrow().fullPath)
    }

    @Test
    fun `identity selects a model with id even when the local path differs`() {
        val root = container(
            id = "root",
            items = listOf(
                text(
                    id = "target",
                    text = "Other",
                    extensions = listOf(DivExtension(id = "counter")),
                ),
                text(id = "target", text = "Target"),
            ),
        ) as Div.Container
        val target = root.value.items!![1]
        val locator = createLocator(root)

        val path = locator.resolveDivPath(target, resolver, localPath.appendDiv("unrelated"))

        assertEquals("0/root/target#1", path.getOrThrow().fullPath)
    }

    @Test
    fun `active state selects a shared model without id even when local state is inactive`() {
        val target = text(text = "Target")
        val root = reuseTargetInStates(target)
        val locator = createLocator(root, activeStates = setOf(DivStatePath.parse("0/state/active")))

        val path = locator.resolveDivPath(target, resolver, DivStatePath.parse("0/state/inactive"))

        assertEquals("0/state/active", path.getOrThrow().statesString)
    }

    @Test
    fun `active state selects a shared model when local state is unavailable`() {
        val target = text(id = "target", text = "Target")
        val root = reuseTargetInStates(target)
        val locator = createLocator(root, activeStates = setOf(DivStatePath.parse("0/state/active")))

        val path = locator.resolveDivPath(target, resolver, localPath)

        assertEquals("0/state/active", path.getOrThrow().statesString)
    }

    @Test
    fun `generated model falls back to the unique matching id without extensions`() {
        val generatedTarget = text(id = "target", text = "Generated")
        val root = container(
            id = "root",
            items = listOf(
                container(id = "branch", items = listOf(text(id = "target", text = "Prototype"))),
            ),
        )
        val locator = createLocator(root)

        val path = locator.resolveDivPath(generatedTarget, resolver, localPath)

        assertEquals("0/root/branch/target", path.getOrThrow().fullPath)
    }

    @Test
    fun `local full path selects a generated block among repeated ids`() {
        val root = container(
            id = "root",
            items = listOf(
                container(id = "left", items = listOf(text(id = "target", text = "Left"))),
                container(id = "right", items = listOf(text(id = "target", text = "Right"))),
            ),
        )
        val locator = createLocator(root)
        val parentPath = localPath.appendDiv("root").appendDiv("right")

        val path = locator.resolveDivPath(text(id = "target", text = "Generated"), resolver, parentPath)

        assertEquals("0/root/right/target", path.getOrThrow().fullPath)
    }

    @Test
    fun `target prefix of the local path selects a generated block`() {
        val locator = createLocator(container(
            id = "root",
            items = listOf(text(id = "target", text = "Prototype")),
        ))
        val childPath = localPath.appendDiv("root").appendDiv("target").appendDiv("child")

        val path = locator.resolveDivPath(text(id = "target", text = "Generated"), resolver, childPath)

        assertEquals("0/root/target", path.getOrThrow().fullPath)
    }

    @Test
    fun `generated model in tooltip namespace does not match a host card block`() {
        val locator = createLocator(text(id = "target", text = "Host"))
        val tooltipPath = localPath.appendDiv("target").withNamespace("tooltip")

        val path = locator.resolveDivPath(
            text(id = "target", text = "Tooltip"), resolver, tooltipPath,
        )

        assertEquals("Element with id 'target' not found", path.exceptionOrNull()?.message)
    }

    @Test
    fun `generated block outside the local full path returns a lookup failure`() {
        val locator = createLocator(container(
            id = "root",
            items = listOf(text(id = "target", text = "Prototype")),
        ))

        val path = locator.resolveDivPath(text(id = "target", text = "Generated"), resolver,
            localPath.appendDiv("unrelated"))

        assertEquals("Element with id 'target' not found", path.exceptionOrNull()?.message)
    }

    @Test
    fun `missing model with an id returns a lookup failure`() {
        val locator = createLocator(container())

        val path = locator.resolveDivPath(text(id = "target", text = "Target"), resolver,
            DivStatePath.parse("0/state/active"))

        assertEquals("Element with id 'target' not found", path.exceptionOrNull()?.message)
    }

    @Test
    fun `missing model without an id returns a lookup failure`() {
        val locator = createLocator(container())

        val path = locator.resolveDivPath(text(text = "Target"), resolver, localPath)

        assertEquals("Element without id not found", path.exceptionOrNull()?.message)
    }

    @Test
    fun `ambiguous model occurrences fail instead of using a local fallback`() {
        val target = text(id = "target", text = "Target")
        val locator = createLocator(repeatTarget(target))

        val error = locator.resolveDivPath(target, resolver, localPath).exceptionOrNull()

        assertEquals("Element with id 'target' is ambiguous", error?.message)
    }

    @Test
    fun `ambiguous generated model fallback fails instead of selecting another block`() {
        val root = container(
            items = listOf(
                text(id = "target", text = "First"),
                text(id = "target", text = "Second"),
            ),
        )
        val locator = createLocator(root)

        val error = locator.resolveDivPath(text(id = "target", text = "Generated"), resolver, localPath)
            .exceptionOrNull()

        assertEquals("Element with id 'target' is ambiguous", error?.message)
    }

    @Test
    fun `reused locator resolves the model path from the updated card`() {
        val target = text(id = "target", text = "Target")
        val original = (container(id = "initial", items = listOf(target)) as Div.Container).value
        var currentState = DivData.State(stateId = 0, div = Div.Container(original))
        val environment = object : DivBlockSearchEnvironment by DivModelSearchEnvironment(currentState, resolver) {
            override fun rootBlock(): DivBlock? = DivModelSearchEnvironment(currentState, resolver).rootBlock()
        }
        val locator = DivBlockLocator(environment)
        locator.resolveDivPath(target, resolver, localPath).getOrThrow()
        currentState = currentState.copy(div = Div.Container(original.copy(id = "updated")))

        val path = locator.resolveDivPath(target, resolver, localPath).getOrThrow()

        assertEquals("0/updated/target", path.fullPath)
    }

    private fun repeatTarget(target: Div, id: String? = null): Div =
        container(id = id, items = listOf(target, target))

    private fun reuseTargetInStates(target: Div): Div = state(id = "state", states = listOf(
        state(stateId = "active", div = target),
        state(stateId = "inactive", div = target),
    ))

    private val siblings = container(
        id = "root",
        items = listOf(
            text(text = "Hidden", visibility = constant(DivVisibility.GONE)),
            text(text = "Visible"),
            text(id = "duplicate", text = "First"),
            text(id = "duplicate", text = "Second"),
        ),
    ) as Div.Container

    private fun resolveChildSegments(locator: DivBlockLocator, parentPath: DivStatePath): List<String> {
        val localPaths = listOf(parentPath, parentPath,
            parentPath.appendDiv("duplicate#2"), parentPath.appendDiv("duplicate#3"))
        return siblings.value.items!!.zip(localPaths).map { (div, path) ->
            locator.resolveDivPath(div, resolver, path).getOrThrow().lastDivId
        }
    }

    private fun createLocator(
        root: Div,
        rootResolver: ExpressionResolver = resolver,
        activeStates: Set<DivStatePath> = emptySet(),
    ): DivBlockLocator {
        return DivBlockLocator(
            environment = DivModelSearchEnvironment(
                DivData.State(stateId = 0, div = root), rootResolver, activeStates,
            ),
        )
    }
}
