# R-05: Remove `WeakKeyDictionary` for constructor_values — P3, High, -0.5% CPU

**Status**: DONE

**Depends on**: R-02 ✅, R-04 ✅

## What was done

1. Removed `WeakKeyDictionary` usage from Python compat layer (`_pydivkit_compat.py`):
   - Removed `from weakref import WeakKeyDictionary`
   - Removed `_RELATED_TEMPLATES_CACHE`
   - Removed `_related_templates_cache_get/_set`
   - Removed `_invalidate_related_templates_cache`
   - Removed template-cache `.clear()` call and old invalidate call site

2. Routed constructor tracking entirely through native methods:
   - `_install_constructor_compat` now calls `instance._set_constructor_values(...)`
   - `_compat_getattribute` uses native `_get_constructor_value/_set_constructor_value`

3. Kept `related_templates` cache behavior by moving it fully to Rust (`py_entity.rs`):
   - Added native per-instance cache fields on `PyDivEntity`
   - Added native epoch invalidation (`_bump_related_templates_cache_epoch`)
   - `related_templates()` now uses native cache directly (no Python cache callbacks)

## Files to modify

- `json-builder/rust/python/divkit_rs/_pydivkit_compat.py`
- `json-builder/rust/src/py_entity.rs`

## Verification

1. `maturin develop --release`: passed
2. `uv run pytest tests/test_divkit_rs.py -v`: 118 passed
3. `make test`: 165 passed
4. `uv run python docs/bench_rust_binding.py`: no clear regression (run-to-run noise)
5. `rg -n "WeakKeyDictionary" .`: no runtime code matches (only plan docs)

## Notes

- Memory profiling/leak validation was not run in this task.
