# B-04: Zero-copy-oriented `PyDivEntity.dict()` path

**Status**: DONE

**Priority**: Medium

**Goal**: Remove clone-heavy `to_rust_entity()` dependency from the hot `dict()` path to reduce hidden Rust CPU and allocations.

## Scope

- Keep strict pydivkit-compatible JSON output.
- Avoid full `fields` and `type_meta` deep clones per `dict()` call.
- Keep dynamic entity behavior and subclass compatibility.

## Current Cost Driver

- `PyDivEntity::to_rust_entity()` clones:
  - `type_meta.field_names`
  - `type_meta.required_fields`
  - `fields` map and nested values

## Files

- `src/py_entity.rs`
- `src/value.rs`
- `src/py_value.rs`
- `tests/test_divkit_rs.py`

## Implementation Tasks

1. Add direct JSON serialization path from borrowed `PyDivEntity` data.
2. Use this path from `PyDivEntity.dict()` instead of constructing cloned `DynamicEntity`.
3. Preserve `type` emission and null filtering semantics.
4. Keep behavior for nested entities and expressions unchanged.
5. Add parity tests comparing pre/post output for representative nested structures.

## Validation

1. `maturin develop --release`
2. `pytest tests/test_divkit_rs.py -q`
3. `python docs/bench_hotspots.py`

## Bench Gate

- `dict_fresh`: >= 20% faster vs pre-B-04 baseline.

## Measured Before/After (median, us)

- Incremental `B-02 -> final` on `dict_fresh`: `74.08 -> 56.75` (`+23.39%`).
- Cumulative `baseline -> final` on `dict_fresh`: `86.67 -> 56.75` (`+34.52%`).

## Notes

- Bench gate hit (`+23.39%` incremental improvement exceeds `>=20%` target).

## Risk

- High: serialization refactor can introduce subtle ordering/shape differences.
