package com.yandex.div.core.state

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.state.DivStatePath.Companion.parse
import java.lang.Math.min

/**
 * Parsed path to switch states.
 * Path could be parsed from string by using [parse] method.
 * Path structure is:
 * ```
 * top_level_state_id/div_id_of_DivState1/state_id1_of_DivState1/.../div_id_of_DivStateN/state_id1_of_DivStateN
 * ```
 * **Note:** after integer top_level_state_id goes pairs of ```div_id->state_id``` of nested DivState's
 * ignoring other Divs.
 */
data class DivStatePath @VisibleForTesting internal constructor(
    val topLevelStateId: Long,
    private val states: MutableList<Pair<String, String>> = mutableListOf()
) {

    val lastStateId: String?
        get() = if (states.isEmpty()) {
            null
        } else {
            states.last().stateId
        }

    val pathToLastState: String?
        get() = if (states.isEmpty()) {
            null
        } else {
            DivStatePath(topLevelStateId, states.subList(0, states.size - 1)).toString() + "/" + states.last().divId
        }

    override fun toString(): String {
        return if (states.isNotEmpty()) {
            "$topLevelStateId/" + states.flatMap { listOf(it.divId, it.stateId) }.joinToString("/")
        } else {
            "$topLevelStateId"
        }
    }

    fun append(divId: String, stateId: String): DivStatePath {
        val newStates = states.toMutableList().apply { add(divId to stateId) }
        return DivStatePath(topLevelStateId, newStates)
    }

    fun getStates(): List<Pair<String, String>> = states

    /**
     * @return previous [DivStatePath]. For example, DivStatePath equal
     * to 0/first_state/first_div/second_state/second_div will return 0/first_state/first_div.
     * Root states returns itself.
     */
    fun parentState() : DivStatePath {
        if (isRootPath()) {
            return this
        }
        val list = states.toMutableList()
        list.removeLast()
        return DivStatePath(topLevelStateId, list)
    }

    fun isRootPath(): Boolean = states.isEmpty()

    fun isAncestorOf(other: DivStatePath): Boolean {
        if (topLevelStateId != other.topLevelStateId) {
            return false
        }

        if (states.size >= other.states.size) {
            return false
        }

        states.forEachIndexed { index, pair ->
            val otherPair = other.states[index]
            if (pair.divId != otherPair.divId || pair.stateId != otherPair.stateId) {
                return false
            }
        }
        return true
    }

    companion object {
        @JvmStatic
        @Throws(PathFormatException::class)
        fun parse(path: String): DivStatePath {
            val list = mutableListOf<Pair<String, String>>()
            val split = path.split("/")
            val topLevelStateId = try {
                split[0].toLong()
            } catch (e: NumberFormatException) {
                throw PathFormatException("Top level id must be number: $path", e)
            }
            if (split.size % 2 != 1) {
                throw PathFormatException("Must be even number of states in path: $path")
            }
            for (i in (1 until split.size).step(2)) {
                list.add(split[i] to split[i + 1])
            }
            return DivStatePath(topLevelStateId, list)
        }

        fun fromState(stateId: Long) = DivStatePath(stateId, mutableListOf())

        /**
         * Search for shared ancestor for two [DivStatePath]. So path equal to
         * 0/first_state/first_div/second_state/second_div and 0/first_state/first_div/third_state/som_div
         * will have shared [DivStatePath] equal to 0/first_state/first_div.
         */
        internal fun lowestCommonAncestor(somePath: DivStatePath, otherPath: DivStatePath): DivStatePath? {
            if (somePath.topLevelStateId != otherPath.topLevelStateId) {
                return null
            }
            val sharedPairs = mutableListOf<Pair<String, String>>()
            somePath.states.forEachIndexed { index, pair ->
                val otherPair = otherPath.states.getOrNull(index)
                if (otherPair == null || pair != otherPair) {
                    return DivStatePath(somePath.topLevelStateId, sharedPairs)
                }
                sharedPairs.add(pair)
            }
            return DivStatePath(somePath.topLevelStateId, sharedPairs)
        }

        internal fun alphabeticalComparator(): Comparator<DivStatePath> {
            return Comparator { lhs, rhs ->
                if (lhs.topLevelStateId != rhs.topLevelStateId) {
                    return@Comparator (lhs.topLevelStateId - rhs.topLevelStateId).toInt()
                }

                val minSize = min(lhs.states.size, rhs.states.size)
                for (i in 0 until minSize) {
                    val lhsPair = lhs.states[i]
                    val rhsPair = rhs.states[i]
                    val divIdComparison = lhsPair.divId.compareTo(rhsPair.divId)
                    if (divIdComparison != 0) {
                        return@Comparator divIdComparison
                    }
                    val stateIdComparison = lhsPair.stateId.compareTo(rhsPair.stateId)
                    if (stateIdComparison != 0) {
                        return@Comparator divIdComparison
                    }
                }
                return@Comparator lhs.states.size - rhs.states.size
            }
        }
    }
}

class PathFormatException constructor(message: String, cause: Throwable? = null) : Exception(message, cause)

private val Pair<String, String>.divId get() = first
private val Pair<String, String>.stateId get() = second
