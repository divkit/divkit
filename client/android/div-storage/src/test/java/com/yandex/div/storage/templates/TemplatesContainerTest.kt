package com.yandex.div.storage.templates

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.DivStorage
import com.yandex.div.storage.histogram.HistogramNameProvider
import com.yandex.div.storage.histogram.HistogramRecorder
import com.yandex.div2.DivTemplate
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import java.lang.AssertionError

private const val GROUP_1 = "group_1"
private const val GROUP_2 = "group_2"
private const val SOURCE_TYPE = "source_A"
private const val TEMPLATES_COUNT = 3

/**
 * Tests for [TemplatesContainer].
 */
@RunWith(RobolectricTestRunner::class)
class TemplatesContainerTest {
    @Language("json")
    private val templateData1 = JSONObject("""
        {
            "boxed_text": {
                "type": "red_container",
                "items": [
                    {
                        "type": "text",
                        "text": "some text"
                    }
                ]
            },
            "boxed_text_clone": {
                "type": "red_container",
                "items": [
                    {
                        "type": "text",
                        "text": "some text"
                    }
                ]
            },
            "red_container": {
                "type": "container",
                "background": [
                    {
                        "color": "#FF0000",
                        "type": "solid"
                    }
                ]
            }
        }
    """.trimIndent())

    @Language("json")
    private val templateData2 = JSONObject("""
        {
            "boxed_text": {
                "type": "green_container",
                "items": [
                    {
                        "type": "text",
                        "text": "some text"
                    }
                ]
            },
            "boxed_text_clone": {
                "type": "green_container",
                "items": [
                    {
                        "type": "text",
                        "text": "some text"
                    }
                ]
            },
            "green_container": {
                "type": "container",
                "background": [
                    {
                        "color": "#00FF00",
                        "type": "solid"
                    }
                ]
           }
        }
    """.trimIndent())

    private val templatesByGroup = mutableMapOf<String, MutableList<Template>>()
    private val templateReferences = mutableSetOf<DivStorage.TemplateReference>()

    private val templateReferencesResult = mock<DivStorage.LoadDataResult<DivStorage.TemplateReference>> {
        on { errors } doReturn emptyList()
        on { restoredData } doAnswer { templateReferences.toList() }
    }

    private val divStorage = mock<DivStorage> {
        on { readTemplateReferences() } doReturn templateReferencesResult
        on { readTemplates(any()) } doAnswer { invocation ->
            val hashes = invocation.getArgument<Set<String>>(0)
            val results = templatesByGroup.values
                    .reduce { acc, input ->
                        acc.addAll(input)
                        acc
                    }
                    .asSequence()
                    .filter { hashes.contains(it.hash) }
                    .map { RawTemplateData(it.hash, it.template.toString().toByteArray()) }
                    .toList()
            DivStorage.LoadDataResult<RawTemplateData>(results)
        }
    }
    private val errorLogger = ParsingErrorLogger {
        throw AssertionError("Caught unexpected error!", it)
    }
    private val histogramRecorder = mock<HistogramRecorder>()
    private val parsingHistogramProxy = mock<DivParsingHistogramProxy> {
        on { parseTemplatesWithResultsAndDependencies(any(), any(), anyOrNull()) } doAnswer {
            val env = it.getArgument<DivParsingEnvironment>(0)
            val templates = it.getArgument<JSONObject>(1)
            env.parseTemplatesWithResultAndDependencies(templates)
        }
    }

    private val histogramNameProvider = mock<HistogramNameProvider>()

    private val underTest = createUnderTest()

    private fun createUnderTest() = TemplatesContainer(
            divStorage,
            errorLogger,
            histogramRecorder,
            { parsingHistogramProxy },
            histogramNameProvider,
    )

    @Test
    fun `templates are loaded from storage`() {
        val savedTemplates = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        addToStorage(GROUP_1, savedTemplates)
        val restoredTemplates = mutableMapOf<String, DivTemplate>()
        createUnderTest().getEnvironment(GROUP_1).templates.takeSnapshot(restoredTemplates)
        Assert.assertEquals(TEMPLATES_COUNT, restoredTemplates.size)
    }

    @Test
    fun `addTemplates returns parsed templates`() {
        val parsedTemplates = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        Assert.assertEquals(TEMPLATES_COUNT, parsedTemplates.size)
    }

    @Test
    fun `templates hashes are based on content of templates`() {
        val parsedTemplates1 = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
                .map { it.hash }.toSet().toTypedArray()
        val parsedTemplates2 = underTest.addTemplates(GROUP_2, templateData1, SOURCE_TYPE)
                .map { it.hash }.toSet().toTypedArray()
        Assert.assertArrayEquals(parsedTemplates1, parsedTemplates2)
    }

    @Test
    fun `each group may access limited subset of templates`() {
        val savedTemplates1 = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        val savedTemplates2 = underTest.addTemplates(GROUP_2, templateData2, SOURCE_TYPE)

        addToStorage(GROUP_1, savedTemplates1)
        addToStorage(GROUP_2, savedTemplates2)

        val restoredTemplates1 = mutableMapOf<String, DivTemplate>()
        val restoredTemplates2 = mutableMapOf<String, DivTemplate>()

        createUnderTest().apply {
            getEnvironment(GROUP_1).templates.takeSnapshot(restoredTemplates1)
            getEnvironment(GROUP_2).templates.takeSnapshot(restoredTemplates2)
        }

        assertCollectionsEqual(
                templateData1.keys().asSequence().toSet(),
                restoredTemplates1.map { it.key })
        assertCollectionsEqual(
                templateData2.keys().asSequence().toSet(),
                restoredTemplates2.map { it.key })
    }

    @Test
    fun `unique template will be reused once loaded`() {
        val savedTemplates1 = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        val savedTemplates2 = underTest.addTemplates(GROUP_2, templateData1, SOURCE_TYPE)

        addToStorage(GROUP_1, savedTemplates1)
        addToStorage(GROUP_2, savedTemplates2)

        createUnderTest().apply {
            getEnvironment(GROUP_1)
            getEnvironment(GROUP_2)
        }

        verify(divStorage, times(1)).readTemplates(any())
    }

    private fun assertCollectionsEqual(expected: Collection<String>, actual: List<String>) {
        Assert.assertArrayEquals(
                expected.sorted().toTypedArray(),
                actual.sorted().toTypedArray()
        )
    }

    private fun addToStorage(groupId: String, templates: List<Template>) {
        val references = templates.map { DivStorage.TemplateReference(groupId, it.id, it.hash) }
        templateReferences.addAll(references)
        templatesByGroup
                .getOrPut(groupId) { mutableListOf() }
                .addAll(templates)
    }
}
