# B-05: JSON-level overlay merge and allocation cleanup

**Status**: DONE

**Priority**: Medium

**Goal**: Reduce Python boundary churn in `dict()` by merging constructor/default overlays at JSON stage and trimming allocation overhead.

## Scope

- Preserve strict pydivkit compatibility.
- Keep all existing edge-case conversions (Expr, mappings/sequences, style corner cases).
- Minimize Python dict mutation operations after serialization.

## Files

- `src/py_entity.rs`
- `src/python.rs`
- `src/value.rs`
- `tests/test_divkit_rs.py`

## Implementation Tasks

1. Merge constructor values, defaults, and forced type name in Rust JSON representation before final `json_to_py`.
2. Ensure current conversion semantics are preserved for overlay values.
3. Reduce avoidable intermediate allocations and clones in map/list conversion loops.
4. Keep existing behavior for `None` defaults and mutable constructor-value tracking.
5. Add regression tests for edge cases affected by overlays.

## Validation

1. `maturin develop --release`
2. `pytest tests/test_divkit_rs.py -q`
3. `python docs/bench_hotspots.py`

## Bench Gate

- Additional cumulative gain after B-04:
  - `dict_fresh`: >= 10% extra improvement vs post-B-04.
  - `make_div_fresh`: >= 20% total improvement vs pre-B-01 baseline.

## Measured Before/After (median, us)

- Incremental `B-02 -> final`:
  - `dict_fresh`: `74.08 -> 56.75` (`+23.39%`)
  - `make_div_fresh`: `32.08 -> 26.33` (`+17.92%`)
- Cumulative `baseline -> final`:
  - `dict_fresh`: `86.67 -> 56.75` (`+34.52%`)
  - `make_div_fresh`: `32.62 -> 26.33` (`+19.28%`)

## Notes

- `dict_fresh` target cleared.
- `make_div_fresh` cumulative target (`>=20%`) is near miss at `+19.28%`.

## Risk

- High: overlay and conversion semantics are broad and easy to regress.
