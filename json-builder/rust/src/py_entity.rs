use std::collections::{HashMap, HashSet};
use std::sync::atomic::{AtomicU64, Ordering};
use std::sync::{Arc, Mutex};

use pyo3::prelude::*;
use pyo3::types::{PyDict, PyList, PySet};

use crate::entity::{DivData, DivDataState, Entity};
use crate::field::FieldDescriptor;
use crate::value::DivValue;

use super::py_value::{divvalue_to_py, json_to_py, py_to_divvalue};
use super::python::compat_dump_bound;

// ── Cached type metadata registry (avoids per-instance Vec<String> alloc) ──
static TYPE_META_CACHE: Mutex<Option<HashMap<String, EntityTypeMeta>>> = Mutex::new(None);
static RELATED_TEMPLATES_CACHE_EPOCH: AtomicU64 = AtomicU64::new(1);

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
    pub constructor_values: Option<HashMap<String, Arc<Py<PyAny>>>>,
    pub constructor_dirty: bool,
    pub defaults: Option<HashMap<String, Arc<Py<PyAny>>>>,
    pub forced_type_name: Option<String>,
    pub related_templates_cache: Option<Arc<Py<PyAny>>>,
    pub related_templates_cache_epoch: u64,
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
        let _ = kwargs;
        // Generated entity __init__ always calls _set_fields() with merged
        // defaults+kwargs, replacing whatever __new__ stored. Keep __new__
        // allocation-only to avoid duplicate py_to_divvalue conversion.
        Ok(PyDivEntity {
            type_meta: EntityTypeMeta {
                type_name: None,
                class_name: "PyDivEntity".to_string(),
                field_names: vec![],
                required_fields: vec![],
            },
            fields: HashMap::new(),
            constructor_values: None,
            constructor_dirty: false,
            defaults: None,
            forced_type_name: None,
            related_templates_cache: None,
            related_templates_cache_epoch: 0,
        })
    }

    #[pyo3(signature = (**kwargs))]
    fn __init__(&mut self, kwargs: Option<&Bound<'_, PyDict>>) -> PyResult<()> {
        if let Some(kw) = kwargs {
            self._set_fields(kw)?;
        }
        Ok(())
    }

    pub(crate) fn dict(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        let result = PyDict::new(py);

        if let Some(type_name) = &self.type_meta.type_name {
            result.set_item("type", type_name)?;
        }

        for (field_name, field_value) in &self.fields {
            if field_value.is_null() {
                continue;
            }
            result.set_item(field_name, json_to_py(py, &field_value.to_json())?)?;
        }

        if self.constructor_dirty {
            if let Some(constructor_values) = &self.constructor_values {
                for (field_name, constructor_value) in constructor_values {
                    let Some(current_value) = result.get_item(field_name)? else {
                        continue;
                    };
                    let dumped_raw =
                        compat_dump_bound(py, constructor_value.as_ref().bind(py).as_any())?;
                    if !dumped_raw.bind(py).eq(&current_value)? {
                        result.set_item(field_name, dumped_raw.bind(py))?;
                    }
                }
            }
        }

        if let Some(defaults) = &self.defaults {
            for (field_name, default_value) in defaults {
                if result.get_item(field_name)?.is_some() {
                    continue;
                }
                let dumped_default =
                    compat_dump_bound(py, default_value.as_ref().bind(py).as_any())?;
                if dumped_default.bind(py).is_none() {
                    continue;
                }
                result.set_item(field_name, dumped_default.bind(py))?;
            }
        }

        if let Some(type_name) = &self.forced_type_name {
            result.set_item("type", type_name)?;
        }

        Ok(result.into_any().unbind())
    }

    fn build(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        self.dict(py)
    }

    /// Return related templates collected from this entity's field tree.
    ///
    /// This mirrors `_compat_related_templates` logic in Rust and reuses
    /// template registry/cache helpers from `_pydivkit_compat`.
    fn related_templates<'py>(
        slf: &Bound<'py, Self>,
        py: Python<'py>,
    ) -> PyResult<Bound<'py, PySet>> {
        let compat = py.import("divkit_rs._pydivkit_compat")?;
        let builtins = py.import("builtins")?;
        let make_set = builtins.getattr("set")?;
        let make_frozenset = builtins.getattr("frozenset")?;

        let epoch = RELATED_TEMPLATES_CACHE_EPOCH.load(Ordering::Relaxed);
        {
            let this = slf.borrow();
            if this.related_templates_cache_epoch == epoch {
                if let Some(cached) = &this.related_templates_cache {
                    return Ok(make_set.call1((cached.as_ref().bind(py),))?.cast_into()?);
                }
            }
        }

        let collect_related_from_value = compat.getattr("_collect_related_templates_from_value")?;
        let template_dependency_closure = compat.getattr("_template_dependency_closure")?;
        let template_registry = compat
            .getattr("_TEMPLATE_REGISTRY")?
            .cast_into::<PyDict>()?;
        let related = PySet::empty(py)?;

        let cls = slf.get_type();
        let is_template = cls
            .getattr("__dk_is_template__")
            .ok()
            .and_then(|v| v.extract::<bool>().ok())
            .unwrap_or(false);
        if is_template {
            let closure = template_dependency_closure.call1((cls,))?;
            related.call_method1("update", (closure,))?;
        }

        let payload = slf.borrow().dict(py)?;
        let mut template_names = HashSet::new();
        collect_template_names_from_json_payload(payload.bind(py), &mut template_names)?;
        for template_name in template_names {
            if let Some(template_cls) = template_registry.get_item(template_name)? {
                let closure = template_dependency_closure.call1((template_cls,))?;
                related.call_method1("update", (closure,))?;
            }
        }

        let constructor_values = slf.borrow().constructor_values.clone();
        if let Some(values) = constructor_values {
            let constructor_related = PySet::empty(py)?;
            for value in values.values() {
                collect_related_from_value
                    .call1((value.as_ref().bind(py), &constructor_related))?;
            }
            for template_cls in constructor_related.iter() {
                let closure = template_dependency_closure.call1((template_cls,))?;
                related.call_method1("update", (closure,))?;
            }
        }

        let frozen = make_frozenset.call1((&related,))?;
        {
            let mut this = slf.borrow_mut();
            this.related_templates_cache = Some(Arc::new(frozen.clone().unbind()));
            this.related_templates_cache_epoch = epoch;
        }
        Ok(make_set.call1((frozen,))?.cast_into()?)
    }

    #[pyo3(signature = (exclude_fields=None))]
    fn schema(&self, py: Python<'_>, exclude_fields: Option<Vec<String>>) -> PyResult<Py<PyAny>> {
        let entity = self.to_rust_entity();
        let exclude_refs: Option<Vec<&str>> = exclude_fields
            .as_ref()
            .map(|v| v.iter().map(|s| s.as_str()).collect());
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
        self.constructor_dirty = true;
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
        if let Some(constructor_values) = self.constructor_values.as_mut() {
            constructor_values.remove(&name);
        }
        self.fields.insert(name, dv);
        Ok(())
    }

    /// Internal: configure type metadata from pre-populated cache.
    ///
    /// The cache is populated at class registration time by `register_entity_class`.
    /// This avoids per-instance Vec<String> allocation from Python→Rust conversion.
    #[pyo3(signature = (class_name))]
    fn _configure(&mut self, class_name: &str) {
        let guard = TYPE_META_CACHE.lock().unwrap();
        if let Some(cache) = guard.as_ref() {
            if let Some(cached) = cache.get(class_name) {
                self.type_meta = cached.clone();
                self.forced_type_name = self
                    .type_meta
                    .type_name
                    .as_ref()
                    .filter(|type_name| type_name.as_str() == class_name)
                    .cloned();
                self.constructor_dirty = false;
                self.related_templates_cache = None;
                self.related_templates_cache_epoch = 0;
                return;
            }
        }
        drop(guard);
        // Fallback for user-defined subclasses not in the registry
        self.type_meta.class_name = class_name.to_string();
        self.forced_type_name = None;
        self.constructor_dirty = false;
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
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
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
        Ok(())
    }

    #[pyo3(signature = (values))]
    fn _set_constructor_values(&mut self, values: &Bound<'_, PyAny>) -> PyResult<()> {
        self.constructor_values = py_mapping_to_pyany_map(values)?;
        self.constructor_dirty = false;
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
        Ok(())
    }

    #[pyo3(signature = (name, value))]
    fn _set_constructor_value(&mut self, name: String, value: &Bound<'_, PyAny>) {
        let constructor_values = self.constructor_values.get_or_insert_with(HashMap::new);
        constructor_values.insert(name, Arc::new(value.clone().unbind()));
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
    }

    #[pyo3(signature = (name))]
    fn _get_constructor_value(&self, py: Python<'_>, name: &str) -> PyResult<Py<PyAny>> {
        if let Some(constructor_values) = &self.constructor_values {
            if let Some(value) = constructor_values.get(name) {
                return Ok(value.as_ref().clone_ref(py));
            }
        }
        Ok(py.None())
    }

    fn _get_constructor_values(&self, py: Python<'_>) -> PyResult<Py<PyAny>> {
        let out = PyDict::new(py);
        if let Some(constructor_values) = &self.constructor_values {
            for (k, v) in constructor_values {
                out.set_item(k, v.as_ref().bind(py))?;
            }
        }
        Ok(out.into_any().unbind())
    }

    #[pyo3(signature = (defaults=None))]
    fn _set_defaults(&mut self, defaults: Option<&Bound<'_, PyAny>>) -> PyResult<()> {
        self.defaults = match defaults {
            Some(value) => py_mapping_to_pyany_map(value)?,
            None => None,
        };
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
        Ok(())
    }

    fn _mark_constructor_dirty(&mut self) {
        self.constructor_dirty = true;
        self.related_templates_cache = None;
        self.related_templates_cache_epoch = 0;
    }

    #[staticmethod]
    fn _bump_related_templates_cache_epoch() {
        RELATED_TEMPLATES_CACHE_EPOCH.fetch_add(1, Ordering::Relaxed);
    }
}

