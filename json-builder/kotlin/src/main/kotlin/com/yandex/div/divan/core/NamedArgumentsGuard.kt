package com.yandex.div.divan.core

internal typealias Guard = NamedArgumentsGuard

class NamedArgumentsGuard private constructor() {
    companion object {
        val instance: NamedArgumentsGuard = NamedArgumentsGuard()
    }
}
