package divkit.dsl

import divkit.dsl.scope.DivScope

/**
 * @param all Margin.
 */
fun DivScope.edgeInsets(all: Int, unit: SizeUnit? = null): EdgeInsets = edgeInsets(
    left = all,
    right = all,
    top = all,
    bottom = all,
    unit = unit,
)
