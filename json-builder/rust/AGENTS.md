# Agent Guide (divkit-rs)

This repo is a Rust library (`divkit-json-builder`) with Python bindings built via PyO3 + maturin.

## Quick Repo Map

- `Cargo.toml`: Rust crate `divkit-json-builder` (library) + `bench` binary.
- `src/`: Rust implementation and PyO3 module.
  - `src/entity.rs`: Core `Entity` / `Template` traits, `DivData`, `DivDataState`, `make_div()`, `make_card()`, `normalize_json_key()`.
  - `src/value.rs`: `DivValue` enum — dynamic value type stored in entity fields; `to_json()` serialization.
  - `src/py_entity.rs`: `PyDivEntity` — PyO3 wrapper for all entities; `dict()`, `_set_fields()` with schema-driven type coercion, `related_templates()`.
  - `src/python.rs`: Python-facing functions (`compat_dump`, `normalize_pydivkit_json`, `compat_make_card`, `make_div`, `make_card`).
  - `src/py_value.rs`: `json_to_py()` — converts `serde_json::Value` back to Python objects.
- `src/generated/`: **Auto-generated** Rust entity files and Python registration code (see [Code Generation](#code-generation)).
  - `src/generated/schema_registry.rs`: `entity_field_descriptors(name)` — looks up field `SchemaFieldType` info by entity short name (e.g. `"DivContainer"`).
- `python/divkit_rs/__init__.py`: Python-facing exports.
- `python/divkit_rs/_pydivkit_compat.py`: pydivkit compatibility layer — `_compat_getattribute`, template registry, `install_pydivkit_compat()`.
- `pyproject.toml`: Python package metadata, maturin config.
- `docs/`: benchmarking + binding notes.

## Build / Lint / Test

### Rust (Cargo)

- Build (debug):
  - `cargo build`
- Build (release):
  - `cargo build --release`
- Format:
  - `cargo fmt`
- Lint (Clippy):
  - `cargo clippy --all-targets --all-features -- -D warnings`
- Run tests:
  - `cargo test --lib` (or `make test`)
  - **Note:** bare `cargo test` fails to link because PyO3 requires Python symbols. Always use `--lib` to skip the binary target.

Single test (Rust):

- By test name substring:
  - `cargo test test_div_text_basic`
- By fully-qualified path (helpful when names repeat):
  - `cargo test lib::tests::test_div_text_basic`
- Show output / don't capture stdout:
  - `cargo test test_div_text_basic -- --nocapture`

Bench binary:

- `cargo run --release --bin bench`

### Python bindings (maturin)

This project uses `uv` for Python dependency management and virtualenv.

- Editable install (fast iteration):
  - `uv run maturin develop`

- Build wheel:
  - `uv run maturin build --release`

Smoke check import:

- `uv run python -c "import divkit_rs; print(divkit_rs.DivText(text='hi').dict())"`

### Python tests (pytest)

Requires the extension to be installed first (`uv run maturin develop`):

```bash
uv run maturin develop
uv run pytest tests/
```

Single test:

- `uv run pytest tests/test_divkit_rs.py::TestBoolSerialization::test_bool_true_serializes_as_true`
- `uv run pytest tests/ -k "enum"` (keyword filter)

Notes:

- Tests live in `tests/test_divkit_rs.py`; pytest config is in `pyproject.toml`.
- Python tests cover PyO3-specific behavior (class types, enum types, serialization from Python) that Rust unit tests cannot exercise.
- **Note:** `python` is not on PATH; always use `uv run` prefix.

## Code Generation

The `src/generated/` directory is **fully auto-generated** — do not edit these files by hand.

### Generator location

The code generator lives at `../../api_generator/api_generator/generators/rust/` (relative to this crate root), specifically:

- `generator.py` (`RustGenerator`): main entry point; generates per-entity `.rs` files, `mod.rs`, and `python_generated.rs`.
- `rust_entities.py`: Rust-specific entity wrappers used during generation.

### What gets generated

| File | Contents |
|------|----------|
| `src/generated/<entity>.rs` | One file per DivKit entity (e.g. `div_text.rs`, `div_container.rs`) with `div_entity!` macro invocations. |
| `src/generated/mod.rs` | Module declarations and `pub use` re-exports for all generated entities. |
| `src/generated/python_generated.rs` | `register_all_entities()`, `register_enums()`, and `register_type_aliases()` — PyO3 registration code called from `src/python.rs`. |

### How to regenerate

From this crate root (`json-builder/rust/`):

```bash
./codegen.sh
```

This runs:

```bash
python3 -m api_generator \
    -c codegen_config.json \
    -s ../../schema \
    -o src/generated
```

- `-c codegen_config.json`: config file in this directory (specifies `lang: "rust"`).
- `-s ../../schema`: DivKit JSON schema directory.
- `-o src/generated`: output directory for generated Rust files.

### When to regenerate

- After schema changes in `../../schema/`.
- After modifying the generator templates in `../../api_generator/api_generator/generators/rust/`.
- **Do not** manually edit files in `src/generated/`. If you need to change registration logic (e.g. how `register_enums` works), update `generator.py` and re-run `./codegen.sh`.

## Architecture

### Two JSON output paths

1. **Compat path** (used by `make_div`/`make_card` in `_pydivkit_compat.py`): `entity.dict()` → `normalize_pydivkit_json()` → `json.dumps`. The normalize step converts underscore keys to hyphens for corners_radius and coerces int→float for specific fields.
2. **Native path** (used by `make_div`/`make_card` in `python.rs`): `entity.dict()` → `json_to_py()` → `json.dumps`. No post-processing normalize step; correctness must come from `dict()` itself.

Both paths must produce identical output matching the original pydivkit (prod).

### Serialization rules (JSON output parity)

These rules ensure preprod (Rust) output matches prod (pydivkit):

- **corners_radius keys**: Use hyphens (`top-left`, `bottom-right`), not underscores. Handled by `normalize_json_key()` in `entity.rs`, applied in both `Entity::dict()` and `PyDivEntity::dict()`.
- **Float preservation**: `DivValue::Float` values always serialize as JSON floats (e.g. `1.0` not `1`). No truncation of whole-number floats to integers.
- **Type-aware coercion** (in `py_entity.rs` `_set_fields()`): Uses the schema registry to coerce values at field-set time:
  - Int 0/1 → Bool for fields with `SchemaFieldType::Boolean` (e.g. `clip_to_bounds`, `has_shadow`).
  - String → Int for fields with `SchemaFieldType::Integer` (e.g. `IntegerVariable.value`).
- **normalize_pydivkit_json coercion** (in `python.rs`): Coerces int→float for keys: `alpha`, `ratio`, `weight`, `letter_spacing`, `rotation`, `value` (under `x`/`y`), `width` (under `stroke`). Coerces int `color` to string.

### Compat layer (`_pydivkit_compat.py`)

`install_pydivkit_compat()` monkey-patches `PyDivEntity` to emulate pydivkit behavior:

- `_compat_getattribute`: Intercepts attribute access to wrap raw dicts/lists returned by Rust back into entity objects:
  - `margins` → `NativeDivEdgeInsets`
  - `action` (singular) → `NativeDivAction`
  - `actions` (plural) → `list[NativeDivAction]`
  - Constructor-assigned `PyDivEntity` values are returned directly (mutable reference pattern).
- `_compat_init_subclass`: Template registration, field declaration processing, `_TEMPLATE_REGISTRY` management.
- `_compat_entity_init`: Applies `__dk_defaults__` and `__dk_fields__` defaults.

When adding new fields that return entities via `getattr`, check if `_compat_getattribute` needs a wrapping case (like `action`/`actions`/`margins`).

## Code Style (Rust)

### Formatting

- Use rustfmt defaults: run `cargo fmt` before committing.
- Prefer one logical operation per line when chaining builder calls gets long.

### Imports

- Group imports in this order: std, external crates, crate/super.
- Keep `use` lines narrow and readable; avoid glob imports except for the generated-ish `div::*` modules and tests/benches where it improves ergonomics.

### Naming

- Types: `CamelCase` (`DivText`, `DivEdgeInsets`).
- Traits: `CamelCase` (`Entity`, `Template`).
- Functions/vars/modules: `snake_case`.
- Constants: `SCREAMING_SNAKE_CASE`.

### Types and APIs

- Prefer concrete types in public APIs where it improves usability; use generics when they clearly reduce duplication.
- Keep Python-facing behavior aligned with the `pydivkit` compatibility goals:
  - `.dict()` returns a JSON-like object (in Rust: `serde_json::Value`).
  - `.build()` is an alias for `.dict()`.
  - `.schema(exclude_fields=...)` behavior matches Python expectations.

### Determinism

- JSON object key order is not semantically meaningful, but stable output is useful for tests.
- If you build map-like structures from iterators (especially `HashMap`), sort keys or construct in a deterministic order when output is asserted in tests.
  - Example pattern exists in `src/entity.rs` (`DivDataState::to_div_value` sorts entries).

### Error handling

- Rust: use `Result<T, E>` + `?`; avoid `unwrap()` in library code.
- Tests/benches may use `unwrap()` when it's genuinely infallible for the test.
- Python bindings:
  - Expose failures as `PyResult<T>`.
  - Map validation errors to specific Python exceptions (e.g. `PyValueError`) rather than generic errors.
  - Avoid panics crossing the FFI boundary.

### Safety / `unsafe`

- Avoid introducing new `unsafe` unless necessary.
- If you must use `unsafe`, keep it small, justify it in code, and add a targeted test.
- Be careful with `Send`/`Sync` claims for Python interop types.

### Clippy guidance

- Keep clippy clean under `-- -D warnings`.
- Prefer idiomatic iteration and allocation (pre-size vectors, avoid needless clones) in hot paths (serialization, schema generation).

## Code Style (Python)

### Layout

- Keep the public surface in `python/divkit_rs/__init__.py` consistent:
  - Maintain `__all__` and re-export list ordering.
  - Prefer explicit imports over wildcard imports.

### Naming

- Python-facing classes follow the Rust-exported names (`DivText`, etc.).
- Methods follow Python conventions (`dict`, `build`, `schema`).

### Compatibility

- Treat Python signatures as part of the API contract; changes should be deliberate.
- Prefer keyword-only style for entity constructors (matches current `#[pyo3(signature = (**kwargs))]`).

## Adding/Updating Entities

- Entity structs and Python registration code in `src/generated/` are auto-generated (see [Code Generation](#code-generation)). Do not edit them by hand.
- To add or update an entity:
  1. Update the DivKit schema in `../../schema/` (or the generator in `../../api_generator/`).
  2. Re-run `./codegen.sh` to regenerate `src/generated/`.
  3. Add the new type to `python/divkit_rs/__init__.py` exports and `__all__`.
  4. Add at least one Rust unit test that asserts `.dict()` output for the new type.
- Non-generated runtime logic lives in `src/entity.rs`, `src/py_entity.rs`, `src/py_value.rs`, and `src/python.rs`.

## Testing Tips

- Prefer small, declarative tests that compare `serde_json::json!(...)` values.
- When testing nested entities, assert key fields rather than entire deep structures if ordering/noise makes diffs brittle.
- When changing serialization behavior (e.g. float handling, key normalization), update assertions in **both** Rust tests (`src/lib.rs`) and Python tests (`tests/test_divkit_rs.py`).
- Float assertions in Rust tests: `json!(1.0)` and `json!(1)` are distinct `serde_json::Value` types — use the correct one matching expected output.
- Schema-driven coercion tests (bool/int) only apply to the Python path (`_set_fields` in `py_entity.rs`) — Rust-only entity construction does not coerce.
