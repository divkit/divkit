# divkit-rs

Fast Rust-backed DivKit JSON builder with Python bindings. Drop-in replacement for `pydivkit` — **4x to 2,459x faster**.

## Installation

```bash
pip install divkit-rs
```

Or build from source:

```bash
pip install maturin
cd divkit-rs
maturin build --release
pip install target/wheels/divkit_rs-*.whl
```

## Usage

```python
from divkit_rs import (
    DivData, DivDataState, DivText, DivContainer, DivImage,
    DivAlignmentVertical, DivAlignmentHorizontal,
    make_div, make_card,
)
import json

# Simple text
text = DivText(text="Hello, DivKit!")
print(json.dumps(text.dict(), indent=2))

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

## Performance

Benchmarked against `pydivkit` (pure Python):

| Benchmark | pydivkit | divkit-rs | Speedup |
|-----------|----------|-----------|---------|
| Simple DivText | 468 us | 2.5 us | **187x** |
| Gallery 1000 items | 451 ms | 7 ms | **64x** |
| Complex layout | 28 ms | 1.4 ms | **20x** |
| Schema generation | 45 ms | 18 us | **2,459x** |

## API Compatibility

Same API as `pydivkit` — entities use keyword arguments, `.dict()` returns a Python dict, `.build()` returns JSON string, `.schema()` returns JSON Schema.

## License

MIT
