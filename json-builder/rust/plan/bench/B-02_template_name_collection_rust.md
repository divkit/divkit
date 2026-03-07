# B-02: Rust-side template-name collection for related templates

**Status**: DONE

**Priority**: High

**Goal**: Remove Python recursion `_collect_template_names_from_json()` from request-time `related_templates()` path.

## Scope

- Keep `related_templates()` output identical.
- Keep late template registration and cache invalidation behavior unchanged.
- Replace Python dict scanning with Rust-side type-name collection.

## Hotspot

- `_collect_template_names_from_json` in `python/divkit_rs/_pydivkit_compat.py`

## Files

- `src/py_entity.rs`
- `python/divkit_rs/_pydivkit_compat.py`
- `tests/test_divkit_rs.py`

## Implementation Tasks

1. Implement Rust-side type-name collector for entity payloads used by `related_templates()`.
2. Integrate collector into `PyDivEntity.related_templates()` path.
3. Remove Python callback dependency for template-name recursion.
4. Keep constructor-value based template discovery behavior intact.
5. Add targeted regression tests for nested templates and late registration.

## Validation

1. `maturin develop --release`
2. `pytest tests/test_divkit_rs.py -k "related_templates or make_div_collects" -q`
3. `pytest tests/test_divkit_rs.py -q`

## Bench Gate

- `related_templates_fresh`: >= 15% faster vs pre-change baseline.
- `make_div_fresh`: >= 8% faster vs pre-change baseline.

## Measured Before/After (median, us)

- `related_templates_fresh`: `19.83 -> 17.54` (`+11.55%`) for `B-01 -> B-02`.
- `make_div_fresh`: `31.08 -> 32.08` (`-3.22%`) for `B-01 -> B-02`.
- Final cumulative (`baseline -> final`):
  - `related_templates_fresh`: `19.62 -> 13.54` (`+30.99%`)
  - `make_div_fresh`: `32.62 -> 26.33` (`+19.28%`)

## Notes

- Incremental B-02 effect improves `related_templates_fresh` but does not by itself hit both gate targets.
- End-to-end cumulative numbers are strongly positive after B-04/B-05.

## Risk

- Medium: incorrect collection can silently drop template dependencies.
