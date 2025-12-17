package divkit.dsl.expression.generator.util

val KEYWORDS: Set<String> = setOf(
    "as", "break", "class", "continue", "do", "else", "false", "for", "fun", "if", "in", "interface", "is", "null",
    "object", "package", "return", "super", "this", "throw", "true", "try", "typealias", "typeof", "val", "var",
    "when", "while"
)

val BUILTIN_METHODS: List<String> = listOf(
    "equals", "hashCode", "toString"
)

val RESERVED_METHOD_NAMES = KEYWORDS + BUILTIN_METHODS
