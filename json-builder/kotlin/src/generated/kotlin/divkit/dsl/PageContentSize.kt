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
 * Page size equals to its content size.
 * 
 * Can be created using the method [pageContentSize].
 * 
 * Required parameters: `type`.
 */
@Generated
data class PageContentSize internal constructor(
    @JsonIgnore
    val properties: Properties,
) : PagerLayoutMode {
    @JsonAnyGetter
    internal fun getJsonProperties(): Map<String, Any> = properties.mergeWith(
        mapOf("type" to "wrap_content")
    )

    operator fun plus(additive: Properties): PageContentSize = PageContentSize(
        Properties(
            alignment = additive.alignment ?: properties.alignment,
        )
    )

    data class Properties internal constructor(
        /**
         * Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
         * Default value: `center`.
         */
        val alignment: Property<Alignment>?,
    ) {
        internal fun mergeWith(properties: Map<String, Any>): Map<String, Any> {
            val result = mutableMapOf<String, Any>()
            result.putAll(properties)
            result.tryPutProperty("alignment", alignment)
            return result
        }
    }

    /**
     * Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
     * 
     * Possible values: [start], [center], [end].
     */
    @Generated
    sealed interface Alignment
}

/**
 * @param alignment Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
 */
@Generated
fun DivScope.pageContentSize(
    `use named arguments`: Guard = Guard.instance,
    alignment: PageContentSize.Alignment? = null,
): PageContentSize = PageContentSize(
    PageContentSize.Properties(
        alignment = valueOrNull(alignment),
    )
)

/**
 * @param alignment Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
 */
@Generated
fun DivScope.pageContentSizeProps(
    `use named arguments`: Guard = Guard.instance,
    alignment: PageContentSize.Alignment? = null,
) = PageContentSize.Properties(
    alignment = valueOrNull(alignment),
)

/**
 * @param alignment Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
 */
@Generated
fun TemplateScope.pageContentSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    alignment: ReferenceProperty<PageContentSize.Alignment>? = null,
) = PageContentSize.Properties(
    alignment = alignment,
)

/**
 * @param alignment Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
 */
@Generated
fun PageContentSize.override(
    `use named arguments`: Guard = Guard.instance,
    alignment: PageContentSize.Alignment? = null,
): PageContentSize = PageContentSize(
    PageContentSize.Properties(
        alignment = valueOrNull(alignment) ?: properties.alignment,
    )
)

/**
 * @param alignment Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
 */
@Generated
fun PageContentSize.defer(
    `use named arguments`: Guard = Guard.instance,
    alignment: ReferenceProperty<PageContentSize.Alignment>? = null,
): PageContentSize = PageContentSize(
    PageContentSize.Properties(
        alignment = alignment ?: properties.alignment,
    )
)

/**
 * @param alignment Pager pages' alignment along the scroll axis. For edge alignment, offset from parent edge is equal to the corresponding padding.
 */
@Generated
fun PageContentSize.evaluate(
    `use named arguments`: Guard = Guard.instance,
    alignment: ExpressionProperty<PageContentSize.Alignment>? = null,
): PageContentSize = PageContentSize(
    PageContentSize.Properties(
        alignment = alignment ?: properties.alignment,
    )
)

@Generated
fun PageContentSize.asList() = listOf(this)

@Generated
fun PageContentSize.Alignment.asList() = listOf(this)
