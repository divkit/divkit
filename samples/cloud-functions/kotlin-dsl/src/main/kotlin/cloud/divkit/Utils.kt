package cloud.divkit

import com.fasterxml.jackson.databind.json.JsonMapper
import divkit.dsl.Divan

fun Divan.toJson(): String {
    return JsonMapper.builder().build().writeValueAsString(this)
}
