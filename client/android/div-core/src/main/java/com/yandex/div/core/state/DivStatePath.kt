package com.yandex.div.core.state

import androidx.annotation.VisibleForTesting
import com.yandex.div.core.annotations.InternalApi
import kotlin.math.min

/**
 * Parsed path to switch states.
 * Path could be parsed from string by using [parse] method.
 * Path structure is:
 * ```
 * top_level_state_id/id_of_DivState1/state_id1_of_DivState1/.../id_of_DivStateN/state_id1_of_DivStateN
 * ```
 * **Note:** after integer top_level_state_id goes pairs of ```id->state_id``` of nested DivState's
 * ignoring other Divs.
 */
public data class DivStatePath @VisibleForTesting @InternalApi constructor(
    public val topLevelStateId: Long,
    private val states: List<Pair<String, String>> = listOf(),
    internal val path: List<String> = listOf(topLevelStateId.toString()),
    private val containsOnlyStates: Boolean = false,
) {

    public val lastStateId: String?
        get() = if (states.isEmpty()) {
            null
        } else {
            states.last().stateId
        }

    public val pathToLastState: String?
        get() = if (states.isEmpty()) {
            null
        } else {
            DivStatePath(topLevelStateId, states.subList(0, states.size - 1), path)
                .statesString + "/" + states.last().divId
        }

    @InternalApi
    public val lastDivId: String get() = path.last()

    @InternalApi
    public val fullPath: String by lazy { path.joinToString("/") }

    @InternalApi
    public val statesString: String by lazy {
        if (states.isNotEmpty()) {
            "$topLevelStateId/" + states.flatMap { listOf(it.divId, it.stateId) }.joinToString("/")
        } else {
            "$topLevelStateId"
        }
    }

    override fun toString(): String = fullPath

    @Deprecated("Will be removed from public API in the near future", ReplaceWith("append(divId, null, stateId)"))
    public fun append(divId: String, stateId: String): DivStatePath = append(divId, stateId, stateId)

    @InternalApi
    public fun append(divId: String, stateId: String, stateDivId: String): DivStatePath {
        val newStates = ArrayList<Pair<String, String>>(states.size + 1).apply {
            addAll(states)
            add(divId to stateId)
        }
        return DivStatePath(topLevelStateId, newStates, createFullPath(stateDivId))
    }

    @InternalApi
    public fun lastStateEquals(other: DivStatePath): Boolean {
        return if (other.containsOnlyStates) {
            this.pathToLastState == other.pathToLastState
        } else {
            this.parentState().fullPath == other.parentState().fullPath
        }
    }

    public fun appendDiv(divId: String): DivStatePath =
        DivStatePath(topLevelStateId, states, createFullPath(divId))

    private fun createFullPath(divId: String): List<String> {
        return ArrayList<String>(path.size + 1).apply {
            addAll(path)
            add(divId)
        }
    }

    public fun getStates(): List<Pair<String, String>> = states

    /**
     * @return previous [DivStatePath]. For example, DivStatePath equal
     * to 0/first_state/first_div/second_state/second_div will return 0/first_state/first_div.
     * Root states returns itself.
     */
    public fun parentState(): DivStatePath {
        if (isRootPath()) {
            return this
        }
        val list = states.toMutableList()
        val lastState = list.removeAt(list.lastIndex)
        val lastStateIndex = path.indexOfLast { it == lastState.divId }.takeIf { it != -1 }
            ?: path.indexOfLast { it.substringBeforeLast('#') == lastState.divId }.takeIf { it != -1 }
            ?: 0.takeIf { path.first().removePrefix("$topLevelStateId:") == lastState.divId }
            ?: return this
        return DivStatePath(topLevelStateId, list, path.subList(0, lastStateIndex + 1), containsOnlyStates)
    }

    public fun isRootPath(): Boolean = states.isEmpty()

    public fun isAncestorOf(other: DivStatePath): Boolean {
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

    public companion object {
        @JvmStatic
        @Throws(PathFormatException::class)
        public fun parse(path: String): DivStatePath {
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
            return DivStatePath(topLevelStateId, list, split, containsOnlyStates = true)
        }

        public fun fromState(stateId: Long): DivStatePath = DivStatePath(stateId, mutableListOf())

        @InternalApi
        public fun fromState(stateId: Long, divIdSegment: String?): DivStatePath {
            val path = listOf(stateId.toString() + (divIdSegment?.let { ":$it" } ?: ""))
            return DivStatePath(stateId, emptyList(), path)
        }

        /**
         * Search for shared ancestor for two [DivStatePath]. So path equal to
         * 0/first_state/first_div/second_state/second_div and 0/first_state/first_div/third_state/some_div
         * will have shared [DivStatePath] equal to 0/first_state/first_div.
         */
        @InternalApi
        public fun lowestCommonAncestor(somePath: DivStatePath, otherPath: DivStatePath): DivStatePath? {
            if (somePath.topLevelStateId != otherPath.topLevelStateId) {
                return null
            }
            val sharedPairs = findSharedPairs(somePath, otherPath)
            return DivStatePath(
                somePath.topLevelStateId,
                sharedPairs,
                somePath.path.extractStates(sharedPairs, true),
                somePath.containsOnlyStates || otherPath.containsOnlyStates
            )
        }

        @InternalApi
        public fun alphabeticalComparator(): Comparator<DivStatePath> {
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
                        return@Comparator stateIdComparison
                    }
                }
                return@Comparator lhs.states.size - rhs.states.size
            }
        }

        private fun findSharedPairs(somePath: DivStatePath, otherPath: DivStatePath): List<Pair<String, String>> {
            val sharedPairs = mutableListOf<Pair<String, String>>()
            somePath.states.forEachIndexed { index, pair ->
                val otherPair = otherPath.states.getOrNull(index)
                if (otherPair == null || pair != otherPair) return sharedPairs

                sharedPairs.add(pair)
            }
            return sharedPairs
        }

        private fun List<String>.extractStates(states: List<Pair<String, String>>, addChild: Boolean): List<String> {
            var index = 0
            states.forEach { index = findState(it, index) }
            if (addChild) index++
            return subList(0, index)
        }

        private fun List<String>.findState(state: Pair<String, String>, start: Int): Int {
            for (i in start until size - 1) {
                if (get(i) == state.divId && get(i + 1) == state.stateId) return i + 1
            }
            return size
        }
    }
}

public class PathFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)

private val Pair<String, String>.divId get() = first
private val Pair<String, String>.stateId get() = second
