use pyo3::prelude::*;
use pyo3::sync::PyOnceLock;
use pyo3::types::{PyBool, PyBytes, PyDict, PyFloat, PyInt, PyList, PyString, PyTuple, PyType};

use super::py_entity::{self, PyDivData, PyDivDataState, PyDivEntity};
use super::py_value::json_to_py;
use crate::entity::{self, Entity};
use crate::generated::python_generated;

#[pyfunction]
fn make_div(py: Python<'_>, div: PyRef<'_, PyDivEntity>) -> PyResult<Py<PyAny>> {
    let rust_entity = div.to_rust_entity();
    let json_val = entity::make_div(rust_entity.as_ref());
    json_to_py(py, &json_val)
}

#[pyfunction]
fn make_card(py: Python<'_>, log_id: &str, div: PyRef<'_, PyDivEntity>) -> PyResult<Py<PyAny>> {
    let rust_entity = div.to_rust_entity();
    let data = entity::make_card(log_id, rust_entity.as_ref());
    let json_val = <entity::DivData as Entity>::dict(&data);
    json_to_py(py, &json_val)
}

fn is_separator_style_subclass(obj: &Bound<'_, PyAny>) -> PyResult<bool> {
    let obj_type = obj.get_type();
    let class_name: String = obj_type.getattr("__name__")?.extract()?;
    if class_name == "DivSeparatorDelimiterStyle" {
        return Ok(false);
    }
    let mro = obj_type.getattr("__mro__")?;
    if let Ok(tuple) = mro.cast::<PyTuple>() {
        for base in tuple.iter().skip(1) {
            let base_name: String = base.getattr("__name__")?.extract()?;
            if base_name == "DivSeparatorDelimiterStyle" {
                return Ok(true);
            }
        }
    }
    Ok(false)
}

static MAPPING_TYPE: PyOnceLock<Py<PyType>> = PyOnceLock::new();
static SEQUENCE_TYPE: PyOnceLock<Py<PyType>> = PyOnceLock::new();
static EXPR_TYPE: PyOnceLock<Py<PyType>> = PyOnceLock::new();

fn get_mapping_type<'py>(py: Python<'py>) -> PyResult<&'py Bound<'py, PyType>> {
    MAPPING_TYPE.import(py, "collections.abc", "Mapping")
}

fn get_sequence_type<'py>(py: Python<'py>) -> PyResult<&'py Bound<'py, PyType>> {
    SEQUENCE_TYPE.import(py, "collections.abc", "Sequence")
}

fn get_expr_type<'py>(py: Python<'py>) -> PyResult<&'py Bound<'py, PyType>> {
    EXPR_TYPE.import(py, "divkit_rs.core.fields", "Expr")
}

pub(crate) fn compat_dump_bound(py: Python<'_>, value: &Bound<'_, PyAny>) -> PyResult<Py<PyAny>> {
    if value.is_none() {
        return Ok(py.None());
    }
    if value.is_instance_of::<PyString>() || value.is_instance_of::<PyBytes>() {
        return Ok(value.clone().unbind());
    }
    if value.is_instance_of::<PyBool>()
        || value.is_instance_of::<PyInt>()
        || value.is_instance_of::<PyFloat>()
    {
        return Ok(value.clone().unbind());
    }
    if value.is_instance(get_expr_type(py)?.as_any())? {
        return Ok(value.str()?.into_any().unbind());
    }

    if let Ok(entity) = value.extract::<PyRef<PyDivEntity>>() {
        // Call Rust dict() directly to avoid Python-level compat trampolines.
        // Safe because _compat_dict is currently a passthrough to native dict().
        let raw_dumped = entity.dict(py)?;
        let raw_dumped_bound = raw_dumped.bind(py);
        if is_separator_style_subclass(value)? {
            if let Ok(raw_dict) = raw_dumped_bound.cast::<PyDict>() {
                let is_empty_or_color_only = raw_dict.is_empty()
                    || raw_dict.keys().iter().all(|k| {
                        k.extract::<String>()
                            .map(|name| name == "color")
                            .unwrap_or(false)
                    });
                if is_empty_or_color_only {
                    return Ok(PyDict::new(py).into_any().unbind());
                }
            }
        }
        return Ok(raw_dumped);
    }

    if let Ok(list) = value.cast::<PyList>() {
        let out = PyList::empty(py);
        for item in list.iter() {
            out.append(compat_dump_bound(py, &item)?)?;
        }
        return Ok(out.into_any().unbind());
    }

    if let Ok(tuple) = value.cast::<PyTuple>() {
        let out = PyList::empty(py);
        for item in tuple.iter() {
            out.append(compat_dump_bound(py, &item)?)?;
        }
        return Ok(out.into_any().unbind());
    }

    if let Ok(dict) = value.cast::<PyDict>() {
        let out = PyDict::new(py);
        for (k, v) in dict.iter() {
            let key: String = k.extract()?;
            out.set_item(key, compat_dump_bound(py, &v)?)?;
        }
        return Ok(out.into_any().unbind());
    }

    if value.is_instance(get_mapping_type(py)?.as_any())? {
        let items = value.call_method0("items")?;
        let dict = PyDict::from_sequence(&items)?;
        let out = PyDict::new(py);
        for (k, v) in dict.iter() {
            let key: String = k.extract()?;
            out.set_item(key, compat_dump_bound(py, &v)?)?;
        }
        return Ok(out.into_any().unbind());
    }

    if value.is_instance(get_sequence_type(py)?.as_any())? {
        let out = PyList::empty(py);
        for item in value.try_iter()? {
            out.append(compat_dump_bound(py, &item?)?)?;
        }
        return Ok(out.into_any().unbind());
    }

    Ok(value.clone().unbind())
}

