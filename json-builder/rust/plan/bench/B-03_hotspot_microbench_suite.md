# B-03: Hotspot microbench suite and baseline discipline

**Status**: DONE

**Priority**: High

**Goal**: Establish stable microbench coverage for the exact hot paths used as acceptance gates for B-01..B-05.

## Scope

- Add reproducible benchmark cases for request-like hot operations.
- Store baseline numbers and run protocol.
- Keep benchmarks local and dependency-light.

## Files

- `docs/bench_rust_binding.py` or `docs/bench_hotspots.py`
- `README.md` (optional benchmark command section)
- `plan/bench/INDEX.md` (reference to commands)

## Implementation Tasks

1. Add benchmarks:
   - `dict_fresh`
   - `related_templates_fresh`
   - `make_card_fresh`
   - `make_card_with_vars`
   - `make_div_fresh`
2. Add warmup/iteration defaults suitable for local repeatability.
3. Print median and average timing in microseconds.
4. Define baseline capture procedure in file header/doc block.
5. Keep benchmark payload close to real id=2 shape where feasible.

## Validation

1. `python docs/bench_hotspots.py`
2. Re-run 3 times and verify low variance for medians.

## Bench Gate

- Script must run reliably and produce all five metrics.
- Metric names and output format remain stable for future comparisons.

## Delivered

- Added `docs/bench_hotspots.py` with required stable metrics:
  - `dict_fresh`
  - `related_templates_fresh`
  - `make_card_fresh`
  - `make_card_with_vars`
  - `make_div_fresh`
- Added baseline capture protocol in script header.
- Added command reference in `plan/bench/INDEX.md`.

## Validation Snapshot

- Three-run samples (medians) remained within expected local variance for all five metrics.
- Staged benchmark artifacts stored under `/tmp/bench-each-task-final-9360Dq`.

## Risk

- Low: benchmark instability can hide true regressions if payload is too synthetic.
