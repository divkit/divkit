package com.yandex.divkit.regression.data

data class Scenario(
    var title: String = "",
    var tags: List<String> = emptyList(),
    var steps: List<String> = emptyList(),
    var expected_results: List<String> = emptyList(),
    var file: String = "",
    var priority: Priority = Priority.normal,
    var position: Int = 0,
    var platforms: List<Platforms> = emptyList(),
    var automated: List<Platforms> = emptyList(),
)

@Suppress("EnumEntryName")
enum class Priority {
    blocker, critical, normal, minor
}

@Suppress("EnumEntryName")
enum class Platforms {
    android, ios, web
}