#[pyfunction]
fn normalize_pydivkit_json(py: Python<'_>, value: &Bound<'_, PyAny>) -> PyResult<Py<PyAny>> {
    normalize_json_bound(py, value, None)
}

fn normalize_json_bound(
    py: Python<'_>,
    value: &Bound<'_, PyAny>,
    parent_key: Option<&str>,
) -> PyResult<Py<PyAny>> {
    if let Ok(list) = value.cast::<PyList>() {
        let out = PyList::empty(py);
        for item in list.iter() {
            out.append(normalize_json_bound(py, &item, parent_key)?)?;
        }
        return Ok(out.into_any().unbind());
    }

    if let Ok(dict) = value.cast::<PyDict>() {
        let out = PyDict::new(py);
        for (k, v) in dict.iter() {
            let key: String = k.extract()?;
            let normalized_key = normalize_key(&key);
            let normalized_value = normalize_json_bound(py, &v, Some(normalized_key))?;
            let final_value =
                coerce_normalized_value(py, normalized_key, parent_key, normalized_value)?;
            out.set_item(normalized_key, final_value.bind(py))?;
        }
        return Ok(out.into_any().unbind());
    }

    Ok(value.clone().unbind())
}

fn normalize_key(key: &str) -> &str {
    match key {
        "top_left" => "top-left",
        "top_right" => "top-right",
        "bottom_left" => "bottom-left",
        "bottom_right" => "bottom-right",
        "$top_left" => "$top-left",
        "$top_right" => "$top-right",
        "$bottom_left" => "$bottom-left",
        "$bottom_right" => "$bottom-right",
        other => other,
    }
}

fn coerce_normalized_value(
    py: Python<'_>,
    normalized_key: &str,
    parent_key: Option<&str>,
    normalized_value: Py<PyAny>,
) -> PyResult<Py<PyAny>> {
    let value = normalized_value.bind(py);
    let is_non_bool_int = value.is_instance_of::<PyInt>() && !value.is_instance_of::<PyBool>();
    if !is_non_bool_int {
        return Ok(normalized_value);
    }

    let should_coerce_to_float = matches!(
        normalized_key,
        "alpha" | "ratio" | "weight" | "letter_spacing"
    ) || (normalized_key == "value"
        && matches!(parent_key, Some("x" | "y")))
        || (normalized_key == "width" && parent_key == Some("stroke"));

    if should_coerce_to_float {
        if let Ok(v) = value.extract::<i64>() {
            return Ok(PyFloat::new(py, v as f64).into_any().unbind());
        }
        return Ok(normalized_value);
    }

    if normalized_key == "color" {
        let text: String = value.str()?.extract()?;
        return Ok(PyString::new(py, &text).into_any().unbind());
    }

    Ok(normalized_value)
}

#[pyfunction]
fn compat_dump(py: Python<'_>, value: &Bound<'_, PyAny>) -> PyResult<Py<PyAny>> {
    compat_dump_bound(py, value)
}

fn getattr_or_none<'py>(
    obj: &Bound<'py, PyAny>,
    attr_name: &str,
) -> PyResult<Option<Bound<'py, PyAny>>> {
    match obj.getattr(attr_name) {
        Ok(value) if !value.is_none() => Ok(Some(value)),
        Ok(_) => Ok(None),
        Err(err) => {
            if err.is_instance_of::<pyo3::exceptions::PyAttributeError>(obj.py()) {
                Ok(None)
            } else {
                Err(err)
            }
        }
    }
}

fn dump_entities(py: Python<'_>, items: Option<&Bound<'_, PyAny>>) -> PyResult<Py<PyAny>> {
    let dumped = PyList::empty(py);
    if let Some(seq) = items {
        for item in seq.try_iter()? {
            let item = item?;
            if item.cast::<PyDict>().is_ok() {
                dumped.append(item)?;
            } else {
                dumped.append(item.call_method0("dict")?)?;
            }
        }
    }
    Ok(dumped.into_any().unbind())
}

