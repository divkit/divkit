package divkit.dsl

import com.fasterxml.jackson.databind.json.JsonMapper
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert.assertEquals
import org.skyscreamer.jsonassert.JSONCompareMode

class DivanPatchTest {

    @Test
    fun `simple patch`() {
        val patch = divanPatch {
            patch(
                changes = listOf(
                    patchChange(
                        id = "test",
                        items = listOf(text("patched"))
                    )
                )
            )
        }

        assertEquals(
            readResource("/json/simple_patch.json"),
            patch.toJson(),
            JSONCompareMode.STRICT
        )
    }
}

private fun DivanPatch.toJson() = JsonMapper.builder().build().writeValueAsString(this)
