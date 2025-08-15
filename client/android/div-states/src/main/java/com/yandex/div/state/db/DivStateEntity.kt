package com.yandex.div.state.db

internal data class DivStateEntity(
    val id: Int,
    val cardId: String,
    val path: String,
    val stateId: String,
    val modificationTime: Long
)

internal data class PathToState(
    val path: String,
    val stateId: String
)
