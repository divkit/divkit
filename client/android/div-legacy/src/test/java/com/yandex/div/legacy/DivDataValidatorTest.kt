package com.yandex.div.legacy

import com.yandex.alicekit.core.json.ParsingErrorLogger
import com.yandex.alicekit.core.utils.IOUtils
import com.yandex.div.DivData
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class DivDataValidatorTest {

    private val validator = DivDataValidator()

    @Test
    fun invalid_table() {
        com.yandex.alicekit.core.utils.Assert.setEnabled(false)
        val data = loadData("invalid_table.json")
        val validate = validator.validate(data, null)
        assertEquals(0, validate[0])
        com.yandex.alicekit.core.utils.Assert.setEnabled(true)
    }

    @Test
    fun invalid_traffic() {
        val data = loadData("invalid_traffic.json")
        val validate = validator.validate(data, null)
        assertEquals(0, validate[0])
    }

    @Test
    fun only_separators() {
        val data = loadData("only_separators.json")
        val validate = validator.validate(data, null)
        assertEquals(0, validate[0])
    }

    @Test
    fun valid_table() {
        val data = loadData("valid_table.json")
        val validate = validator.validate(data, null)
        assertEquals(1, validate[0])
    }

    @Test
    fun valid_title_universal_buttons_footer() {
        val data = loadData("valid_title_universal_buttons_footer.json")
        val validate = validator.validate(data, null)
        assertEquals(4, validate[0])
    }

    @Test
    fun valid_traffic() {
        val data = loadData("valid_traffic.json")
        val validate = validator.validate(data, null)
        assertEquals(1, validate[0])
    }

    private fun loadData(resourceName: String): DivData {
        return DivData(JSONObject(String(readFromResource(javaClass, resourceName))), ParsingErrorLogger.LOG)
    }

    private fun readFromResource(clz: Class<Any>, resourceName: String): ByteArray {
        return IOUtils.toByteArray(clz.getResourceAsStream(resourceName)) ?: throw AssertionError("Could not get bytes from resource")
    }
}
