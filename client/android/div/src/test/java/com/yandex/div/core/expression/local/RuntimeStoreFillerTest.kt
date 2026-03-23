package com.yandex.div.core.expression.local

import android.graphics.Color
import android.net.Uri
import com.yandex.div.core.expression.ExpressionResolverImpl
import com.yandex.div.core.expression.ExpressionsRuntime
import com.yandex.div.json.expressions.Expression
import com.yandex.div.test.data.data
import com.yandex.div.test.data.variable
import com.yandex.div2.Div
import com.yandex.div2.DivCircleShape
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEvaluableType
import com.yandex.div2.DivFunction
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGifImage
import com.yandex.div2.DivGrid
import com.yandex.div2.DivImage
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivInput
import com.yandex.div2.DivPageContentSize
import com.yandex.div2.DivPager
import com.yandex.div2.DivPagerLayoutMode
import com.yandex.div2.DivSelect
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSlider
import com.yandex.div2.DivState
import com.yandex.div2.DivSwitch
import com.yandex.div2.DivTabs
import com.yandex.div2.DivText
import com.yandex.div2.DivTrigger
import com.yandex.div2.DivVariable
import com.yandex.div2.DivVideo
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class RuntimeStoreFillerTest {

    private val store: RuntimeStoreImpl = mock<RuntimeStoreImpl> {
        on { putRuntime(any(), any(), anyOrNull()) } doAnswer {
            runtimePaths.add(it.arguments[1] as String)
            Unit
        }
        on { getOrPutItemBuilderResolver(any(), any()) } doAnswer {
            createResolver(it.arguments[0] as String)
        }
    }
    private val rootRuntime = ExpressionsRuntime(createResolver(""))
    private val runtimeProvider = mock<ExpressionsRuntimeProvider> {
        on { createRootRuntime(any(), any(), any()) } doReturn rootRuntime
        on { createChildRuntime(any(), any(), any(), any()) } doAnswer {
            val path = it.arguments[0] as String
            ExpressionsRuntime(createResolver(path))
        }
        on { createRuntimeWithResolver(any(), any(), any()) } doAnswer {
            ExpressionsRuntime(it.arguments[1] as ExpressionResolverImpl)
        }
    }
    private val runtimePaths = mutableSetOf<String>()

    private val underTest = RuntimeStoreFiller(runtimeProvider, mock())

    private fun createResolver(path: String) = ExpressionResolverImpl(path, store, mock(), mock(), mock())

    @Test
    fun `create runtime for every root state`() {
        val text1 = createText()
        val container1 = createContainer(items = listOf(text1))
        val text2 = createText()
        val container2 = createContainer(items = listOf(text2))
        val states = listOf(DivData.State(container1, 0), DivData.State(container2, 1))
        val data = createData(states)

        underTest.fillStore(store, data)

        assertEquals(5, runtimePaths.size)
    }

    @Test
    fun `create runtime for every div`() {
        val text1 = createText()
        val text2 = createText()
        val text3 = createText()
        val container1 = createContainer(items = listOf(text3))
        val container2 = createContainer(items = listOf(text1, text2, container1))
        val data = data(content = container2)

        underTest.fillStore(store, data)

        assertEquals(6, runtimePaths.size)
    }

    @Test
    fun `create runtime for root div with id`() {
        val text = createText("root_id")
        val data = data(content = text)

        underTest.fillStore(store, data)

        assertPaths(2, "", "0:root_id")
    }

    @Test
    fun `create runtime for root div without id`() {
        val text = createText()
        val data = data(content = text)

        underTest.fillStore(store, data)

        assertPaths(2, "", "0")
    }

    @Test
    fun `create runtime for div with id`() {
        val text = createText("text_id")
        val container = createContainer(items = listOf(text))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(3, "", "0", "0/text_id")
    }

    @Test
    fun `create runtime for div without id`() {
        val text = createText()
        val container = createContainer(items = listOf(text))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(3, "", "0", "0/child#0")
    }

    @Test
    fun `create runtime for gallery with items`() {
        val text1 = createText("item1")
        val text2 = createText()
        val gallery = createGallery(items = listOf(text1, text2))
        val data = data(content = gallery)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/item1", "0/child#1")
    }

    @Test
    fun `create runtime for pager with items`() {
        val text1 = createText("page1")
        val text2 = createText()
        val pager = createPager(items = listOf(text1, text2))
        val data = data(content = pager)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/page1", "0/child#1")
    }

    @Test
    fun `create runtime for grid`() {
        val text1 = createText("item1")
        val text2 = createText()
        val grid = Div.Grid(DivGrid(columnCount = Expression.constant(2), items = listOf(text1, text2)))
        val data = data(content = grid)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/item1", "0/child#1")
    }

    @Test
    fun `create runtime for tabs`() {
        val text1 = createText("tab1")
        val text2 = createText()
        val items = listOf(text1, text2).map { DivTabs.Item(div = it, title = Expression.constant("Tab")) }
        val tabs = Div.Tabs(DivTabs(items = items))
        val data = data(content = tabs)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/tab1", "0/child#1")
    }

    @Test
    fun `create runtime for state div`() {
        val states = listOf(createText("text1"), createText()).mapIndexed { index, div ->
            DivState.State(div = div, stateId = "state$index")
        }
        val state = Div.State(DivState(id = "state_id", states = states))
        val data = data(content = state)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0:state_id", "0:state_id/text1", "0:state_id/state1")
    }

    @Test
    fun `create runtime for custom div`() {
        val text1 = createText("custom_item1")
        val text2 = createText()
        val custom = Div.Custom(DivCustom(customType = "custom", id = "custom_id", items = listOf(text1, text2)))
        val data = data(content = custom)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0:custom_id", "0:custom_id/custom_item1", "0:custom_id/child#1")
    }

    @Test
    fun `create runtime for leaf divs`() {
        val url = Expression.constant(Uri.EMPTY)
        val image = Div.Image(DivImage(id = "image_id", imageUrl = url))
        val gifImage = Div.GifImage(DivGifImage(id = "gif_id", gifUrl = url))
        val separator = Div.Separator(DivSeparator(id = "separator_id"))
        val indicator = Div.Indicator(DivIndicator(id = "indicator_id"))
        val drawable =
            DivDrawable.Shape(DivShapeDrawable(Expression.constant(Color.BLACK), DivShape.Circle(DivCircleShape())))
        val slider = Div.Slider(DivSlider(
            id = "slider_id",
            thumbStyle = drawable,
            trackActiveStyle = drawable,
            trackInactiveStyle = drawable
        ))
        val input = Div.Input(DivInput(id = "input_id", textVariable = "var"))
        val select = Div.Select(DivSelect(id = "select_id", options = listOf(), valueVariable = "var"))
        val video = Div.Video(DivVideo(id = "video_id", videoSources = listOf()))
        val switch = Div.Switch(DivSwitch(id = "switch_id", isOnVariable = "var"))

        val container = createContainer("container_id", listOf(
            image, gifImage, separator, indicator, slider, input, select, video, switch
        ))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(11, "", "0:container_id", "0:container_id/image_id", "0:container_id/gif_id",
            "0:container_id/separator_id", "0:container_id/indicator_id", "0:container_id/slider_id",
            "0:container_id/input_id", "0:container_id/select_id", "0:container_id/video_id",
            "0:container_id/switch_id")
    }

    @Test
    fun `create runtime for nested containers with mixed ids`() {
        val text1 = createText("text1")
        val text2 = createText()
        val innerContainer = createContainer("inner", listOf(text1, text2))
        val text3 = createText("text3")
        val outerContainer = createContainer(items = listOf(innerContainer, text3))
        val data = data(content = outerContainer)

        underTest.fillStore(store, data)

        assertPaths(6, "", "0", "0/inner", "0/inner/text1", "0/inner/child#1", "0/text3")
    }

    @Test
    fun `create runtime for duplicate ids with index suffix`() {
        val text1 = createText("duplicate")
        val text2 = createText("duplicate")
        val text3 = createText("duplicate")
        val container = createContainer("container", listOf(text1, text2, text3))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(5, "", "0:container", "0:container/duplicate#0", "0:container/duplicate#1",
            "0:container/duplicate#2")
    }

    @Test
    fun `fillStore returns root runtime`() {
        val data = data(content = createText())
        val result = underTest.fillStore(store, data)
        assertSame(rootRuntime, result)
    }

    @Test
    fun `create runtime for container with null items`() {
        val data = data(content = createContainer())

        underTest.fillStore(store, data)

        assertPaths(2, "", "0")
    }

    @Test
    fun `create runtime for container with empty items`() {
        val container = createContainer(items = emptyList())
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(2, "", "0")
    }

    @Test
    fun `create child runtime for div with variables`() {
        val text = createText("var_div", variables = listOf(variable("x", "")))
        val container = createContainer(items = listOf(text))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(3, "", "0", "0/var_div")
        verify(runtimeProvider).createChildRuntime(any(), eq(text), any(), any())
    }

    @Test
    fun `create child runtime for div with functions`() {
        val text = createText("var_div", functions = listOf(DivFunction(emptyList(), "", "", DivEvaluableType.INTEGER)))
        val container = createContainer(items = listOf(text))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(3, "", "0", "0/var_div")
        verify(runtimeProvider).createChildRuntime(any(), eq(text), any(), any())
    }

    @Test
    fun `copy resolver for div with triggers`() {
        val text = createText("var_div", triggers = listOf(DivTrigger(emptyList(), Expression.constant(true))))
        val container = createContainer(items = listOf(text))
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(3, "", "0", "0/var_div")
        verify(runtimeProvider, never()).createChildRuntime(any(), eq(text), any(), any())
    }

    @Test
    fun `create runtime for items from builder in container`() {
        val builder = createItemBuilder()
        val container = createContainer(builder = builder)
        val data = data(content = container)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/built_item", "0/child#1")
    }

    @Test
    fun `create runtime for items from builder in gallery`() {
        val builder = createItemBuilder()
        val gallery = createGallery(builder = builder)
        val data = data(content = gallery)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/built_item", "0/child#1")
    }

    @Test
    fun `create runtime for items from builder in pager`() {
        val builder = createItemBuilder()
        val pager = createPager(builder = builder)
        val data = data(content = pager)

        underTest.fillStore(store, data)

        assertPaths(4, "", "0", "0/built_item", "0/child#1")
    }

    @Test
    fun `create runtime for state with null div in one of states`() {
        val text = createText("only_child")
        val state = Div.State(DivState(
            id = "state_id",
            states = listOf(
                DivState.State(div = text, stateId = "state_0"),
                DivState.State(stateId = "state_1")
            )
        ))
        val data = data(content = state)

        underTest.fillStore(store, data)

        assertPaths(3, "", "0:state_id", "0:state_id/only_child")
    }

    private fun createText(
        id: String? = null,
        variables: List<DivVariable>? = null,
        functions: List<DivFunction>? = null,
        triggers: List<DivTrigger>? = null,
    ): Div {
        return Div.Text(DivText(
            id = id,
            text = Expression.constant("text"),
            variables = variables,
            functions = functions,
            variableTriggers = triggers
        ))
    }

    private fun createItemBuilder(): DivCollectionItemBuilder {
        val data = JSONArray().apply {
            put(JSONObject())
            put(JSONObject())
        }
        val prototype1 =
            DivCollectionItemBuilder.Prototype(createText(), Expression.constant("built_item"), getSelector(true))
        val prototype2 = DivCollectionItemBuilder.Prototype(createText(), null, getSelector(false))
        return DivCollectionItemBuilder(Expression.constant(data), prototypes = listOf(prototype1, prototype2))
    }

    private fun getSelector(needFirst: Boolean): Expression<Boolean> {
        return mock<Expression<Boolean>> {
            on { evaluate(any()) } doAnswer {
                val resolver = it.arguments[0] as ExpressionResolverImpl
                val isFirst = resolver.path.endsWith("0")
                (needFirst && isFirst) || (!needFirst && !isFirst)
            }
        }
    }

    private fun createContainer(
        id: String? = null,
        items: List<Div>? = null,
        builder: DivCollectionItemBuilder? = null,
    ) = Div.Container(DivContainer(id = id, itemBuilder = builder, items = items))

    private fun createGallery(
        id: String? = null,
        items: List<Div>? = null,
        builder: DivCollectionItemBuilder? = null,
    ) = Div.Gallery(DivGallery(id = id, itemBuilder = builder, items = items))

    private fun createPager(
        id: String? = null,
        items: List<Div>? = null,
        builder: DivCollectionItemBuilder? = null,
    ): Div {
        val layoutMode = DivPagerLayoutMode.PageContentSize(DivPageContentSize())
        return Div.Pager(DivPager(id = id, itemBuilder = builder, items = items, layoutMode = layoutMode))
    }

    private fun createData(states: List<DivData.State>) = DivData(logId = "test", states = states)

    private fun assertPaths(expectedSize: Int, vararg paths: String) {
        assertEquals(expectedSize, runtimePaths.size)
        paths.forEach { assertTrue(runtimePaths.contains(it)) }
    }
}
