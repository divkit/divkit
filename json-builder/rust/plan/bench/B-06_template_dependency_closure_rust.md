# B-06: Move template dependency closure to Rust

**Status**: DONE

**Priority**: High

**Goal**: Remove Python callback overhead from `_template_dependency_closure()` in `related_templates()` hot path by computing closure in Rust.

## Scope

- Replace runtime calls from Rust to Python `_template_dependency_closure`.
- Keep Python implementation available for compatibility surface (not hot path).
- Preserve template dependency semantics and cache invalidation behavior.

## Files

- `src/py_entity.rs`
- `tests/test_divkit_rs.py`

## Implementation

1. Added Rust-side `compute_template_dependency_closure()` with epoch-aware cache keyed by template name.
2. Switched `PyDivEntity.related_templates()` to use Rust closure expansion for:
   - current class if it is a template
   - template names discovered in entity payload
   - template classes discovered from constructor values
3. Added regression test for transitive template dependency collection.

## Validation

1. `.venv/bin/python -m pytest tests/test_divkit_rs.py -q` (`121 passed`)

## Microbenchmark (Before vs After, median of 5 runs)

Command:

`python docs/bench_hotspots.py --iterations 350 --warmup 100`

| Metric | Before | After | Delta |
|---|---:|---:|---:|
| `dict_fresh` | 45.50 us | 44.88 us | +1.36% |
| `related_templates_fresh` | 11.33 us | 11.54 us | -1.85% |
| `make_card_fresh` | 13.08 us | 12.58 us | +3.82% |
| `make_card_with_vars` | 24.08 us | 23.58 us | +2.08% |
| `make_div_fresh` | 24.92 us | 24.29 us | +2.53% |

Artifacts:

- `/tmp/divkit-b06-b08/b06/summary.json`
- `/tmp/divkit-b06-b08/b06/before/run_*.txt`
- `/tmp/divkit-b06-b08/b06/after/run_*.txt`

## Notes

- This change did not improve `related_templates_fresh` in isolation.
- Remaining callback overhead from constructor-value scanning still dominates this path before B-07.