#[pyfunction(signature = (log_id, *card_divs, divs=None, variables=None, variable_triggers=None, timers=None))]
fn compat_make_card(
    py: Python<'_>,
    log_id: &str,
    card_divs: &Bound<'_, PyTuple>,
    divs: Option<&Bound<'_, PyAny>>,
    variables: Option<&Bound<'_, PyAny>>,
    variable_triggers: Option<&Bound<'_, PyAny>>,
    timers: Option<&Bound<'_, PyAny>>,
) -> PyResult<Py<PyAny>> {
    let keyword_divs = divs.filter(|value| !value.is_none());
    if keyword_divs.is_some() && !card_divs.is_empty() {
        return Err(pyo3::exceptions::PyTypeError::new_err(
            "Provide either positional divs or `divs=` keyword, not both",
        ));
    }

    let selected_divs: Vec<Py<PyAny>> = if let Some(divs_value) = keyword_divs {
        divs_value
            .try_iter()?
            .map(|item| item.map(|v| v.unbind()))
            .collect::<PyResult<_>>()?
    } else {
        card_divs.iter().map(|item| item.unbind()).collect()
    };

    let mut variables_obj = variables.filter(|value| !value.is_none()).cloned();
    let mut variable_triggers_obj = variable_triggers.filter(|value| !value.is_none()).cloned();
    let mut timers_obj = timers.filter(|value| !value.is_none()).cloned();

    let mut include_var_data =
        variables_obj.is_some() || variable_triggers_obj.is_some() || timers_obj.is_some();

    if let Some(variables_value) = variables_obj.as_ref() {
        if variable_triggers_obj.is_none() && timers_obj.is_none() {
            let is_container_like = variables_value.extract::<PyRef<'_, PyDivEntity>>().is_ok()
                || variables_value.hasattr("variables")?
                || variables_value.hasattr("variable_triggers")?
                || variables_value.hasattr("timers")?;
            if is_container_like {
                variable_triggers_obj = getattr_or_none(variables_value, "variable_triggers")?;
                timers_obj = getattr_or_none(variables_value, "timers")?;
                variables_obj = getattr_or_none(variables_value, "variables")?;
                include_var_data = true;
            }
        }
    }

    if !include_var_data {
        let mut states = Vec::with_capacity(selected_divs.len());
        for (index, div_obj) in selected_divs.into_iter().enumerate() {
            states.push(PyDivDataState {
                state_id: index as i64,
                div: div_obj.bind(py).extract()?,
            });
        }
        return Ok(Py::new(
            py,
            PyDivData {
                log_id: log_id.to_string(),
                states,
            },
        )?
        .into_any());
    }

    let dumped_variables = dump_entities(py, variables_obj.as_ref())?;
    let dumped_variable_triggers = dump_entities(py, variable_triggers_obj.as_ref())?;
    let dumped_timers = dump_entities(py, timers_obj.as_ref())?;

    let native = py.import("divkit_rs._native")?;
    let div_data_state_cls = native.getattr("DivDataState")?;
    let div_data_cls = native.getattr("DivData")?;

    let states = PyList::empty(py);
    for (index, div_obj) in selected_divs.into_iter().enumerate() {
        let kwargs = PyDict::new(py);
        kwargs.set_item("state_id", index as i64)?;
        kwargs.set_item("div", div_obj.bind(py))?;
        states.append(div_data_state_cls.call((), Some(&kwargs))?)?;
    }

    let kwargs = PyDict::new(py);
    kwargs.set_item("log_id", log_id)?;
    kwargs.set_item("states", states)?;
    kwargs.set_item("variables", dumped_variables.bind(py))?;
    kwargs.set_item("variable_triggers", dumped_variable_triggers.bind(py))?;
    kwargs.set_item("timers", dumped_timers.bind(py))?;
    Ok(div_data_cls.call((), Some(&kwargs))?.unbind())
}

/// The native Python module `divkit_rs._native`.
#[pymodule]
pub fn _native(py: Python<'_>, m: &Bound<'_, PyModule>) -> PyResult<()> {
    m.add_class::<PyDivEntity>()?;
    m.add_class::<PyDivData>()?;
    m.add_class::<PyDivDataState>()?;

    python_generated::register_all_entities(py, m)?;
    python_generated::register_enums(py, m)?;
    python_generated::register_type_aliases(py, m)?;

    m.add_function(wrap_pyfunction!(make_div, m)?)?;
    m.add_function(wrap_pyfunction!(make_card, m)?)?;
    m.add_function(wrap_pyfunction!(compat_make_card, m)?)?;
    m.add_function(wrap_pyfunction!(compat_dump, m)?)?;
    m.add_function(wrap_pyfunction!(normalize_pydivkit_json, m)?)?;
    m.add_function(wrap_pyfunction!(register_type_meta, m)?)?;

    Ok(())
}

/// Register type metadata for a user-defined subclass so that _configure()
/// can do a fast cache lookup instead of accepting Vec<String> args.
#[pyfunction]
#[pyo3(signature = (class_name, type_name, field_names, required_fields))]
fn register_type_meta(
    class_name: String,
    type_name: Option<String>,
    field_names: Vec<String>,
    required_fields: Vec<String>,
) {
    py_entity::cache_type_meta(class_name, type_name, field_names, required_fields);
}
