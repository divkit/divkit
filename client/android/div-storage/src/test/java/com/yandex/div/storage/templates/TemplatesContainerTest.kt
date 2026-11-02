package com.yandex.div.storage.templates

import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.storage.DivStorage
import com.yandex.div.storage.DivStorageErrorException
import com.yandex.div2.DivTemplate
import org.intellij.lang.annotations.Language
import org.json.JSONObject
import org.mockito.kotlin.any
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

private const val GROUP_1 = "group_1"
private const val GROUP_2 = "group_2"
private const val SOURCE_TYPE = "source_A"
private const val TEMPLATES_COUNT = 3
private const val TIMEOUT_SECONDS = 5L

class TemplatesContainerTest {

    @Language("json")
    private val templateData1 = JSONObject(
        """
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
        """.trimIndent()
    )

    @Language("json")
    private val templateData2 = JSONObject(
        """
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
        """.trimIndent()
    )

    private val templatesByGroup = mutableMapOf<String, MutableList<Template>>()
    private val templateReferences = mutableSetOf<DivStorage.TemplateReference>()

    private val templateReferencesResult = mock<DivStorage.LoadDataResult<DivStorage.TemplateReference>> {
        on { errors } doReturn emptyList()
        on { restoredData } doAnswer { templateReferences.toList() }
    }

    private val failedReferences = DivStorage.LoadDataResult<DivStorage.TemplateReference>(
        emptyList(),
        listOf(DivStorageErrorException()),
    )

    private val failedTemplates = DivStorage.LoadDataResult<RawTemplateData>(
        emptyList(),
        listOf(DivStorageErrorException()),
    )

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
            DivStorage.LoadDataResult(results)
        }
    }

    private val parsingHistogramProxy = mock<DivParsingHistogramProxy> {
        on { parseTemplatesWithResultsAndDependencies(any(), any(), anyOrNull()) } doAnswer {
            val env = it.getArgument<DivParsingEnvironment>(0)
            val templates = it.getArgument<JSONObject>(1)
            env.parseTemplatesWithResultAndDependencies(templates)
        }
    }

    private val underTest = createUnderTest()

    private fun createUnderTest(
        errorLogger: ParsingErrorLogger = ParsingErrorLogger { throw it },
    ) = TemplatesContainer(
        divStorage,
        errorLogger,
        mock(),
        { parsingHistogramProxy },
        mock(),
    )

    @Test
    fun `templates are loaded from storage`() {
        val savedTemplates = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        addToStorage(GROUP_1, savedTemplates)
        val restoredTemplates = mutableMapOf<String, DivTemplate>()
        createUnderTest().getEnvironment(GROUP_1).templates.takeSnapshot(restoredTemplates)
        assertEquals(TEMPLATES_COUNT, restoredTemplates.size)
    }

    @Test
    fun `addTemplates returns parsed templates`() {
        val parsedTemplates = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        assertEquals(TEMPLATES_COUNT, parsedTemplates.size)
    }

    @Test
    fun `templates hashes are based on content of templates`() {
        val parsedTemplates1 = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
            .map { it.hash }.toSet().toTypedArray()
        val parsedTemplates2 = underTest.addTemplates(GROUP_2, templateData1, SOURCE_TYPE)
            .map { it.hash }.toSet().toTypedArray()
        assertContentEquals(parsedTemplates1, parsedTemplates2)
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
            restoredTemplates1.map { it.key }
        )
        assertCollectionsEqual(
            templateData2.keys().asSequence().toSet(),
            restoredTemplates2.map { it.key }
        )
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

    @Test
    fun `concurrent environment request waits for template references`() {
        addToStorage(GROUP_1, underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE))
        val requested = CountDownLatch(1)
        val released = CountDownLatch(1)
        whenever(divStorage.readTemplateReferences()).doAnswer {
            requested.countDown()
            released.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            templateReferencesResult
        }
        val container = createUnderTest()

        val first = FutureTask { container.getEnvironment(GROUP_1) }
        Thread(first).start()
        requested.await(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        val second = FutureTask { container.getEnvironment(GROUP_1) }
        Thread(second).apply { start() }.awaitBlockedOrFinished()
        released.countDown()

        assertEquals(TEMPLATES_COUNT, first.awaitResult().templatesCount())
        assertEquals(TEMPLATES_COUNT, second.awaitResult().templatesCount())
    }

    @Test
    fun `template references are reloaded after storage error`() {
        addToStorage(GROUP_1, underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE))
        whenever(divStorage.readTemplateReferences())
            .doReturn(failedReferences, templateReferencesResult)
        val container = createUnderTest(errorLogger = {})
        container.getEnvironment(GROUP_1)

        assertEquals(TEMPLATES_COUNT, container.getEnvironment(GROUP_1).templatesCount())
    }

    @Test
    fun `templates are reloaded after storage error`() {
        val savedTemplates = underTest.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)
        addToStorage(GROUP_1, savedTemplates)
        val hashes = savedTemplates.map { it.hash }.toSet()
        val storedTemplates = divStorage.readTemplates(hashes)
        whenever(divStorage.readTemplates(hashes)).doReturn(failedTemplates, storedTemplates)
        val container = createUnderTest(errorLogger = {})
        container.getEnvironment(GROUP_1)

        assertEquals(TEMPLATES_COUNT, container.getEnvironment(GROUP_1).templatesCount())
    }

    @Test
    fun `added templates are available when template references fail to load`() {
        whenever(divStorage.readTemplateReferences()).doReturn(failedReferences)
        val container = createUnderTest(errorLogger = {})

        container.addTemplates(GROUP_1, templateData1, SOURCE_TYPE)

        assertEquals(TEMPLATES_COUNT, container.getEnvironment(GROUP_1).templatesCount())
    }

    private fun DivParsingEnvironment.templatesCount() =
        mutableMapOf<String, DivTemplate>().also { templates.takeSnapshot(it) }.size

    private fun <T> FutureTask<T>.awaitResult() = get(TIMEOUT_SECONDS, TimeUnit.SECONDS)

    private fun Thread.awaitBlockedOrFinished() {
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(TIMEOUT_SECONDS)
        while (isAlive && state != Thread.State.BLOCKED && System.nanoTime() < deadline) {
            Thread.yield()
        }
    }

    private fun assertCollectionsEqual(expected: Collection<String>, actual: List<String>) {
        assertContentEquals(
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
