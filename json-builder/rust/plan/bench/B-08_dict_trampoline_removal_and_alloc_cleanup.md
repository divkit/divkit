# B-08: Remove dict trampoline and allocation cleanup

**Status**: DONE

**Priority**: Medium

**Goal**: Remove Python no-op `dict()` trampoline and reduce temporary allocations in `related_templates()` hot path.

## Scope

- Stop installing `_compat_dict` as `PyDivEntity.dict`.
- Keep `_compat_dict` function available for compatibility imports.
- Reduce allocation churn for template name collection and constructor value traversal.

## Files

- `python/divkit_rs/_pydivkit_compat.py`
- `src/py_entity.rs`
- `tests/test_divkit_rs.py`

## Implementation

1. Removed `PyDivEntity.dict = _compat_dict` in `install_pydivkit_compat()`.
2. Kept `_compat_dict` function definition unchanged for backward compatibility.
3. In `related_templates()`:
   - pre-sized template-name HashSet
   - replaced full `constructor_values` map clone with `Vec<Arc<Py<PyAny>>>` value extraction.
4. Added regression test ensuring `_compat_dict(entity) == entity.dict()` output parity.

## Validation

1. `.venv/bin/python -m pytest tests/test_divkit_rs.py -q` (`123 passed`)

## Microbenchmark (Before vs After, median of 5 runs)

Command:

`python docs/bench_hotspots.py --iterations 350 --warmup 100`

| Metric | Before | After | Delta |
|---|---:|---:|---:|
| `dict_fresh` | 45.83 us | 45.25 us | +1.27% |
| `related_templates_fresh` | 11.54 us | 11.46 us | +0.69% |
| `make_card_fresh` | 12.79 us | 12.71 us | +0.63% |
| `make_card_with_vars` | 23.88 us | 23.71 us | +0.71% |
| `make_div_fresh` | 24.62 us | 24.25 us | +1.50% |

Artifacts:

- `/tmp/divkit-b06-b08/b08/summary.json`
- `/tmp/divkit-b06-b08/b08/before/run_*.txt`
- `/tmp/divkit-b06-b08/b08/after/run_*.txt`

## Notes

- Isolated B-08 gains are modest but consistently positive across all tracked metrics.
