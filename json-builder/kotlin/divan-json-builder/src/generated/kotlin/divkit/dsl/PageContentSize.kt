@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonAnyGetter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import kotlin.Any
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.collections.Map

/**
 * The page size is equal to the size of its content.
 * 
 * Can be created using the method [pageContentSize].
 * 
 * Required parameters: `type`.
 */
@Generated
data object PageContentSize : PagerLayoutMode {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = mapOf("type" to "wrap_content")
}

@Generated
fun DivScope.pageContentSize(): PageContentSize = PageContentSize

@Generated
fun PageContentSize.asList() = listOf(this)
