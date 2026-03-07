# R-07: `_template_names_from_json()` → collect при Rust serialization — Medium, -90% template scan

**Status**: TODO

**Depends on**: R-06 (лучше после, использует ту же архитектуру)

## Goal

Заменить Python-рекурсию `_collect_template_names_from_json()` на Rust-side collection type names из `serde_json::Value`.

## Hotspot

119 samples. `_collect_template_names_from_json()` рекурсивно обходит Python dict, ища `"type"` поля. Это происходит **после** Rust serialization — т.е. данные уже были в Rust (serde_json), конвертировались в Python dict, и потом Python снова обходит dict.

## Files

- `json-builder/rust/src/py_entity.rs` — новый метод + интеграция с `related_templates()`
- `json-builder/rust/python/divkit_rs/_pydivkit_compat.py:108-118` — заменить Python функцию

## Current flow

```
related_templates() [Rust, py_entity.rs:138+]
  → calls Python _compat_related_templates()
    → entity.dict() → Python dict
    → _collect_template_names_from_json(dict, out_set)  ← 119 samples
    → lookup template classes by name
    → return set of template classes
```

## Current Python code (`_pydivkit_compat.py:108-118`)

```python
def _collect_template_names_from_json(value: Any, out: set[str]) -> None:
    if isinstance(value, dict):
        type_name = value.get("type")
        if isinstance(type_name, str):
            out.add(type_name)
        for item in value.values():
            _collect_template_names_from_json(item, out)
        return
    if isinstance(value, list):
        for item in value:
            _collect_template_names_from_json(item, out)
```

## Changes

### Шаг 1: Rust-side `collect_type_names()`

В `py_entity.rs`:

```rust
fn collect_type_names(value: &serde_json::Value, out: &mut HashSet<String>) {
    match value {
        serde_json::Value::Object(map) => {
            if let Some(serde_json::Value::String(type_name)) = map.get("type") {
                out.insert(type_name.clone());
            }
            for v in map.values() {
                collect_type_names(v, out);
            }
        }
        serde_json::Value::Array(arr) => {
            for v in arr {
                collect_type_names(v, out);
            }
        }
        _ => {}
    }
}
```

### Шаг 2: Новый Python-exposed метод

```rust
/// Collect all "type" field values from this entity's JSON representation.
fn collect_template_names_from_dict(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
    let entity = self.to_rust_entity();
    let json_val = entity.dict();
    let mut names = HashSet::new();
    collect_type_names(&json_val, &mut names);

    // Также учесть constructor_values (могут содержать вложенные entities)
    if let Some(cv) = &self.constructor_values {
        for (_, py_val) in cv {
            let bound = py_val.bind(py);
            if let Ok(entity) = bound.as_any().extract::<PyRef<PyDivEntity>>() {
                let nested_json = entity.to_rust_entity().dict();
                collect_type_names(&nested_json, &mut names);
            }
        }
    }

    let py_set = PySet::new(py, names.iter())?;
    Ok(py_set.into())
}
```

### Шаг 3: Интеграция с `related_templates()`

В `related_templates()` (py_entity.rs:138+) — использовать `collect_template_names_from_dict()` вместо вызова Python `_collect_template_names_from_json()`.

### Шаг 4 (опционально): Кеш при `dict()`

Если R-06 реализован, `dict()` уже имеет `json_val` до конвертации. Можно собирать type names как побочный продукт:

```rust
pub(crate) fn dict(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
    let entity = self.to_rust_entity();
    let mut json_val = entity.dict();
    // ... merge constructor_values/defaults (R-06) ...

    // Побочный продукт: collect type names
    let mut names = HashSet::new();
    collect_type_names(&json_val, &mut names);
    // Cache for later use by related_templates()
    // ...

    json_to_py(py, &json_val)
}
```

Но кеш усложняет lifetime management. Проще вызывать `collect_type_names` отдельно в `related_templates()`.

## Considerations

- constructor_values и defaults могут содержать вложенные PyDivEntity с собственными "type" полями — нужно учесть.
- `_collect_related_templates_from_value()` (line 120+) тоже обходит значения — это **другая** функция, не путать. Она ищет PyDivEntity instances, а не JSON type strings.

## Validation

1. `maturin develop --release`
2. `uv run pytest tests/test_divkit_rs.py -v`
3. `make test`
4. Проверить: `related_templates()` возвращает те же templates, что и до оптимизации.

## Risk

Средний. Нужно убедиться, что Rust-side collection учитывает все источники type names (base entity + constructor_values + defaults).
