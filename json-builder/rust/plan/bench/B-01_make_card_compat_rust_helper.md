# B-01: compat `make_card` normalization in Rust helper

**Status**: DONE

**Priority**: High

**Goal**: Remove Python overhead in `_compat_make_card` by moving argument normalization and var-data dumping to Rust while keeping strict pydivkit-compatible behavior.

## Scope

- Keep public `divkit_rs.make_card` behavior unchanged.
- Keep existing `_native.make_card` behavior unchanged.
- Add an internal/native compat helper used by Python compat layer.

## Hotspot

- `_compat_make_card` in `python/divkit_rs/_pydivkit_compat.py`:
  - positional vs keyword div normalization
  - var-data auto-unpack logic
  - `_dump_entities()` loop

## Files

- `src/python.rs`
- `python/divkit_rs/_pydivkit_compat.py`
- `tests/test_divkit_rs.py`

## Implementation Tasks

1. Add native compat helper in `_native` module with compat signature and return semantics.
2. Move var-data normalization (variables / variable_triggers / timers) into Rust helper.
3. Move entity/dict dumping of var-data lists into Rust helper.
4. Make `_compat_make_card` a thin passthrough to native helper.
5. Remove redundant Python `_dump_entities()` helper if no longer needed.

## Validation

1. `maturin develop --release`
2. `pytest tests/test_divkit_rs.py -k "make_card" -q`
3. `pytest tests/test_divkit_rs.py -q`

## Bench Gate

- `make_card_fresh`: >= 10% faster vs pre-change baseline.
- `make_card_with_vars`: >= 10% faster vs pre-change baseline.

## Measured Before/After (median, us)

- `make_card_fresh`: `17.00 -> 14.46` (`+14.94%`) for `baseline -> B-01`.
- `make_card_with_vars`: `32.50 -> 32.71` (`-0.65%`) for `baseline -> B-01`.
- Final cumulative (`baseline -> final`): `32.50 -> 26.62` (`+18.09%`).

## Notes

- Gate hit for `make_card_fresh`.
- `make_card_with_vars` gain appears after later dict-path optimizations in B-04/B-05.

## Risk

- Medium: strict parity for edge-case argument combinations and error messages must be preserved.
