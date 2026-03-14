# R-03: `_normalize_pydivkit_json()` → Rust serializer — P2, High, -1% CPU

**Status**: DONE

**Depends on**: — (independent)

## What was done

1. **Native JSON normalizer in Rust** (`python.rs:144`):
   - `normalize_pydivkit_json(py, value)` + recursive helpers
   - Key aliases: `top_left`→`top-left`, `$bottom_right`→`$bottom-right`, etc.
   - Float coercion: `alpha`/`ratio`/`weight`/`letter_spacing` int→float; `value` in x/y parent int→float; `width` in stroke parent int→float
   - Color coercion: `color` int→string
   - Bools remain bools (not coerced)

2. **Registered in `_native` module** (`python.rs:245`)

3. **Replaced Python implementation** (`_pydivkit_compat.py:34,720`):
   - Import `_normalize_pydivkit_json` from `_native`
   - Removed Python recursive implementation
   - `_compat_make_div` unchanged — still calls `_normalize_pydivkit_json(result)`

4. **Tests** (`test_divkit_rs.py:1000`):
   - Alias + float coercion + color coercion
   - Bools remain bools

## Verification

- `maturin develop --release`: passed
- `pytest tests/test_divkit_rs.py -v`: 118 passed
- `make test`: 165 passed
- `python docs/bench_rust_binding.py`: no regression (simple_text 6.3 us)
