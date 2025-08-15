# Kotlin Json Builder (DSL)

## API structure

### Node types
**Classes:** `Text`, `Image`, `Text.Range`, `Dimension`, `FixedSize` etc.
* Responsible for storing the layout description
* Responsible for serialization to json format
* Fundamental types for all library signatures
* Can be created using factory methods (see below)

### Div types
**Classes:** `Text`, `Image`, `Container`, `Pager`, `Indicator` etc.
* Subset of node types
* Conforms to `Div` node type
* Have extended syntax (compared with other node types)
* Can be the root elements of the layout
* Similar to `View` in Android and `UIView` in iOS.

### Node properties
**Classes:** `Text.Properties`, `Image.Properties` etc.
* Auxiliary entities for operations with node properties
* Contains all non-constant node properties
* Can be created using factory methods

### Scopes
**Classes:** `DivScope`, `TemplateScope`
* Provides access to factory methods
* Provides access to enum values
* Accumulates templates used while layouting
* Accumulates supplements used while layouting (see below)

### Node factory methods
**Functions:** `text(...)`, `image(...)` etc.
* Available for all node types except abstract types
* Accessible only within `DivScope` or `TemplateScope`
* Signature contains all non-constant node properties
* Each argument should be literal property value (`String`, `Int`, `Text.Range`, `FixedSize` etc.)
* Requires call with named arguments (except forced properties)
* Some methods supports calls with partially unnamed parameters (ex. `text("Hello, world!")`

### Alternative factory methods
**Functions:** `column`, `row` etc.
Some node types has alternative factories with adjusted semantic.
For example type `Container` has factory `column` means `container` with vertical orientation and type `EdgeInsets` has same named factory `edgeInsets` with single argument `all`.

### Properties factory methods
**Functions:** `textProps(...)`, `imageProps(...)` etc.
* Available for all node property types
* Accessible only within `DivScope` or `TemplateScope`
* Signature contains all non-constant node properties
* Each argument should be literal property value (`String`, `Int`, `Text.Range`, `FixedSize` etc.)
* Requires call with named arguments (no exceptions)

### References factory methods
**Functions:** `textRefs(...)`, `imageRefs(...)` etc.
* Available for all node property types
* Accessible only within `TemplateScope`
* Signature contains all non-constant node properties
* Each argument should be a reference property value (`Reference<String>`, `Reference<Size>` etc.)
* Requires call with named arguments (no exceptions)

### Enum values
**Read only properties:** `horizontal`, `verical`, `start`, `bottom` etc.
* The names matches with values (`start` -> "start" in json)
* Accessible only within `DivScope` or `TemplateScope`

### Template
**Class:** `Template`
* Incapsulates layout invariants
* Body must be invariant (pure randomless function)
* Name must be unique among all templates used in actual div-json
* Can be created using method `template(...)`

### Component
**Class:** `Component`
* Holds template reference and template properties
* Conforms to `Div` node type
* Can be created using method `render(...)`

### Supplement
**Interface:** `Supplement`
* Allows to accumulate supplementary data while layouting
* Can be provided using `supplement(...)` method inside `DivScope` or `TemplateScope`

### Divan
**Class:** `Divan`
* Holds card data, templates used in card and layout supplements
* Responsible for div-json serialization
* Can be created using method `divan { ... }`

## DSL Syntax

### Entry point
Method `divan` provides access to `DivScope` so all syntax available only within `divan { ... }` body.
The result of `divan` method call is object of type `Divan` with fields `card: Div` (layout), `templates: Map<String, Div>` (used templates) and `supplements: Map<SupplementKey<*>, Supplement>`.
```kotlin
val divan = divan {
    // Your layout data here
}
```

### Layout data
Root of layout tree is `data` method with required `logId` and list of layout states. List of states can be created via `listOf(root(...), root(...))` for multiple state layout with explicit `stateId` or via `singleRoot(...)` for single state layout with implicit `stateId = 0`.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = listOf(
            root(stateId = 1, div = ...),
            root(stateId = 2, div = ...)
        )
    )
}
```
or
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = ...
        )
    )
}
```

### Layout tree
Layout is a tree of DSL or extension method calls with arguments.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column(
                width = wrapContentSize(),
                height = wrapContentSize(),
                margins = edgeInsets(left = 10, right = 10, top = 5, bottom = 5),
                items = listOf(
                    text("Hello, world!", fontSize = 18),
                    image("https://my-image-link")
                )
            )
        )
    )
}
```

### Layout decomposition
Decomposition is a great technique for working with complex layout. To carry out part of the layout into a method, it is enough to create an extension for `DivScope`.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column(
                items = listOf(
                    header("Hello, world!"),
                    body(...)
                )
            )
        )
    )
}

fun DivScope.header(title: String) = text(
    text = title,
    fontWeight = bold,
    fontSize = 24,
    lineHeight = 28
)

fun DivScope.body(...) = ...
```

