use std::collections::HashMap;

use pyo3::prelude::*;
use pyo3::types::{PyDict, PyList};

use crate::entity::{DivData, DivDataState, Entity};
use crate::field::FieldDescriptor;
use crate::value::DivValue;

use super::py_value::{divvalue_to_py, json_to_py, py_to_divvalue};

/// Metadata for a registered entity type.
#[derive(Clone)]
pub struct EntityTypeMeta {
    pub type_name: Option<String>,
    pub class_name: String,
    pub field_names: Vec<String>,
    pub required_fields: Vec<String>,
}

/// Generic Python wrapper for any DivKit entity.
#[pyclass(subclass, from_py_object)]
#[derive(Clone)]
pub struct PyDivEntity {
    pub type_meta: EntityTypeMeta,
    pub fields: HashMap<String, DivValue>,
}

impl PyDivEntity {
    pub fn to_rust_entity(&self) -> Box<dyn Entity> {
        Box::new(DynamicEntity {
            type_name: self.type_meta.type_name.clone(),
            class_name: self.type_meta.class_name.clone(),
            field_names: self.type_meta.field_names.clone(),
            required_fields: self.type_meta.required_fields.clone(),
            fields: self.fields.clone(),
        })
    }
}

#[pymethods]
impl PyDivEntity {
    #[new]
    #[pyo3(signature = (**kwargs))]
    fn new_py(kwargs: Option<&Bound<'_, PyDict>>) -> PyResult<Self> {
        let mut fields = HashMap::new();
        if let Some(kw) = kwargs {
            for (k, v) in kw.iter() {
                let key: String = k.extract()?;
                fields.insert(key, py_to_divvalue(&v)?);
            }
        }
        Ok(PyDivEntity {
            type_meta: EntityTypeMeta {
                type_name: None,
                class_name: "PyDivEntity".to_string(),
                field_names: vec![],
                required_fields: vec![],
            },
            fields,
        })
    }

    fn dict(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        let entity = self.to_rust_entity();
        let json_val = entity.dict();
        json_to_py(py, &json_val)
    }

    fn build(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        self.dict(py)
    }

    /// Return related templates collected from this entity's field tree.
    ///
    /// Mirrors pydivkit's `related_templates()` which returns an iterable of
    /// template-like objects.  Currently `DynamicEntity` has no template
    /// mechanism so this always returns an empty list, but exposing the method
    /// prevents `AttributeError` in code that probes for it.
    fn related_templates<'py>(&self, py: Python<'py>) -> PyResult<Bound<'py, PyList>> {
        Ok(PyList::empty(py))
    }

    #[pyo3(signature = (exclude_fields=None))]
    fn schema(&self, py: Python<'_>, exclude_fields: Option<Vec<String>>) -> PyResult<Py<PyAny>> {
        let entity = self.to_rust_entity();
        let exclude_refs: Option<Vec<&str>> =
            exclude_fields.as_ref().map(|v| v.iter().map(|s| s.as_str()).collect());
        let json_val = entity.schema(exclude_refs.as_deref());
        json_to_py(py, &json_val)
    }

    fn __repr__(slf: &Bound<'_, Self>) -> PyResult<String> {
        let this = slf.borrow();
        let fields: Vec<String> = this
            .fields
            .iter()
            .map(|(k, v)| format!("{}={:?}", k, v))
            .collect();
        let class_name = slf.get_type().qualname()?;
        Ok(format!("{}({})", class_name, fields.join(", ")))
    }

    fn __getattr__(&self, py: Python<'_>, name: &str) -> PyResult<Py<PyAny>> {
        if let Some(val) = self.fields.get(name) {
            divvalue_to_py(py, val)
        } else {
            Err(pyo3::exceptions::PyAttributeError::new_err(format!(
                "'{}' object has no attribute '{}'",
                self.type_meta.class_name, name
            )))
        }
    }

    fn __setattr__(&mut self, name: String, value: &Bound<'_, PyAny>) -> PyResult<()> {
        let dv = py_to_divvalue(value)?;
        self.fields.insert(name, dv);
        Ok(())
    }

    /// Internal: configure type metadata (called from dynamically created subclasses).
    #[pyo3(signature = (class_name, type_name, field_names, required_fields))]
    fn _configure(
        &mut self,
        class_name: String,
        type_name: Option<String>,
        field_names: Vec<String>,
        required_fields: Vec<String>,
    ) {
        self.type_meta = EntityTypeMeta {
            type_name,
            class_name,
            field_names,
            required_fields,
        };
    }

    /// Internal: replace all fields from a Python dict.
    ///
    /// Called from generated `__init__` to ensure kwargs passed to
    /// `super().__init__(**kwargs)` by subclasses are properly captured,
    /// overriding whatever `__new__` originally stored.
    #[pyo3(signature = (fields_dict))]
    fn _set_fields(&mut self, fields_dict: &Bound<'_, PyDict>) -> PyResult<()> {
        let mut fields = HashMap::with_capacity(fields_dict.len());
        for (k, v) in fields_dict.iter() {
            let key: String = k.extract()?;
            let value = py_to_divvalue(&v)?;
            // Preserve pydivkit semantics: None-valued kwargs behave as unset fields.
            if !value.is_null() {
                fields.insert(key, value);
            }
        }
        self.fields = fields;
        Ok(())
    }
}

