package com.yandex.divkit.regression.data

data class Scenario(
    var title: String = "",
    var tags: List<String> = emptyList(),
    var steps: List<String> = emptyList(),
    var expect: List<String> = emptyList(),
    var paths: List<String> = emptyList(),
    var priority: Priority = Priority.normal,
    var position: Int = 0
)

@Suppress("EnumEntryName")
enum class Priority {
    blocker, critical, normal, minor
}
