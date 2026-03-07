# R-06: Убрать Python wrapper `_compat_dict()` — merge constructor_values/defaults в Rust — High, -80% compat dict

**Status**: TODO

**Depends on**: R-02 ✅, R-05 ✅

## Goal

Перенести merge-логику constructor_values + defaults + forced_type_name целиком в Rust, устранив Python↔Rust boundary crossing при overlay полей. Основная идея: merge в JSON Value (serde_json) **до** конвертации в Python dict.

## Hotspot

832 samples в `_compat_dict`. Текущий flow:
1. Rust `entity.dict()` → `serde_json::Value`
2. `json_to_py()` → Python dict
3. Цикл по `constructor_values` → `compat_dump_bound()` per field → `result.set_item()`
4. Цикл по `defaults` → `compat_dump_bound()` per field → `result.set_item()`
5. Override "type" field

Проблема: шаги 3-4 вызывают `compat_dump_bound()` для каждого поля — это Python↔Rust boundary crossing (достаём Py<PyAny>, конвертируем, кладём обратно).

## Files

- `json-builder/rust/src/py_entity.rs:88-128` — основные изменения
- `json-builder/rust/src/python.rs` — новая функция `py_val_to_json()`
- `json-builder/rust/python/divkit_rs/_pydivkit_compat.py:514` — проверить, что passthrough можно убрать

## Changes

### Шаг 1: Новая функция `py_val_to_json()`

В `python.rs` — обратная `json_to_py()`, конвертирует Python value в `serde_json::Value`:

```rust
pub(crate) fn py_val_to_json(py: Python<'_>, value: &Bound<'_, PyAny>) -> PyResult<serde_json::Value> {
    if value.is_none() {
        return Ok(serde_json::Value::Null);
    }
    if let Ok(s) = value.extract::<String>() {
        return Ok(serde_json::Value::String(s));
    }
    if let Ok(b) = value.downcast::<PyBool>() {
        return Ok(serde_json::Value::Bool(b.is_true()));
    }
    if let Ok(i) = value.extract::<i64>() {
        return Ok(serde_json::json!(i));
    }
    if let Ok(f) = value.extract::<f64>() {
        return Ok(serde_json::json!(f));
    }
    if let Ok(entity) = value.extract::<PyRef<PyDivEntity>>() {
        // Рекурсивно: entity → Rust entity → JSON
        let rust_entity = entity.to_rust_entity();
        return Ok(rust_entity.dict());
    }
    if value.is_instance(get_expr_type(py)?.as_any())? {
        let s: String = value.str()?.extract()?;
        return Ok(serde_json::Value::String(s));
    }
    if let Ok(dict) = value.downcast::<PyDict>() {
        let mut map = serde_json::Map::new();
        for (k, v) in dict.iter() {
            let key: String = k.extract()?;
            map.insert(key, py_val_to_json(py, &v)?);
        }
        return Ok(serde_json::Value::Object(map));
    }
    if let Ok(list) = value.downcast::<PyList>() {
        let arr: Vec<serde_json::Value> = list.iter()
            .map(|item| py_val_to_json(py, &item))
            .collect::<PyResult<_>>()?;
        return Ok(serde_json::Value::Array(arr));
    }
    // Fallback: str() representation
    let s: String = value.str()?.extract()?;
    Ok(serde_json::Value::String(s))
}
```

### Шаг 2: Переписать `dict()` — batch merge в JSON

В `py_entity.rs:88-128`:

```rust
pub(crate) fn dict(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
    let entity = self.to_rust_entity();
    let mut json_val = entity.dict(); // serde_json::Value

    // Merge constructor_values в JSON (до Python-конвертации)
    if self.constructor_dirty {
        if let Some(cv) = &self.constructor_values {
            if let serde_json::Value::Object(ref mut map) = json_val {
                for (field_name, py_val) in cv {
                    if map.contains_key(field_name) {
                        let dumped = py_val_to_json(py, py_val.bind(py).as_any())?;
                        let current = &map[field_name];
                        if *current != dumped {
                            map.insert(field_name.clone(), dumped);
                        }
                    }
                }
            }
        }
    }

    // Merge defaults в JSON
    if let Some(defaults) = &self.defaults {
        if let serde_json::Value::Object(ref mut map) = json_val {
            for (field_name, py_val) in defaults {
                if map.contains_key(field_name) {
                    continue;
                }
                let dumped = py_val_to_json(py, py_val.bind(py).as_any())?;
                if dumped != serde_json::Value::Null {
                    map.insert(field_name.clone(), dumped);
                }
            }
        }
    }

    // Type name override
    if let Some(type_name) = &self.forced_type_name {
        if let serde_json::Value::Object(ref mut map) = json_val {
            map.insert("type".to_string(), serde_json::Value::String(type_name.clone()));
        }
    }

    // Одна конвертация JSON → Python
    json_to_py(py, &json_val)
}
```

### Шаг 3: Проверить `_compat_dict` passthrough

`_compat_dict` (`_pydivkit_compat.py:514`) — уже passthrough `_ORIG_DICT(self)`. Проверить, можно ли убрать monkey-patch `dict → _compat_dict` из `install_pydivkit_compat()`.

## Key differences from current approach

| Аспект | Текущий | Новый |
|--------|---------|-------|
| JSON → Python | 1 раз, потом patch | 1 раз, уже merged |
| constructor_values loop | `compat_dump_bound()` per field → `set_item()` | `py_val_to_json()` per field → serde merge |
| defaults loop | `compat_dump_bound()` per field → `set_item()` | `py_val_to_json()` per field → serde merge |
| Python dict mutations | N set_item calls | 0 |
| Boundary crossings | 2N (dump + set per field) | N (dump only, result returned once) |

## Edge cases to handle

1. **Expr type** — `py_val_to_json` должен вызывать `str()` для Expr объектов
2. **SeparatorStyle subclass** — `compat_dump_bound` имеет специальную логику для `DivSeparatorDelimiterStyle`. Нужно воспроизвести в `py_val_to_json`.
3. **Nested PyDivEntity** — рекурсивный `to_rust_entity().dict()`
4. **Mapping/Sequence abc** — `compat_dump_bound` проверяет `isinstance(Mapping)` и `isinstance(Sequence)`. Нужно обрабатывать в `py_val_to_json`.

## Validation

1. `maturin develop --release`
2. `uv run pytest tests/test_divkit_rs.py -v`
3. `make test`
4. `uv run python docs/bench_rust_binding.py`
5. `bench_blocks_ab.sh id=1 id=2`
6. `compare_flamegraphs.py` (RATE=200, concurrency=8)

## Risk

Высокий. `py_val_to_json()` должен точно воспроизвести семантику `compat_dump_bound()` для всех типов. Тщательные тесты обязательны.
