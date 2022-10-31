package divan.core

import divan.annotation.Generated

internal typealias Guard = NamedArgumentsGuard

@Generated
class NamedArgumentsGuard private constructor() {
    companion object {
        val instance: NamedArgumentsGuard = NamedArgumentsGuard()
    }
}
