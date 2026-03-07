# R-02: `_compat_dict()` entirely in Rust — P2, High, -2–3% CPU

**Status**: DONE

**Depends on**: R-01

## What was done

Moved `_compat_dict` merge behavior into Rust `PyDivEntity::dict()`:

1. **Rust-side storage on PyDivEntity** (`py_entity.rs:29`):
   - `constructor_values`, `defaults`, `forced_type_name`, `constructor_dirty`
   - Native methods: `_set/_get_constructor_value(s)`, `_set_defaults`

2. **Rust `dict()` now handles** (`py_entity.rs:80`):
   - Merges constructor-cached values (via `compat_dump_bound`)
   - Fills missing defaults
   - Forces type from `forced_type_name`

3. **Generated native class init** passes class defaults into Rust (`py_entity.rs:400`)

4. **`compat_dump_bound` exposed as `pub(crate)`** and reused by `PyDivEntity::dict()` (`python.rs:59`)

5. **Python compat layer simplified**:
   - `_compat_dict` is now passthrough: `return _ORIG_DICT(self)` (`_pydivkit_compat.py:619`)
   - Constructor cache routed through native getter/setter methods (`_pydivkit_compat.py:279`)

## Performance regression fixes

Initial R-02 had ~140x regression (465us vs 3us for simple_text). Root causes and fixes:

1. **`forced_type_name` only for templates** — `_configure()` now applies only when `type_name == class_name` (`py_entity.rs:180`)
2. **Dirty flag gates constructor merge** — `constructor_dirty` flag, `dict()` skips dump/compare when clean (`py_entity.rs:29,80`)
3. **Direct Rust dict() in compat_dump_bound** — bypasses Python monkey-patch trampoline (`python.rs:76`)
4. **`_compat_getattribute` skip private names** — no longer probes constructor cache for `_`-prefixed names (`_pydivkit_compat.py:553`)

## Verification

- `maturin develop --release`: passed
- `pytest tests/test_divkit_rs.py -v`: 116 passed
- `make test`: 165 passed

## Benchmark (post-fix)

| Test | Time |
|------|------|
| simple_text | 6.1 us |
| slider | 74.7 us |
| gallery_100 | 1010.9 us |
| gallery_1000 | 10104.7 us |
| nested_20 | 1685.7 us |
| nested_50 | 8774.2 us |
| complex_layout | 1961.5 us |
| schema | 30.8 us |
| compat_dump(Expr) | 0.5 us |
| compat_dump(nested) | 110.8 us |
