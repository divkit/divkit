package divkit.dsl

import com.fasterxml.jackson.databind.json.JsonMapper
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.nio.file.Files
import java.nio.file.Path

fun assertEquals(expectedJson: String, card: Divan) {
    val cardJson = JsonMapper.builder().build().writeValueAsString(card)
    JSONAssert.assertEquals(expectedJson, cardJson, JSONCompareMode.STRICT)
}

fun readResource(path: String): String {
    return Files.readString(Path.of(object {}.javaClass.getResource(path).toURI()))
}
