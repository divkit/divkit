package divkit.dsl

import divkit.dsl.core.Guard
import divkit.dsl.core.ReferenceProperty
import divkit.dsl.scope.DivScope
import divkit.dsl.scope.TemplateScope

@Deprecated(
    "Use `sizeUnitValue`",
    replaceWith = ReplaceWith("sizeUnitValue")
)
fun DivScope.wrapContentSizeConstraintSize(
    `use named arguments`: Guard = Guard.instance,
    unit: SizeUnit? = null,
    value: Int? = null,
) = sizeUnitValue(unit = unit, value = value)

@Deprecated(
    "Use `sizeUnitValueRefs`",
    replaceWith = ReplaceWith("sizeUnitValueRefs")
)
fun TemplateScope.wrapContentSizeConstraintSizeRefs(
    `use named arguments`: Guard = Guard.instance,
    unit: ReferenceProperty<SizeUnit>? = null,
    value: ReferenceProperty<Int>? = null,
) = sizeUnitValueRefs(unit = unit, value = value)
