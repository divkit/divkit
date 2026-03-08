# R-09: Native `_native.pyi` stubs — Status

## Status
**Completed**

## Scope delivered
- Added schema-driven stub generator: `generate_native_pyi.py`
- Added generated native stub: `python/divkit_rs/_native.pyi`
- Added PEP 561 marker: `python/divkit_rs/py.typed`
- Wired generation into `codegen.sh`
- Added Make targets:
  - `stubs`
  - `check-stubs`

## Alias typing — verified correct
- `TypeAlias` unions are per-`EntityEnumeration` members (not all entities).
- `collect_declarations()` extracts specific members from each `EntityEnumeration.entities` list.
- Example: `DivSize = DivFixedSize | DivMatchParentSize | DivWrapContentSize` (3 members, not 178)

## Verification
- `make stubs` ✅
- `make check-stubs` ✅
- `ruff check generate_native_pyi.py` ✅
- Mypy smoke (valid usage) ✅
- Mypy negative check (required/type mismatch) ✅
- Alias strictness check (`DivSize` vs `DivText`) ✅
- Runtime export coverage vs stub names: missing `0`, extra `0` ✅
- Wheel contains `divkit_rs/_native.pyi` and `divkit_rs/py.typed` ✅

## Files changed
- `generate_native_pyi.py`
- `python/divkit_rs/_native.pyi`
- `python/divkit_rs/py.typed`
- `codegen.sh`
- `Makefile`
