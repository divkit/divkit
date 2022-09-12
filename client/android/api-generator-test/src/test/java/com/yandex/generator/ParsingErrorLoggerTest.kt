package com.yandex.generator

import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div.json.ParsingException
import com.yandex.div.json.summary
import com.yandex.testing.EntityWithOptionalProperty
import com.yandex.testing.EntityWithRequiredProperty
import com.yandex.testing.EntityWithOptionalComplexProperty
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyZeroInteractions
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ParsingErrorLoggerTest {

    private val logger = mock<ParsingErrorLogger>()
    private val environment = EntityParsingEnvironment(logger = logger)
    private val exceptionCaptor = argumentCaptor<ParsingException>()

    private val exception: ParsingException
        get() = exceptionCaptor.firstValue

    @Test
    fun `error when optional property has invalid value`() {
        val json = JSONObject("""{ "property": 123 }""")

        EntityWithOptionalProperty(environment, json)

        verify(logger).logError(exceptionCaptor.capture())
        assertEquals(
            "Value for key 'property' has wrong type java.lang.Integer",
            exception.message
        )
        assertEquals(json.summary(), exception.jsonSummary)
    }

    @Test
    fun `no error when optional property has null value`() {
        val json = JSONObject("""{ "property": null }""")

        EntityWithOptionalProperty(environment, json)

        verifyZeroInteractions(logger)
    }

    @Test
    fun `error when optional complex property has has null value`() {
        val json = JSONObject("""{ "property": { "value": null } }""")

        try {
            EntityWithOptionalComplexProperty(environment, json)
        } catch (e: ParsingException) {
            // expected
        }

        verify(logger).logError(exceptionCaptor.capture())
        assertEquals(
            "Value for key 'value' is missing",
            exception.message
        )
        assertEquals(json.getJSONObject("property").summary(), exception.jsonSummary)
    }

    @Test
    fun `no error when required property has invalid value`() {
        val json = JSONObject("""{ "property": 123 }""")

        try {
            EntityWithRequiredProperty(environment, json)
        } catch (e: ParsingException) {
            // expected
        }

        verifyZeroInteractions(logger)
    }

    @Test
    fun `no error when required property has null value`() {
        val json = JSONObject("""{ "property": null }""")

        try {
            EntityWithRequiredProperty(environment, json)
        } catch (e: ParsingException) {
            // expected
        }

        verifyZeroInteractions(logger)
    }

    @Ignore
    @Test
    fun `error when template without type`() {
        val json = JSONObject("""{ "template1": { } }""")

        environment.parseTemplates(json)

        verify(logger).logTemplateError(exceptionCaptor.capture(), eq("template1"))
        assertEquals(
            "Value for key 'type' is missing",
            exception.message
        )
        assertEquals(json.getJSONObject("template1").summary(), exception.jsonSummary)
    }

    @Test
    fun `error when template has invalid optional property value`() {
        val json = JSONObject(
            """{
                "template1": {
                    "type": "entity_with_optional_property",
                    "property": 123
                }
            }"""
        )

        environment.parseTemplates(json)

        verify(logger).logTemplateError(exceptionCaptor.capture(), eq("template1"))
        assertEquals(
            "Value for key 'property' has wrong type java.lang.Integer",
            exception.message
        )
        assertEquals(json.getJSONObject("template1").summary(), exception.jsonSummary)
    }

    @Test
    fun `error when template has invalid link`() {
        val json = JSONObject(
            """{
                "template1": {
                    "type": "entity_with_optional_property",
                    "${'$'}property": 123
                }
            }"""
        )

        environment.parseTemplates(json)

        verify(logger).logTemplateError(exceptionCaptor.capture(), eq("template1"))
        assertEquals(
            "Value for key '\$property' has wrong type java.lang.Integer",
            exception.message
        )
        assertEquals(json.getJSONObject("template1").summary(), exception.jsonSummary)
    }

    @Test
    fun `multiple errors when errors in multiple templates`() {
        val json = JSONObject(
            """{
                "template1": {
                    "type": "entity_with_optional_property",
                    "property": 123
                },
                "template2": {
                    "type": "entity_with_optional_property",
                    "property": 123
                }
            }"""
        )

        environment.parseTemplates(json)

        val templateIdCaptor = argumentCaptor<String>()
        verify(logger, times(2)).logTemplateError(any(), templateIdCaptor.capture())

        val ids = templateIdCaptor.allValues
        assertEquals("template1", ids[0])
        assertEquals("template2", ids[1])
    }
}