fn py_dict_to_pyany_map(values: &Bound<'_, PyDict>) -> PyResult<HashMap<String, Arc<Py<PyAny>>>> {
    let mut out = HashMap::with_capacity(values.len());
    for (k, v) in values.iter() {
        let key: String = k.extract()?;
        if v.is_none() {
            continue;
        }
        out.insert(key, Arc::new(v.clone().unbind()));
    }
    Ok(out)
}

fn py_mapping_to_pyany_map(
    value: &Bound<'_, PyAny>,
) -> PyResult<Option<HashMap<String, Arc<Py<PyAny>>>>> {
    if value.is_none() {
        return Ok(None);
    }
    if let Ok(dict) = value.cast::<PyDict>() {
        return Ok(Some(py_dict_to_pyany_map(dict)?));
    }
    if let Ok(items) = value.call_method0("items") {
        if let Ok(dict) = PyDict::from_sequence(&items) {
            return Ok(Some(py_dict_to_pyany_map(&dict)?));
        }
    }
    Ok(None)
}

fn collect_template_names_from_json_payload(
    value: &Bound<'_, PyAny>,
    out: &mut HashSet<String>,
) -> PyResult<()> {
    if let Ok(dict) = value.cast::<PyDict>() {
        if let Some(type_name) = dict.get_item("type")? {
            if let Ok(type_name) = type_name.extract::<String>() {
                out.insert(type_name);
            }
        }
        for (_, item) in dict.iter() {
            collect_template_names_from_json_payload(&item, out)?;
        }
        return Ok(());
    }

    if let Ok(list) = value.cast::<PyList>() {
        for item in list.iter() {
            collect_template_names_from_json_payload(&item, out)?;
        }
    }
    Ok(())
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

    // Pre-populate the type_meta cache for this class.
    // Use "divkit_rs._native.ClassName" as key to match cls.__module__ + cls.__name__.
    cache_type_meta(
        format!("divkit_rs._native.{}", class_name),
        type_name.map(|s| s.to_string()),
        field_names.iter().map(|s| s.to_string()).collect(),
        required_fields.iter().map(|s| s.to_string()).collect(),
    );

    // Create a real Python class that inherits from PyDivEntity.
    //
    // Design for subclassability:
    //  - _type_name, _field_names, _required_fields are class attrs so
    //    subclasses can introspect them.
    //  - __init__ uses fast _configure(class_name) which looks up pre-cached
    //    type metadata instead of converting Python lists to Rust Vecs on
    //    every instance.
    //  - __init__ caches class-level field defaults from the MRO once per
    //    class, merges with explicit kwargs (kwargs win), then calls
    //    _set_fields.
    let code = format!(
        concat!(
            "class {cls}(_Base):\n",
            "    __module__ = 'divkit_rs._native'\n",
            "    _type_name = {tn}\n",
            "    _field_names = {fns}\n",
            "    _required_fields = {rfs}\n",
            "    _cached_defaults = None\n",
            "    def __init__(self, **kwargs):\n",
            "        cls = type(self)\n",
            "        self._configure(f'{{cls.__module__}}.{{cls.__name__}}')\n",
            "        cached = cls.__dict__.get('_cached_defaults')\n",
            "        if cached is None:\n",
            "            defaults = {{}}\n",
            "            field_set = set(cls._field_names)\n",
            "            for klass in reversed(cls.__mro__):\n",
            "                for key, val in vars(klass).items():\n",
            "                    if key in field_set:\n",
            "                        defaults[key] = val\n",
            "            cls._cached_defaults = defaults\n",
            "            cached = defaults\n",
            "        merged = dict(cached)\n",
            "        merged.update(kwargs)\n",
            "        dk_defaults = cls.__dict__.get('__dk_defaults__')\n",
            "        if dk_defaults:\n",
            "            self._set_defaults(dk_defaults)\n",
            "        self._set_fields(merged)\n",
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

    let cls = ns.get_item(class_name)?.ok_or_else(|| {
        pyo3::exceptions::PyRuntimeError::new_err(format!("Failed to create class {}", class_name))
    })?;

    module.setattr(class_name, &cls)?;
    Ok(())
}

/// Cache type metadata for a class (called from register_entity_class and Python compat layer).
pub fn cache_type_meta(
    class_name: String,
    type_name: Option<String>,
    field_names: Vec<String>,
    required_fields: Vec<String>,
) {
    let meta = EntityTypeMeta {
        type_name,
        class_name: class_name.clone(),
        field_names,
        required_fields,
    };
    let mut guard = TYPE_META_CACHE.lock().unwrap();
    let cache = guard.get_or_insert_with(HashMap::new);
    cache.insert(class_name, meta);
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
