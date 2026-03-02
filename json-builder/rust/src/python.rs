use pyo3::prelude::*;
use pyo3::types::{PyDict, PyList, PyString, PyTuple};

use super::py_entity::{PyDivData, PyDivDataState, PyDivEntity};
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
fn make_card(
    py: Python<'_>,
    log_id: &str,
    div: PyRef<'_, PyDivEntity>,
) -> PyResult<Py<PyAny>> {
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

fn compat_dump_bound(py: Python<'_>, value: &Bound<'_, PyAny>) -> PyResult<Py<PyAny>> {
    if value.is_none() {
        return Ok(py.None());
    }
    if value.is_instance_of::<PyString>() {
        return Ok(value.clone().unbind());
    }

    if value.is_instance_of::<PyDivEntity>() {
        if is_separator_style_subclass(value)? {
            let raw_dumped = value.call_method0("dict")?;
            if let Ok(raw_dict) = raw_dumped.cast::<PyDict>() {
                let is_empty_or_color_only = raw_dict.is_empty()
                    || raw_dict
                        .keys()
                        .iter()
                        .all(|k| k.extract::<String>().map(|name| name == "color").unwrap_or(false));
                if is_empty_or_color_only {
                    return Ok(PyDict::new(py).into_any().unbind());
                }
            }
        }
        return Ok(value.call_method0("dict")?.unbind());
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

    Ok(value.clone().unbind())
}

#[pyfunction]
fn compat_dump(py: Python<'_>, value: &Bound<'_, PyAny>) -> PyResult<Py<PyAny>> {
    compat_dump_bound(py, value)
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
    m.add_function(wrap_pyfunction!(compat_dump, m)?)?;

    Ok(())
}
