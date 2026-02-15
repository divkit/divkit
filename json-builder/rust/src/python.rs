use pyo3::prelude::*;

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

    Ok(())
}
