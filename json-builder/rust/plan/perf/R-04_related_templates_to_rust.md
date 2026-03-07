# R-04: `_compat_related_templates()` → Rust — P2, High, -1% CPU

**Status**: DONE

**Depends on**: R-02 ✅

## What was done

1. **Native `related_templates()` in Rust** (`py_entity.rs:132`):
   - Walks DivValue tree collecting type names
   - No JSON serialization needed — direct DivValue traversal

2. **Python compat layer simplified** (`_pydivkit_compat.py:675`):
   - `_compat_related_templates` removed
   - `_compat_make_div` now uses native `div.related_templates()` directly

3. **Compat dict remains passthrough** (`_pydivkit_compat.py:574`)

## Benchmark

### General (no regression)
| Test | Before | After | Δ |
|------|--------|-------|---|
| simple_text | 6.5 us | 6.5 us | +0.0% |
| slider | 75.3 us | 75.5 us | +0.3% |
| gallery_100 | 1045.1 us | 1021.7 us | **-2.2%** |
| gallery_1000 | 10296.9 us | 10202.7 us | -0.9% |
| nested_20 | 1687.8 us | 1669.8 us | -1.1% |
| nested_50 | 9049.1 us | 8954.2 us | -1.0% |
| complex_layout | 2074.7 us | 2019.6 us | **-2.7%** |
| schema | 31.9 us | 31.7 us | -0.6% |
| json_serialize | 65.2 us | 61.9 us | -5.1% |

### R-04 micro-bench (make_div is the key metric)
| Test | Before | After | Δ |
|------|--------|-------|---|
| related_templates_hot | 0.5 us | 1.1 us | +120% |
| related_templates_cold | 5.7 us | 6.8 us | +19% |
| **make_div_hot** | 17.8 us | 13.3 us | **-25.3%** |
| **make_div_cold** | 23.0 us | 19.5 us | **-15.2%** |

## Verification

- `make test`: 165 passed
- `pytest tests/test_divkit_rs.py`: passed
