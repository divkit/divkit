@file:Suppress(
    "unused",
    "UNUSED_PARAMETER",
)

package divan

import divan.annotation.Generated
import kotlin.Suppress

@Generated
sealed interface Entity

@Generated
fun Entity.asList() = listOf(this)
