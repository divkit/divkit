package com.yandex.div.core.state

import com.yandex.div.core.path
import com.yandex.div.core.state.DivPathUtils.compactPathList
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.view2.divs.UnitTestData
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivState
import junit.framework.AssertionFailedError
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivStatePathUtilsTest {

    private val rootPath = DivStatePath(0, getStates(0), getPath(0))
    private val firstLvlPath = DivStatePath(0, getStates(1), getPath(1))
    private val scndLvlPath = DivStatePath(0, getStates(2), getPath(2))
    private val thrdLvlPath = DivStatePath(0, getStates(3), getPath(3))
    private val testData = UnitTestData("div-state", "state_tree.json")
    private val div = testData.div
    private val resolver = mock<ExpressionResolver>()

    @Test
    fun `find div state by path finds first container`() {
        val firstStateContainers = listOf("container_item_one", "container_item_two", "container_item_three")

        firstStateContainers.forEach { containerStateId: String ->
            val path = DivStatePath.parse("0/state_container/first/$containerStateId/two")

            val div: Div? = div.findDivState(path, resolver)

            Assert.assertNotNull(div)
            val divState: DivState = div?.value() as? DivState
                ?: throw AssertionFailedError("DivStatePath#findDivState found smthng wrong")
            Assert.assertEquals(divState.id, containerStateId)
            Assert.assertEquals(divState.states.size, 3)
        }
    }

    @Test
    fun `find div state by path finds second container`() {
        val path = DivStatePath.parse("0/state_container/second/second_state/hidden")

        val div: Div? = div.findDivState(path, resolver)

        Assert.assertNotNull(div)
        val divState: DivState = div?.value() as? DivState
            ?: throw AssertionFailedError("DivStatePath#findDivState found smthng wrong")
        Assert.assertEquals(divState.id, "second_state")
        Assert.assertEquals(divState.states.size, 3)
        Assert.assertEquals(divState.states[0].stateId, "hidden")
        Assert.assertEquals(divState.states[1].stateId, "shown")
        Assert.assertEquals(divState.states[2].stateId, "clicked")
    }

    @Test
    fun `find div state by path finds root container`() {
        val path = DivStatePath.parse("0/state_container/second")

        val div: Div? = div.findDivState(path, resolver)

        Assert.assertNotNull(div)
        val divState: DivState = div?.value() as? DivState
            ?: throw AssertionFailedError("DivStatePath#findDivState found smthng wrong")
        Assert.assertEquals(divState.id, "state_container")
        Assert.assertEquals(divState.states.size, 2)
        Assert.assertEquals(divState.states[0].stateId, "first")
        Assert.assertEquals(divState.states[1].stateId, "second")
    }

    @Test
    fun `find div state by root path finds nothing`() {
        val path = DivStatePath.fromState(0)

        val div: Div? = div.findDivState(path, resolver)

        Assert.assertNull(div)
    }

    @Test
    fun `find div state by incorrect path fails`() {
        val path = scndLvlPath

        val div: Div? = div.findDivState(path, resolver)

        Assert.assertNull(div)
    }

    @Test
    fun `sharedAncestor works ok`() {
        Assert.assertEquals(DivStatePath.lowestCommonAncestor(thrdLvlPath, scndLvlPath), scndLvlPath)
        Assert.assertEquals(DivStatePath.lowestCommonAncestor(thrdLvlPath, firstLvlPath), firstLvlPath)
        Assert.assertEquals(DivStatePath.lowestCommonAncestor(thrdLvlPath, rootPath), rootPath)
        Assert.assertEquals(DivStatePath.lowestCommonAncestor(thrdLvlPath, thrdLvlPath), thrdLvlPath)

        val notSharedState = DivStatePath(0, listOf("one" to "another"), listOf("0", "one", "another"))
        Assert.assertEquals(DivStatePath.lowestCommonAncestor(thrdLvlPath, notSharedState), rootPath)

        val nestedState = scndLvlPath.append("some", DivState.State(stateId = "state"), "state")
        Assert.assertEquals(DivStatePath.lowestCommonAncestor(thrdLvlPath, nestedState), scndLvlPath)

        val pseudoThrdLvl = DivStatePath(
            0,
            mutableListOf("one" to "another").apply { addAll(getStates(2)) },
            mutableListOf("0", "one", "another").apply { addAll(getPath(2)) }
        )

        Assert.assertEquals(DivStatePath.lowestCommonAncestor(pseudoThrdLvl, thrdLvlPath), rootPath)
    }

    @Test
    fun `compactPathList leaves only parent paths`() {
        val parent1 = "0/first_state/first_div".path
        val children1 = listOf(
            "0/first_state/first_div/state_1/div".path,
            "0/first_state/first_div/state_2/div_1/state/div_2".path,
            "0/first_state/first_div/state_3/div_3/state/div_4".path
        )
        Assert.assertEquals(compactPathList(children1 + parent1), listOf(parent1))

        val parent2 = listOf(
            "0/state_1/div_1".path,
            "0/state_2/div_2".path,
            "0/state_3/div_3".path,
        )
        Assert.assertEquals(compactPathList(parent2), parent2)
    }
}

private fun getStates(deepness: Int): List<Pair<String, String>> {
    val states = mutableListOf<Pair<String, String>>()
    repeat(deepness) {
        states.add("stateName$it" to "stateValue$it")
    }
    return states
}

private fun getPath(deepness: Int): List<String> {
    val path = mutableListOf("0")
    repeat(deepness) {
        path.add("stateName$it")
        path.add("stateValue$it")
    }
    return path
}
