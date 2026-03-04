# divkit-rs

Fast Rust-backed [DivKit](https://divkit.tech/) JSON builder with Python bindings.

Drop-in replacement for [pydivkit](https://pypi.org/project/pydivkit/) with significant performance improvements.

## Installation

```bash
pip install divkit-rs
```

Or build from source:

```bash
pip install maturin
cd json-builder/rust
maturin build --release
pip install target/wheels/divkit_rs-*.whl
```

## Quick Start

```python
from divkit_rs import DivText, DivContainer, make_div, make_card
import json

# Simple text element
text = DivText(text="Hello, DivKit!")
print(json.dumps(text.dict(), indent=2))

# Container with children
container = DivContainer(
    items=[
        DivText(text="Title", font_size=24, font_weight="bold"),
        DivText(text="Subtitle", font_size=16),
    ],
)
print(json.dumps(make_div(container), indent=2))

# Full card
card = make_card(
    DivContainer(
        orientation="horizontal",
        items=[
            DivImage(image_url="https://example.com/logo.png", width=48, height=48),
            DivText(text="Welcome!", font_size=18, font_weight="bold"),
        ],
    ),
    card_id="welcome_card",
)
print(json.dumps(card, indent=2))

# JSON Schema generation
schema = DivText.schema()
```

## Features

- 100+ DivKit entity classes (DivText, DivContainer, DivImage, DivGallery, etc.)
- `dict()` / `build()` — sparse JSON serialization
- `schema(exclude_fields=)` — JSON Schema v7 generation
- `make_div()`, `make_card()` — card assembly helpers
- `Expr` — DivKit expression validation (`@{...}`)
- 30+ enum types
- Template system: `Field()`, `Ref()`, `related_templates()`, `template()`
- Full subclassing support with class-level defaults

## pydivkit Compatibility

divkit-rs provides a compatibility layer that supports the full pydivkit API:

```python
# Drop-in replacement imports
from divkit_rs import DivText, DivContainer, Field, Ref, Expr
from divkit_rs import make_div, make_card
from divkit_rs import BaseDiv, BaseEntity
```

Template classes work the same way:

```python
from divkit_rs import DivContainer, DivText, Field, Ref

class MyCard(DivContainer):
    title: str = Field()
    items = [DivText(text=Ref("title"))]

card = MyCard(title="Hello")
templates = {t.template_name: t.template() for t in card.related_templates()}
```

## Performance

Request-level benchmarks (real service, sequential requests):

| Endpoint | pydivkit p50 | divkit-rs p50 | Speedup |
|----------|-------------|---------------|---------|
| Simple block (id=1) | 28.86 ms | 10.94 ms | **2.64x** |
| Complex block (id=2) | 21.77 ms | 17.40 ms | **1.25x** |

## License

Apache-2.0
