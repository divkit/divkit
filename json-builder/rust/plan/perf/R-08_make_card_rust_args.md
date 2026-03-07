# R-08: `_compat_make_card()` — перенести argument parsing в Rust — Medium, -50% make_card

**Status**: TODO

**Depends on**: нет (standalone)

## Goal

Перенести argument normalization и `_dump_entities()` из Python `_compat_make_card()` в Rust `make_card()`.

## Hotspot

89 samples. `_compat_make_card()` — Python функция с:
- Argument normalization (positional `*card_divs` vs keyword `divs=`)
- Auto-detect variable data structure (`hasattr`/`getattr`)
- `_dump_entities()` — цикл `isinstance(item, dict) else item.dict()` per item
- Создание `NativePyDivData` или `NativeDivData`

## Files

- `json-builder/rust/src/python.rs:17-23` — расширить `make_card()`
- `json-builder/rust/python/divkit_rs/_pydivkit_compat.py:552-612` — удалить `_dump_entities()` и `_compat_make_card()`

## Current Rust `make_card()` (`python.rs:17-23`)

```rust
#[pyfunction]
fn make_card(py: Python<'_>, log_id: &str, div: PyRef<'_, PyDivEntity>) -> PyResult<Py<PyAny>> {
    let rust_entity = div.to_rust_entity();
    let data = entity::make_card(log_id, rust_entity.as_ref());
    let json_val = <entity::DivData as Entity>::dict(&data);
    json_to_py(py, &json_val)
}
```

Принимает только `(log_id, div)` — нет variables/triggers/timers, нет multiple divs.

## Current Python `_compat_make_card()` (`_pydivkit_compat.py:559-612`)

```python
def _compat_make_card(
    log_id: str,
    *card_divs: PyDivEntity,
    divs: Sequence[PyDivEntity] | None = None,
    variables: Any = None,
    variable_triggers: Any = None,
    timers: Any = None,
) -> NativePyDivData | NativeDivData:
    # 1. Argument normalization
    if divs is not None and card_divs:
        raise TypeError("Provide either positional divs or `divs=` keyword, not both")
    selected_divs = tuple(divs) if divs is not None else tuple(card_divs)

    # 2. Auto-detect variable data object
    include_var_data = variables is not None or variable_triggers is not None or timers is not None
    if (variables is not None and variable_triggers is None and timers is None
        and (isinstance(variables, PyDivEntity)
             or hasattr(variables, "variables")
             or hasattr(variables, "variable_triggers")
             or hasattr(variables, "timers"))):
        variable_triggers = getattr(variables, "variable_triggers", None)
        timers = getattr(variables, "timers", None)
        variables = getattr(variables, "variables", None)
        include_var_data = True

    # 3. Simple path (no var data)
    if not include_var_data:
        return NativePyDivData(
            log_id=log_id,
            states=[NativePyDivDataState(state_id=i, div=d) for i, d in enumerate(selected_divs)],
        )

    # 4. Complex path (with var data)
    card = NativeDivData(
        log_id=log_id,
        states=[NativeDivDataState(state_id=i, div=d) for i, d in enumerate(selected_divs)],
    )
    card.variables = _dump_entities(variables)
    card.variable_triggers = _dump_entities(variable_triggers)
    card.timers = _dump_entities(timers)
    return card
```

## Changes

### Шаг 1: Расширить сигнатуру Rust `make_card()`

```rust
#[pyfunction]
#[pyo3(signature = (log_id, *card_divs, divs=None, variables=None, variable_triggers=None, timers=None))]
fn make_card(
    py: Python<'_>,
    log_id: &str,
    card_divs: &Bound<'_, PyTuple>,
    divs: Option<&Bound<'_, PyAny>>,
    variables: Option<&Bound<'_, PyAny>>,
    variable_triggers: Option<&Bound<'_, PyAny>>,
    timers: Option<&Bound<'_, PyAny>>,
) -> PyResult<Py<PyAny>> {
    // Argument normalization
    if divs.is_some() && !card_divs.is_empty() {
        return Err(pyo3::exceptions::PyTypeError::new_err(
            "Provide either positional divs or `divs=` keyword, not both"
        ));
    }

    let selected: Vec<PyRef<'_, PyDivEntity>> = if let Some(d) = divs {
        d.iter()?.map(|item| item?.extract::<PyRef<PyDivEntity>>()).collect::<PyResult<_>>()?
    } else {
        card_divs.iter().map(|item| item.extract::<PyRef<PyDivEntity>>()).collect::<PyResult<_>>()?
    };

    // Auto-detect variable data object
    let (vars, var_triggers, tmrs) = normalize_var_data(py, variables, variable_triggers, timers)?;

    let has_var_data = vars.is_some() || var_triggers.is_some() || tmrs.is_some();

    if !has_var_data {
        // Simple path — use NativePyDivData
        // Build states from selected divs
        // ...
    } else {
        // Complex path — build with var data
        // dump_entities for vars/triggers/timers
        // ...
    }
}
```

