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
 * Page width (%).
 * 
 * Can be created using the method [pageSize].
 * 
 * Required parameters: `type, page_width`.
 */
@Generated
data class PageSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : PagerLayoutMode {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "percentage")
    )

    operator fun plus(additive: Properties): PageSize = PageSize(
        Properties(
            pageWidth = additive.pageWidth ?: properties.pageWidth,
        )
    )

    data class Properties internal constructor(
        /**
         * Page width as a percentage of the parent element width.
         */
        val pageWidth: Property<PercentageSize>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("page_width", pageWidth)
            return result
        }
    }
}

/**
 * @param pageWidth Page width as a percentage of the parent element width.
 */
@Generated
fun DivScope.pageSize(
    `use named arguments`: Guard = Guard.instance,
    pageWidth: PercentageSize? = null,
): PageSize = PageSize(
    PageSize.Properties(
        pageWidth = valueOrNull(pageWidth),
    )
)

/**
 * @param pageWidth Page width as a percentage of the parent element width.
 */
@Generated
fun DivScope.pageSizeProps(
    `use named arguments`: Guard = Guard.instance,
    pageWidth: PercentageSize? = null,
) = PageSize.Properties(
    pageWidth = valueOrNull(pageWidth),
)

/**
 * @param pageWidth Page width as a percentage of the parent element width.
 */
@Generated
fun TemplateScope.pageSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    pageWidth: ReferenceProperty<PercentageSize>? = null,
) = PageSize.Properties(
    pageWidth = pageWidth,
)

/**
 * @param pageWidth Page width as a percentage of the parent element width.
 */
@Generated
fun PageSize.override(
    `use named arguments`: Guard = Guard.instance,
    pageWidth: PercentageSize? = null,
): PageSize = PageSize(
    PageSize.Properties(
        pageWidth = valueOrNull(pageWidth) ?: properties.pageWidth,
    )
)

/**
 * @param pageWidth Page width as a percentage of the parent element width.
 */
@Generated
fun PageSize.defer(
    `use named arguments`: Guard = Guard.instance,
    pageWidth: ReferenceProperty<PercentageSize>? = null,
): PageSize = PageSize(
    PageSize.Properties(
        pageWidth = pageWidth ?: properties.pageWidth,
    )
)

/**
 * @param pageWidth Page width as a percentage of the parent element width.
 */
@Generated
fun PageSize.modify(
    `use named arguments`: Guard = Guard.instance,
    pageWidth: Property<PercentageSize>? = null,
): PageSize = PageSize(
    PageSize.Properties(
        pageWidth = pageWidth ?: properties.pageWidth,
    )
)

@Generated
fun PageSize.asList() = listOf(this)
