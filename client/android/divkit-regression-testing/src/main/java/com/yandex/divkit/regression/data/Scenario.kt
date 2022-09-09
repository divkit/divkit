package com.yandex.divkit.regression.data

data class Scenario(
    var title: String = "",
    var tags: List<String> = emptyList(),
    var steps: List<String> = emptyList(),
    var expected_results: List<String> = emptyList(),
    var file: String = "",
    var priority: Priority = Priority.normal,
    var position: Int = 0
)

@Suppress("EnumEntryName")
enum class Priority {
    blocker, critical, normal, minor
}
