# Python Bindings Plan for divkit-rs

## Goal

Expose the Rust `divkit-json-builder` library to Python via native bindings using [PyO3](https://pyo3.rs/) + [maturin](https://www.maturin.rs/), providing a drop-in replacement for `pydivkit` with significantly better performance.

## Phase 1: Core Binding Setup

- Add `pyo3` dependency with `extension-module` feature to `Cargo.toml`
- Add `maturin` as the build backend (`pyproject.toml`)
- Create a `python/` directory for the Python package structure
- Configure `Cargo.toml` for `cdylib` crate type (alongside `rlib`)

```toml
[lib]
crate-type = ["cdylib", "rlib"]

[dependencies]
pyo3 = { version = "0.22", features = ["extension-module"] }
```

## Phase 2: Wrap Core Types

Expose the following types as Python classes via `#[pyclass]`:

| Rust type | Python class | Notes |
|-----------|-------------|-------|
| `Expr` | `Expr` | `__str__`, `__repr__`, validation in `__init__` |
| `FieldDescriptor` | `Field()` | Factory function returning descriptor |
| `FieldBuilder` | — | Internal, exposed through `Field()` function |
| `Constraints` | — | Internal, passed through `Field()` kwargs |
| `DivValue` | — | Automatic conversion from Python objects |
| `DivData` | `DivData` | `dict()`, `build()`, `schema()` |
| `DivDataState` | `DivDataState` | |

### Python-to-Rust Value Conversion

Implement `FromPyObject` for `DivValue`:

```
Python int    -> DivValue::Int
Python float  -> DivValue::Float
Python str    -> DivValue::String
Python bool   -> DivValue::Bool
Python list   -> DivValue::Array
Python dict   -> DivValue::Map
Entity class  -> DivValue::Entity
Expr instance -> DivValue::Expr
None          -> DivValue::Null
```

## Phase 3: Entity Type Generation

Two approaches (choose one):

### Option A: Proc-Macro Code Generation

- Write a proc-macro `#[pyentity]` that generates both the Rust struct and the PyO3 wrapper
- Each entity gets `__init__` with keyword arguments, `dict()`, `build()`, `schema()` methods
- Generates `From<PyEntity> for DivValue` automatically

### Option B: Dynamic Class Creation at Import

- Create a single generic `PyEntity` wrapper in Rust
- At module init (`#[pymodule]`), dynamically create Python classes for each entity
- Use Python metaclass or `type()` to create classes with proper `__init__` signatures

**Recommendation**: Option A — more Pythonic, better IDE support, static type hints.

## Phase 4: Template and Ref Support

- Wrap `Template` trait as `BaseDiv` Python base class
- Implement `Ref()` function that creates template references
- Support Python class inheritance for custom templates (like the `CategoriesItem` pattern in pydivkit)
- Implement `related_templates()` traversal
- Implement `make_div()` and `make_card()` as module-level functions

## Phase 5: Packaging and Distribution

- `pyproject.toml` with maturin build backend:
  ```toml
  [build-system]
  requires = ["maturin>=1.0,<2.0"]
  build-backend = "maturin"

  [project]
  name = "divkit-rs"
  requires-python = ">=3.8"
  ```
- Build wheels for Linux (manylinux), macOS (x86_64 + arm64), Windows
- Publish to PyPI
- CI/CD with GitHub Actions using `maturin-action`

## Phase 6: Compatibility Layer

- Provide a `pydivkit` compatibility shim so existing code works unchanged:
  ```python
  # divkit_rs/compat.py
  from divkit_rs import *  # re-export all under pydivkit-compatible names
  ```
- Ensure `DivText`, `DivContainer`, etc. match the same constructor signatures
- Match `dict()` output exactly (key ordering, null handling)

## File Structure

```
divkit-rs/
├── Cargo.toml
├── pyproject.toml
├── src/
│   ├── lib.rs          # existing Rust library
│   ├── python.rs       # PyO3 module definition
│   ├── py_entity.rs    # #[pyclass] entity wrappers
│   ├── py_value.rs     # DivValue <-> Python conversion
│   └── ...             # existing modules
├── python/
│   └── divkit_rs/
│       ├── __init__.py
│       ├── py.typed     # PEP 561 marker
│       └── _divkit_rs.pyi  # type stubs
└── tests/
    ├── test_python.py   # Python-side tests
    └── ...
```

## Performance Expectations

Based on typical PyO3 benchmarks, expect:
- **10-50x** faster `dict()`/`build()` serialization vs pure Python
- **5-20x** faster `schema()` generation
- Near-zero overhead for simple entity construction
- Main gains on large, deeply nested layouts

## Open Questions

1. Should we support pickling/deepcopy for entities?
2. Should `schema()` return a Python dict or a JSON string?
3. How to handle Python subclassing of Rust entities for custom templates?
4. Should we provide async variants for batch operations?
