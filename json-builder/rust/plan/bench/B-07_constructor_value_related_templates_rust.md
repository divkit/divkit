# B-07: Move constructor-value related-template scanning to Rust

**Status**: DONE

**Priority**: High

**Goal**: Remove per-invocation Python callback overhead from `_collect_related_templates_from_value()` by scanning constructor values in Rust.

## Scope

- Replace callback usage in `PyDivEntity.related_templates()` constructor-value path.
- Preserve behavior for mixed value types: entity classes, entity instances, dict/list/tuple/sequence values.
- Keep Python helper available for compatibility surface (not hot path).

## Files

- `src/py_entity.rs`
- `tests/test_divkit_rs.py`

## Implementation

1. Added Rust recursive collector `collect_related_from_constructor_value(...)`.
2. Replaced callback calls to Python `_collect_related_templates_from_value()` in `related_templates()`.
3. Added regression test for mixed constructor values resolving template dependencies.

## Validation

1. `.venv/bin/python -m pytest tests/test_divkit_rs.py -q` (`122 passed`)

## Microbenchmark (Before vs After, median of 5 runs)

Command:

`python docs/bench_hotspots.py --iterations 350 --warmup 100`

| Metric | Before | After | Delta |
|---|---:|---:|---:|
| `dict_fresh` | 45.00 us | 46.58 us | -3.51% |
| `related_templates_fresh` | 11.42 us | 11.54 us | -1.05% |
| `make_card_fresh` | 12.71 us | 12.94 us | -1.81% |
| `make_card_with_vars` | 23.42 us | 23.79 us | -1.58% |
| `make_div_fresh` | 24.40 us | 24.71 us | -1.27% |

Artifacts:

- `/tmp/divkit-b06-b08/b07/summary.json`
- `/tmp/divkit-b06-b08/b07/before/run_*.txt`
- `/tmp/divkit-b06-b08/b07/after/run_*.txt`

## Notes

- Isolated B-07 regressed in this environment.
- The Rust collector currently imports `collections.abc.Sequence` on each traversal path; follow-up tuning should cache this type lookup and reduce recursion overhead.
