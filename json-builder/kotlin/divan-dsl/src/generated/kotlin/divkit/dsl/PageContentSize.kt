@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divkit.dsl

import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import divkit.dsl.annotation.*
import divkit.dsl.core.*
import divkit.dsl.scope.*
import divkit.dsl.serialization.*
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
@JsonSerialize(using = PageContentSizeSerializer::class)
data object PageContentSize : PagerLayoutMode

@Generated
fun DivScope.pageContentSize(): PageContentSize = PageContentSize

@Generated
fun PageContentSize.asList() = listOf(this)