### Property composition
Composition is a great technique for reusing parts of layout. Each node can be composed with relevant properties. It's convenient to set the element location parameters at the element integration point, and it's style in a separate method. To allow this approach DSL provides syntax `div + divProps`.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column(
                items = listOf(
                    header("Hello, world") + textProps(margins = edgeInsets(top = 10)),
                    body(...) + containerProps(margins = edgeInsets(all = 8))
                )
            )
        )
    )
}
```

### Property overriding
Alternative way to change node properties is `override` extension method. Signature of this method contains all non-constant properties of node.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column(
                items = listOf(
                    header("Hello, world").override(margins = edgeInsets(top = 10)),
                    body(...).override(margins = edgeInsets(all = 8))
                )
            )
        )
    )
}
```

### Components
DivKit implements templates approach to allow deduplication of repeated layout parts. Type `Component<T : Div>` allows to inline templates in layout. Component can be created by method `render` from specified `template` and `bindings`.
* Method `reference` allows to create deferred layout properties which should be resolved with `render` call.
* Method `<node>Refs` allows to create property references.
* Method `defer` allows to override node properties by specified property references.
* Method `render` allows to "bake" template with bindings into `Component`.
```kotlin
// Template attributes
private val titleRef = reference<String>("title")
// Template layout
private val headerTemplate = template("header-template") {
    text(
        fontWeight = bold,
        fontSize = 24,
        lineHeight = 28
    ) + textRefs(text = titleRef) // or .defer(text = titleRef)
}
// Component signature for usage inside layout tree
fun DivScope.header(title: String) = render(headerTemplate, titleRef bind title)

// Component usage example
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column(
                items = listOf(
                    header("Hello, world!") + textProps(width = matchParentSize()),
                    body(...)
                )
            )
        )
    )
}
```

### Enum values
DSL allows to inline enum values in layout. You can just start typing value of enum (and even select value from suggest). Check available enum values in documentation. 
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = container(
                orientation = horizontal, // Enum value = "horizontal"
                items = listOf(
                    text("Hello, world!", fontWeight = bold) // Enum value = "bold"
                    body(...)
                )
            )
        )
    )
}
```

### Property values
DSL contains 3 types of properties: literals, references and expressions. Base class of all properties is `Property<T>`.
* Methods `value` and `valueOrNull` allows to create literal.
* Method `reference` allows to create reference (can be resolver later in layout).
* Method `expression` allows to create expression (evaluates in runtime).

### Expressions
Method `expression<T>` allows to create evaluable property. Most types has evaluable properties. If type has no `evaluate` method, there is no evaluable properties. In common case expressions are used together with variables. Variables can be declared in `data` object (property `variables`).
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleState(
            div = column(
                items = listOf(
                    text("Hello, world!").evaluate(alpha = "@{toNumber(alpha_var) / 255.0}"),
                    slider(
                        minValue = 0,
                        maxValue = 255,
                        thumbValueVariable = "alpha_var",
                        ...
                    )
                )
            )
        ),
        variables = listOf(
            integerVariable(name = "alpha_var", value = 0)
        )
    )
}
```

### Containers
Developer wants to understand structure of layout as fast as possible. The most influential to structure of layout type is `container`. But "container" don't suggests how layout looks like (it depends on orientation parameter). To enhance readability DSL provides shortcuts `column`, `row` and `stack`.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column( // Container
                items = listOf(
                    row(...), // Container
                    stack(...) // Container
                )
            )
        )
    )
}
```
Container shortcuts has `vararg` signature for `items` property so the children can be specified without named parameter.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleRoot(
            div = column(
                row(...),
                stack(...),
                // Set other properties below children
                margins = ...,
                background = ...
            )
        )
    )
}
```

### As List
A lot of properties in DivKit is a collections (for example `background: List<Background>`). However property `background` has more than one element in rare cases. Method `listOf()` in starting part of value distracts from matter part of value. To solve this issue DSL provides method `asList()` for each node type.
```kotlin
val divan = divan {
    data(
        logId = "my-layout-id",
        states = singleState(
            div = text(
                text = "Hello, world!",
                background = solidBackground(color("#ffffff")).asList()
            )
        )
    )
}
```