### Шаг 2: `normalize_var_data()` в Rust

```rust
fn normalize_var_data<'py>(
    py: Python<'py>,
    variables: Option<&Bound<'py, PyAny>>,
    variable_triggers: Option<&Bound<'py, PyAny>>,
    timers: Option<&Bound<'py, PyAny>>,
) -> PyResult<(Option<Bound<'py, PyAny>>, Option<Bound<'py, PyAny>>, Option<Bound<'py, PyAny>>)> {
    let vars = variables;
    let vt = variable_triggers;
    let tm = timers;

    if let Some(v) = vars {
        if vt.is_none() && tm.is_none() {
            // Auto-detect: check if `variables` is a compound object
            let is_entity = v.extract::<PyRef<PyDivEntity>>().is_ok();
            let has_variables = v.hasattr("variables")?;
            let has_vt = v.hasattr("variable_triggers")?;
            let has_timers = v.hasattr("timers")?;
            if is_entity || has_variables || has_vt || has_timers {
                let new_vt = v.getattr("variable_triggers").ok();
                let new_tm = v.getattr("timers").ok();
                let new_vars = v.getattr("variables").ok();
                return Ok((new_vars, new_vt, new_tm));
            }
        }
    }
    Ok((vars.map(|v| v.clone()), vt.map(|v| v.clone()), tm.map(|v| v.clone())))
}
```

### Шаг 3: `dump_entities()` в Rust

```rust
fn dump_entities(py: Python<'_>, items: Option<&Bound<'_, PyAny>>) -> PyResult<Vec<serde_json::Value>> {
    let Some(items) = items else { return Ok(vec![]); };
    if items.is_none() {
        return Ok(vec![]);
    }
    let iter = items.iter()?;
    let mut result = Vec::new();
    for item_result in iter {
        let item = item_result?;
        if let Ok(dict) = item.downcast::<PyDict>() {
            // Already a dict — convert to JSON
            result.push(py_dict_to_json(py, dict)?);
        } else if let Ok(entity) = item.extract::<PyRef<PyDivEntity>>() {
            // PyDivEntity — use Rust serialization
            let json = entity.to_rust_entity().dict();
            result.push(json);
        } else {
            return Err(pyo3::exceptions::PyTypeError::new_err(
                "Expected dict or PyDivEntity"
            ));
        }
    }
    Ok(result)
}
```

### Шаг 4: Обновить `_pydivkit_compat.py`

- Удалить `_dump_entities()` (строки 552-556)
- Удалить `_compat_make_card()` (строки 559-612)
- В `install_pydivkit_compat()` — убрать monkey-patch `make_card`

## Considerations

- `NativePyDivData` и `NativeDivData` — Rust structs, используются по-разному. Simple path возвращает `NativePyDivData` (без variable data), complex path — `NativeDivData`.
- Auto-detect логика: если `variables` — это объект с атрибутами `variables`/`variable_triggers`/`timers`, развернуть его. Это пришло из pydivkit compatibility.
- `_dump_entities()` принимает None → пустой список. Нужно сохранить.

## Validation

1. `maturin develop --release`
2. `uv run pytest tests/test_divkit_rs.py -v`
3. `make test`
4. Проверить вызовы `make_card()` в mosaic — все signature variants:
   - `make_card("id", div)` — single div
   - `make_card("id", div1, div2)` — multiple divs
   - `make_card("id", divs=[div1, div2])` — keyword divs
   - `make_card("id", div, variables=var_data)` — with variables
5. `bench_blocks_ab.sh`

## Risk

Средний. Auto-detect логика (`hasattr(variables, "variables")`) — нужно точно воспроизвести в Rust. Edge case: когда `variables` — PyDivEntity с атрибутами.