/// A dynamic Entity constructed from a HashMap at runtime.
#[derive(Debug, Clone)]
pub struct DynamicEntity {
    pub type_name: Option<String>,
    pub class_name: String,
    pub field_names: Vec<String>,
    pub required_fields: Vec<String>,
    pub fields: HashMap<String, DivValue>,
}

// Safety: DivValue is Send+Sync since Entity trait now requires it
unsafe impl Send for DynamicEntity {}
unsafe impl Sync for DynamicEntity {}

impl Entity for DynamicEntity {
    fn type_name(&self) -> Option<&str> {
        self.type_name.as_deref()
    }

    fn field_descriptors(&self) -> Vec<FieldDescriptor> {
        // Try to look up the rich field descriptors from the schema registry.
        // The class_name should match the generated entity name (e.g. "DivContainer").
        if let Some(descs) =
            crate::generated::schema_registry::entity_field_descriptors(&self.class_name)
        {
            return descs;
        }

        // Fallback: build basic descriptors from metadata
        let mut descs = Vec::new();
        if let Some(ref tn) = self.type_name {
            descs.push(
                FieldDescriptor::new("type")
                    .with_default(serde_json::Value::String(tn.clone()))
                    .required(),
            );
        }
        for name in &self.field_names {
            let mut d = FieldDescriptor::new(name);
            if self.required_fields.contains(name) {
                d = d.required();
            }
            descs.push(d);
        }
        descs
    }

    fn field_values(&self) -> Vec<(String, DivValue)> {
        let mut values = Vec::new();
        if let Some(ref tn) = self.type_name {
            values.push(("type".to_string(), DivValue::String(tn.clone())));
        }
        for (name, val) in &self.fields {
            if !val.is_null() {
                values.push((name.clone(), val.clone()));
            }
        }
        values
    }
}

