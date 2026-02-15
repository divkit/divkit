use serde_json::Value;

use crate::field::FieldDescriptor;
use crate::schema::SchemaGenerator;
use crate::value::DivValue;

/// Core trait for all entities (mirrors Python's `BaseEntity`).
///
/// Every entity type implements this trait to provide:
/// - `dict()` / `build()`: serialize to JSON value (same as Python's `.dict()`)
/// - `schema()`: generate JSON Schema
/// - Field metadata access
pub trait Entity: std::fmt::Debug + EntityClone + Send + Sync {
    /// The type name (e.g., "text", "container"). None for non-typed entities.
    fn type_name(&self) -> Option<&str> {
        None
    }

    /// Get field descriptors for this entity.
    fn field_descriptors(&self) -> Vec<FieldDescriptor>;

    /// Get field values as (field_name, value) pairs.
    /// Only includes non-null fields.
    fn field_values(&self) -> Vec<(String, DivValue)>;

    /// Serialize this entity to a JSON Value (dict/object).
    /// Mirrors Python's `BaseEntity.dict()`.
    fn dict(&self) -> Value {
        let mut map = serde_json::Map::new();
        for (name, value) in self.field_values() {
            if !value.is_null() {
                map.insert(name, value.to_json());
            }
        }
        Value::Object(map)
    }

    /// Alias for `dict()` — build this entity into JSON.
    fn build(&self) -> Value {
        self.dict()
    }

    /// Generate JSON Schema for this entity type.
    /// Mirrors Python's `BaseDiv.schema(exclude_fields=...)`.
    fn schema(&self, exclude_fields: Option<&[&str]>) -> Value {
        let descriptors = self.field_descriptors();
        let mut gen = SchemaGenerator::new();
        gen.build_entity_schema(&descriptors, exclude_fields)
    }

    /// Collect related templates (for BaseDiv-like types).
    fn related_templates(&self) -> Vec<Box<dyn Template>> {
        vec![]
    }
}

/// Trait for template support (mirrors Python's `BaseDiv`).
pub trait Template: Entity {
    /// The base type this template extends (e.g., "container", "text").
    fn base_type(&self) -> Option<&str>;

    /// The template name (unique identifier).
    fn template_name(&self) -> &str;

    /// Build the template definition.
    fn template(&self) -> Value;
}

/// Helper trait for cloning boxed entities.
pub trait EntityClone {
    fn clone_box(&self) -> Box<dyn Entity>;
}

impl<T: Entity + Clone + 'static> EntityClone for T {
    fn clone_box(&self) -> Box<dyn Entity> {
        Box::new(self.clone())
    }
}

impl Clone for Box<dyn Entity> {
    fn clone(&self) -> Self {
        self.clone_box()
    }
}

/// Wrap any Entity implementor into a DivValue.
pub fn entity_to_value<E: Entity + 'static>(e: E) -> DivValue {
    DivValue::Entity(Box::new(e))
}

/// Build a complete card with templates and card data.
/// Mirrors Python's `make_div()`.
pub fn make_div<E: Entity + ?Sized>(div: &E) -> Value {
    let mut templates = serde_json::Map::new();
    for tpl in div.related_templates() {
        templates.insert(tpl.template_name().to_string(), tpl.template());
    }

    let card = make_card("card", div);

    let mut result = serde_json::Map::new();
    result.insert("templates".to_string(), Value::Object(templates));
    result.insert("card".to_string(), card.dict());
    Value::Object(result)
}

/// Build a DivData card wrapper.
/// Mirrors Python's `make_card()`.
pub fn make_card<E: Entity + ?Sized>(log_id: &str, div: &E) -> DivData {
    let boxed: Box<dyn Entity> = div.clone_box();
    DivData {
        log_id: log_id.to_string(),
        states: vec![DivDataState {
            state_id: 0,
            div: boxed,
        }],
    }
}

/// DivData — top-level card data.
/// Mirrors Python's `DivData`.
#[derive(Debug, Clone)]
pub struct DivData {
    pub log_id: String,
    pub states: Vec<DivDataState>,
}

impl Entity for DivData {
    fn field_descriptors(&self) -> Vec<FieldDescriptor> {
        vec![
            FieldDescriptor::new("log_id").required(),
            FieldDescriptor::new("states").required(),
        ]
    }

    fn field_values(&self) -> Vec<(String, DivValue)> {
        vec![
            ("log_id".into(), DivValue::String(self.log_id.clone())),
            (
                "states".into(),
                DivValue::Array(
                    self.states.iter().map(|s| s.to_div_value()).collect(),
                ),
            ),
        ]
    }
}

/// DivDataState — a single state in a card.
#[derive(Debug, Clone)]
pub struct DivDataState {
    pub state_id: i64,
    pub div: Box<dyn Entity>,
}

impl DivDataState {
    pub fn to_div_value(&self) -> DivValue {
        let mut entries = vec![
            ("div".to_string(), DivValue::Entity(self.div.clone())),
            ("state_id".to_string(), DivValue::Int(self.state_id)),
        ];
        // Sort for consistent output
        entries.sort_by(|a, b| a.0.cmp(&b.0));
        DivValue::Map(entries)
    }
}

/// Helper to collect related templates from a DivValue tree.
pub fn collect_templates(value: &DivValue) -> Vec<Box<dyn Template>> {
    match value {
        DivValue::Entity(e) => e.related_templates(),
        DivValue::Array(arr) => {
            let mut result = Vec::new();
            for v in arr {
                result.extend(collect_templates(v));
            }
            result
        }
        _ => vec![],
    }
}
