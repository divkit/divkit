# Кotlin

Based on [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)

This document supplements and also redefines certain provisions of these standards. In case of conflict follow this document.

## General points

1. The principle of consistency should be used: new (or fixed) code should follow the style of the code written around it. Since different variations of text layout are allowed, it is necessary to adhere to the same approach and style within the same file.
2. You should not use Java-familiar approaches when alternative Kotlin-style approaches are available. In other words, don't try to write Kotlin like Java.


## Naming

Names of classes, fields, variables and parameters should be chosen from English words (transliteration is not allowed) or established abbreviations (abbreviations should be used as words). Unknown abbreviations should not be used.

Good             | Bad
---------------- | ----------------
Separator        | Razdelitel
XmlHttpRequest   | XMLHTTPRequest
getId            | getID
class Html       | class HTML
url: String      | URL: String
id: Long         | ID: Long
JustOneNewAcro   | JONA


## Imports

Each `import` statement must be written on one line without breaks. The list of expressions should be sorted alphabetically and should not contain unused imports.
Wildcards are forbidden.


## Formatting

### Line length

Maximum line length - 120 characters.

### Vertical indentation

Methods must be separated from each other by one empty line.

### Curly braces

Don't use curly braces in single-line `if` statement. In multiline `if` statements curly braces must be used.

**Good:**
```kotlin
    val myVal = if (condition) 1 else 0
```

```kotlin
    if (condition) doSomething()
```

```kotlin
    if (condition1) {
        doSomething1()
        doSomething2()
        doSomething3()
    }

    if (condition2) {
        value = Value.builder()
            .someThing1(someThing1)
            .someThing2(someThing2)
            .someThing3(someThing3)
            .build()
    }
```

In methods with an empty implementation, curly braces should be replaced with the `Unit` expression.

**Good:**
```kotlin
    override fun empty() = Unit
```

**Bad:**
```kotlin
    override fun empty() {
    }
```

### Expression bodies

Always use expression body of functions in case of trivial expressions.

**Good:**
```kotlin
    fun sum(a: Int, b: Int) = a + b
```

**Bad:**
```kotlin
    fun sum(a: Int, b: Int): Int { 
        return a + b
    }
```

For more complex expressions, you can use the expression body in case of single-line statement, including the situation with line wrapping to the next 1 (according to the rules for wrapping long statements)

**Good:**
```kotlin
    // 1 line
    fun doPerform() = actionFactory.createPerformer.perform()
```

```kotlin
    // 1 line with a break
    fun dispatchNextState(mode: CaptureMode = null): Unit =
        fallbackStateDispatcher.dispatchByMode(captureMode = mode ?: CaptureMode.DEFAULT, oldState = currentState)
```

**Bad:**
```kotlin
    fun doEither(condition: Boolean): Result = 
        if (condition) {
            myPerformer.doActual()
        } else {
            Result.FAIL
        }
```

```kotlin
    fun doEither(condition: Boolean?): Result = 
        when (condition) {
            true -> Result.SUCCESS
            null -> Result.UNKNOWN
            false -> Result.FAIL
        }
```

### Return types

Declare type explicitly if:
* function is a part of public api
* it's not clear what will be returned
* should pay attention to the type
* platform type is used

**Good:**
```kotlin
    fun method(a: Type1, b: Type2): Type3 = a.doA(b.getB())
```

```kotlin
    fun sum(a: Int, b: Int): Float = а * b.toFloat()
```

```kotlin
    fun toast(text: String): Toast = Toast.makeText(activity, text, Toast.LENGTH_LONG)
```

**Bad:**
```kotlin
    fun method(a: Type1, b: Type2) = a.doA(b.getB())
```

```kotlin
    fun sum(a: Int, b: Int) = а * b.toFloat()
```

```kotlin
    fun toast(text: String) = Toast.makeText(activity, text, Toast.LENGTH_LONG)
```

Don't specify return type if it is obvious.

**Good:**
```kotlin
    fun sum(a: Int, b: Int) = a + b
```

**Bad:**
```kotlin
    fun sum(a: Int, b: Int): Int = а + b
```

## Comments

Comments must be written only in English.


## Idiomatic use of language features

### public `lateinit`

Don't use `lateinit` modifier for public properties.

**Bad:**
```kotlin
    lateinit var someString: String
```

### Nullability

1. Unsafe operator `!!` is not allowed.
2. Elvis operator is preferred.

**Good:**
```kotlin
    optional?.doSomething()
```

**Bad:**
```kotlin
    if (optional != null) {
        optional.doSomething()
    }
```

```kotlin
    optional!!.doSomething()
```

### Named arguments

Use the named argument syntax for complex functions with many arguments. If the arguments contain `Boolean` and/or primitives, the use of a named call is mandatory. Use of positional parameters only if their meaning is absolutely clear from the context.

**Good:**
```kotlin
drawSquare(x = 10, y = 10, width = 100, height = 100, fill = true)
```

### Extension functions

Use extension functions instead of utility functions.

**Good:**
```kotlin
fun String.removeFirstLastChar() = substring(1, length - 1)
```

**Bad:**
```kotlin
fun removeFirstLastChar(string: String) = string.substring(1, string.length - 1)
```

### Enum vs `@IntDef`/`@StringDef`

In all cases, the use of `enum class` or `sealed class` is recommended instead of `@IntDef`/`@StringDef` annotations.

### Scope functions `apply`/`with`/`run`/`also`/`let`

It is recommended to:
1. Use `apply` to set up or initialize an object and avoid using it for logic.
2. Don't overuse this API and try to keep it readable in natural English style.
3. Avoid excessive nesting.
4. Do not use nested `with`/`run`/`apply`. In case of nested `also`/`let`, naming of the argument is required (you can't use `it`).
