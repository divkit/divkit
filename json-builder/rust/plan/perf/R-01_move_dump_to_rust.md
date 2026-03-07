# R-01: Move `_dump()` to Rust

**Status**: DONE

**Depends on**: — (independent)

## Outcome

- Native `compat_dump_bound()` in `src/python.rs` handles primitives, `Expr`, `Mapping`, and `Sequence`.
- Python `_dump` is now a direct alias: `_dump = _compat_dump_native`.
- This work is the base for R-02/R-03/R-04/R-05 compat-path optimization.

## Context

From the optimization checklist, `_dump()` is a hot function called from `_compat_dict` (472 samples in flamegraph). It's a recursive Python function with isinstance-chains that could be handled faster in Rust. The existing Rust `compat_dump_bound()` already handles PyDivEntity/list/tuple/dict but **misses** Expr, Mapping, Sequence, and primitive fast paths. The Python `_dump()` bridges the gap by handling those types before delegating to Rust.

**Goal**: Extend `compat_dump_bound()` in Rust to handle all types that `_dump()` currently handles, then replace the Python `_dump()` with a direct call to the Rust function.

## Changes

### 1. Extend `compat_dump_bound()` in `python.rs`

**File**: `json-builder/rust/src/python.rs` (lines 42-94)

Add handling for:

1. **Primitives fast path** — `PyBool`, `PyInt`, `PyFloat` → return unchanged (currently fall through to fallback which works, but explicit check is clearer and avoids any future issues)

2. **Expr objects** — Check if object has attribute `v` and is instance of the `Expr` class (imported from `divkit_rs.core.fields`). Convert to string via `str()`. Implementation: check `obj.getattr("v")` after checking for `PyString` — if the object has a `.v` attribute that starts with `@{`, call `obj.str()` and return. Alternative: use `py.import("divkit_rs.core.fields")?.getattr("Expr")?` to get the class and use `is_instance()`.

3. **Mapping (collections.abc.Mapping)** — For objects that aren't `PyDict` but implement `Mapping`, call `PyDict::new()` + iterate via `.items()`. Implementation: after the dict check, try `obj.call_method0("items")` — if it succeeds and object has `__getitem__`, treat as mapping.

4. **Sequence (collections.abc.Sequence)** — For objects that aren't `PyList`/`PyTuple`/`PyString` but implement `Sequence`, iterate and recurse. Implementation: after list/tuple checks, try `obj.iter()` — if iterable and not str/bytes/dict, convert to list.

**Recommended approach**: Use Python's `collections.abc` for isinstance checks via `py.import()` cached in a `GILOnceCell`:

```rust
use pyo3::sync::GILOnceCell;

static MAPPING_TYPE: GILOnceCell<Py<PyType>> = GILOnceCell::new();
static SEQUENCE_TYPE: GILOnceCell<Py<PyType>> = GILOnceCell::new();
static EXPR_TYPE: GILOnceCell<Py<PyType>> = GILOnceCell::new();

fn get_mapping_type(py: Python<'_>) -> PyResult<&Bound<'_, PyType>> { ... }
fn get_sequence_type(py: Python<'_>) -> PyResult<&Bound<'_, PyType>> { ... }
fn get_expr_type(py: Python<'_>) -> PyResult<&Bound<'_, PyType>> { ... }
```

**Updated `compat_dump_bound` order:**
1. None → return None
2. PyString → return unchanged
3. PyBool/PyInt/PyFloat → return unchanged (new)
4. Expr (via cached type) → return `str(value)` (new)
5. PyDivEntity → call `.dict()` (existing)
6. PyList → recurse items (existing)
7. PyTuple → recurse items as list (existing)
8. PyDict → recurse values (existing)
9. Mapping (via cached ABC) → convert to dict, recurse values (new)
10. Sequence (via cached ABC) → convert to list, recurse items (new)
11. Fallback → return unchanged

### 2. Replace Python `_dump()` with Rust call

**File**: `json-builder/rust/python/divkit_rs/_pydivkit_compat.py` (lines 103-116)

Replace:
```python
def _dump(value: Any) -> Any:
    if value is None or type(value) in (int, float, bool):
        return value
    if isinstance(value, (str, bytes)):
        return value
    if isinstance(value, Expr):
        return str(value)
    if isinstance(value, (PyDivEntity, list, tuple, dict)):
        return _compat_dump_native(value)
    if isinstance(value, Mapping):
        return _compat_dump_native(dict(value))
    if isinstance(value, Sequence):
        return [_dump(v) for v in value]
    return value
```

With:
```python
_dump = _compat_dump_native
```

### 3. Handle `bytes` type

The Python `_dump()` passes through `bytes` unchanged. The Rust `compat_dump_bound` currently would fall through to the final passthrough for bytes, which is correct. No change needed.

## Files to modify

1. `json-builder/rust/src/python.rs` — extend `compat_dump_bound()`
2. `json-builder/rust/python/divkit_rs/_pydivkit_compat.py` — replace `_dump` function

## Verification

### Correctness
1. `cd json-builder/rust && maturin develop --release`
2. `cd json-builder/rust && uv run pytest tests/test_divkit_rs.py -v` — all existing tests pass
3. Verify Expr serialization: `Expr("@{value}")` → `"@{value}"` in dict output
4. Verify Mapping/Sequence handling with custom types
5. If mosaic project available, run `uv run pytest tests/` in mosaic

### Benchmarks (before/after)

**Step 0 — Baseline** (run BEFORE making changes):
```bash
cd json-builder/rust && maturin develop --release && python docs/bench_rust_binding.py
```
Save the output for comparison.

**Step 1 — After changes**:
```bash
cd json-builder/rust && maturin develop --release && python docs/bench_rust_binding.py
```

**Step 2 — Add `_dump`-specific microbenchmark** to `docs/bench_rust_binding.py`:
```python
def bench_dump_expr():
    """Benchmark _dump with Expr objects."""
    from divkit_rs.core.fields import Expr
    from divkit_rs._native import compat_dump
    expr = Expr("@{my_var}")
    return compat_dump(expr)

def bench_dump_nested():
    """Benchmark _dump with nested entity tree (simulates _compat_dict hot path)."""
    from divkit_rs._native import compat_dump
    container = dk.DivContainer(
        items=[dk.DivText(text=f"Item {i}") for i in range(10)],
        paddings=dk.DivEdgeInsets(left=4, right=4, top=4, bottom=4),
    )
    return compat_dump(container)
```

**Step 3 — Mosaic A/B** (if available):
```bash
cd ~/ExtraSource/mosaic-divkitrs && tools/bench_blocks_ab.sh
```

**Expected improvement**: -1–2% CPU on `_compat_dict` flamegraph samples (472 → ~400), measurable on nested entity benchmarks.
