use pyo3::prelude::*;
use pyo3::conversion::IntoPyObject;
use pyo3::types::{PyBool, PyDict, PyFloat, PyInt, PyList, PyString};

use crate::entity::Entity;
use crate::expr::Expr;
use crate::value::DivValue;

use super::py_entity::PyDivEntity;

/// Convert a Python object into a DivValue.
pub fn py_to_divvalue(obj: &Bound<'_, PyAny>) -> PyResult<DivValue> {
    if obj.is_none() {
        return Ok(DivValue::Null);
    }

    // PyDivEntity — check before dict
    if let Ok(entity) = obj.extract::<PyRef<PyDivEntity>>() {
        let boxed: Box<dyn Entity> = entity.to_rust_entity();
        return Ok(DivValue::Entity(boxed));
    }

    // Bool (must check before int, since bool is a subclass of int in Python)
    if obj.is_instance_of::<PyBool>() {
        let v: bool = obj.extract()?;
        return Ok(DivValue::Bool(v));
    }

    // Int
    if obj.is_instance_of::<PyInt>() {
        let v: i64 = obj.extract()?;
        return Ok(DivValue::Int(v));
    }

    // Float
    if obj.is_instance_of::<PyFloat>() {
        let v: f64 = obj.extract()?;
        return Ok(DivValue::Float(v));
    }

    // String
    if obj.is_instance_of::<PyString>() {
        let v: String = obj.extract()?;
        if v.starts_with("@{") && v.ends_with('}') {
            return Ok(DivValue::Expr(
                Expr::new(&v).map_err(|e| pyo3::exceptions::PyValueError::new_err(e))?,
            ));
        }
        return Ok(DivValue::String(v));
    }

    // List
    if obj.is_instance_of::<PyList>() {
        let list = obj.cast::<PyList>()?;
        let mut items = Vec::with_capacity(list.len());
        for item in list.iter() {
            items.push(py_to_divvalue(&item)?);
        }
        return Ok(DivValue::Array(items));
    }

    // Dict
    if obj.is_instance_of::<PyDict>() {
        let dict = obj.cast::<PyDict>()?;
        let mut entries = Vec::with_capacity(dict.len());
        for (k, v) in dict.iter() {
            let key: String = k.extract()?;
            entries.push((key, py_to_divvalue(&v)?));
        }
        return Ok(DivValue::Map(entries));
    }

    // Duck-typed .dict() protocol: if the object has a callable .dict() method,
    // call it and convert the result. This allows passing pydivkit-compatible objects,
    // Pydantic models, or any object that can serialize itself to a dict.
    if let Ok(dict_method) = obj.getattr("dict") {
        if dict_method.is_callable() {
            let result = dict_method.call0()?;
            return py_to_divvalue(&result);
        }
    }

    // Fallback: convert to string
    let s: String = obj.str()?.extract()?;
    Ok(DivValue::String(s))
}

/// Convert a DivValue back to a Python object (for __getattr__).
pub fn divvalue_to_py(py: Python<'_>, val: &DivValue) -> PyResult<Py<PyAny>> {
    match val {
        DivValue::Null => Ok(py.None()),
        DivValue::Bool(b) => Ok((*b).into_pyobject(py)?.to_owned().into_any().unbind()),
        DivValue::Int(n) => Ok((*n).into_pyobject(py)?.into_any().unbind()),
        DivValue::Float(f) => Ok((*f).into_pyobject(py)?.into_any().unbind()),
        DivValue::String(s) => Ok(s.as_str().into_pyobject(py)?.into_any().unbind()),
        DivValue::Expr(e) => Ok(e.to_string().into_pyobject(py)?.into_any().unbind()),
        DivValue::Enum(s) => Ok(s.as_str().into_pyobject(py)?.into_any().unbind()),
        DivValue::Entity(e) => {
            // Serialize to JSON then convert to Python dict
            let json_val = e.dict();
            json_to_py(py, &json_val)
        }
        DivValue::Array(arr) => {
            let items: Vec<Py<PyAny>> = arr
                .iter()
                .map(|item| divvalue_to_py(py, item))
                .collect::<PyResult<_>>()?;
            Ok(PyList::new(py, &items)?.into_any().unbind())
        }
        DivValue::Map(entries) => {
            let dict = PyDict::new(py);
            for (k, v) in entries {
                dict.set_item(k, divvalue_to_py(py, v)?)?;
            }
            Ok(dict.into_any().unbind())
        }
    }
}

/// Convert a serde_json::Value to a Python object.
pub fn json_to_py(py: Python<'_>, val: &serde_json::Value) -> PyResult<Py<PyAny>> {
    match val {
        serde_json::Value::Null => Ok(py.None()),
        serde_json::Value::Bool(b) => Ok((*b).into_pyobject(py)?.to_owned().into_any().unbind()),
        serde_json::Value::Number(n) => {
            if let Some(i) = n.as_i64() {
                Ok(i.into_pyobject(py)?.into_any().unbind())
            } else if let Some(f) = n.as_f64() {
                Ok(f.into_pyobject(py)?.into_any().unbind())
            } else {
                Ok(py.None())
            }
        }
        serde_json::Value::String(s) => Ok(s.as_str().into_pyobject(py)?.into_any().unbind()),
        serde_json::Value::Array(arr) => {
            let items: Vec<Py<PyAny>> = arr
                .iter()
                .map(|item| json_to_py(py, item))
                .collect::<PyResult<_>>()?;
            Ok(PyList::new(py, &items)?.into_any().unbind())
        }
        serde_json::Value::Object(map) => {
            let dict = PyDict::new(py);
            for (k, v) in map {
                dict.set_item(k, json_to_py(py, v)?)?;
            }
            Ok(dict.into_any().unbind())
        }
    }
}
