package divkit.dsl.core

import divkit.dsl.scope.DivScope

/**
 * Provides supplement by specified key.
 * Uses factory to create instance
 * if supplement is missing in [DivScope].
 */
fun <T : Supplement> DivScope.supplement(
    key: SupplementKey<T>,
    factory: () -> T
): T = supplements.getOrPut(key, factory) as T

/**
 * Each instances of this class should be unique
 * key for supplement in [DivScope]. So the class
 * should be final with default [equals]/[hashCode].
 */
class SupplementKey<T : Any>(private val key: String) {
    override fun toString(): String {
        return "SupplementKey(\"$key\")"
    }
}

/**
 * Interface of Supplement for [DivScope].
 * Can be used to collect supplementary data
 * while layout build process.
 */
fun interface Supplement {

    /**
     * Extends current [Supplement] by another one.
     */
    fun extend(extension: Supplement): Supplement
}