/// Register an entity type as a real Python class (subclass of PyDivEntity) on the module.
///
/// This creates a proper Python type so that `isinstance()`, subclassing, and
/// pattern matching all work correctly.
pub fn register_entity_class(
    py: Python<'_>,
    module: &Bound<'_, pyo3::types::PyModule>,
    class_name: &str,
    type_name: Option<&str>,
    field_names: &[&str],
    required_fields: &[&str],
) -> PyResult<()> {
    let type_name_py = match type_name {
        Some(tn) => format!("'{}'", tn),
        None => "None".to_string(),
    };
    let field_names_py = format!(
        "[{}]",
        field_names
            .iter()
            .map(|f| format!("'{}'", f))
            .collect::<Vec<_>>()
            .join(", ")
    );
    let required_fields_py = format!(
        "[{}]",
        required_fields
            .iter()
            .map(|f| format!("'{}'", f))
            .collect::<Vec<_>>()
            .join(", ")
    );

    // Create a real Python class that inherits from PyDivEntity.
    //
    // Design for subclassability:
    //  - _type_name, _field_names, _required_fields are class attrs so
    //    subclasses can introspect them.
    //  - __init__ collects class-level field defaults from the MRO, merges
    //    with explicit kwargs (kwargs win), then calls _set_fields to replace
    //    whatever __new__ stored. This means:
    //      class Card(DivContainer):
    //          orientation = "vertical"
    //      Card(items=[]).dict()  →  includes orientation="vertical"
    //  - Subclasses can override __init__ and call super().__init__(**kwargs)
    //    and the kwargs will be properly captured.
    let code = format!(
        concat!(
            "class {cls}(_Base):\n",
            "    __module__ = 'divkit_rs._native'\n",
            "    _type_name = {tn}\n",
            "    _field_names = {fns}\n",
            "    _required_fields = {rfs}\n",
            "    def __init__(self, **kwargs):\n",
            "        cls = type(self)\n",
            "        tn = cls._type_name\n",
            "        fns = cls._field_names\n",
            "        rfs = cls._required_fields\n",
            "        self._configure(cls.__name__, tn, list(fns), list(rfs))\n",
            "        defaults = {{}}\n",
            "        field_set = set(fns)\n",
            "        for klass in reversed(cls.__mro__):\n",
            "            for key, val in vars(klass).items():\n",
            "                if key in field_set:\n",
            "                    defaults[key] = val\n",
            "        defaults.update(kwargs)\n",
            "        self._set_fields(defaults)\n",
        ),
        cls = class_name,
        tn = type_name_py,
        fns = field_names_py,
        rfs = required_fields_py,
    );

    let ns = PyDict::new(py);
    ns.set_item("_Base", module.getattr("PyDivEntity")?)?;

    let code_cstr = std::ffi::CString::new(code).map_err(|e| {
        pyo3::exceptions::PyRuntimeError::new_err(format!(
            "Failed to create class {}: {}",
            class_name, e
        ))
    })?;

    py.run(&code_cstr, Some(&ns), Some(&ns))?;

    let cls = ns
        .get_item(class_name)?
        .ok_or_else(|| {
            pyo3::exceptions::PyRuntimeError::new_err(format!(
                "Failed to create class {}",
                class_name
            ))
        })?;

    module.setattr(class_name, &cls)?;
    Ok(())
}

/// Python wrapper for DivData.
#[derive(Clone)]
#[pyclass(from_py_object)]
pub struct PyDivData {
    pub log_id: String,
    pub states: Vec<PyDivDataState>,
}

#[derive(Clone)]
#[pyclass(from_py_object)]
pub struct PyDivDataState {
    pub state_id: i64,
    pub div: PyDivEntity,
}

#[pymethods]
impl PyDivData {
    #[new]
    #[pyo3(signature = (log_id, states))]
    fn new_py(log_id: String, states: Vec<PyDivDataState>) -> Self {
        PyDivData { log_id, states }
    }

    fn dict(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        let rust_states: Vec<DivDataState> = self
            .states
            .iter()
            .map(|s| DivDataState {
                state_id: s.state_id,
                div: s.div.to_rust_entity(),
            })
            .collect();
        let data = DivData {
            log_id: self.log_id.clone(),
            states: rust_states,
        };
        let json_val = <DivData as Entity>::dict(&data);
        json_to_py(py, &json_val)
    }

    fn build(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        self.dict(py)
    }
}

#[pymethods]
impl PyDivDataState {
    #[new]
    #[pyo3(signature = (state_id, div))]
    fn new_py(state_id: i64, div: PyDivEntity) -> Self {
        PyDivDataState { state_id, div }
    }
}
