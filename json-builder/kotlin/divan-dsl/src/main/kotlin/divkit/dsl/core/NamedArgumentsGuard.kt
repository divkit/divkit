package divkit.dsl.core

internal typealias Guard = NamedArgumentsGuard

class NamedArgumentsGuard private constructor() {
    companion object {
        val instance: NamedArgumentsGuard = NamedArgumentsGuard()
    }
}
