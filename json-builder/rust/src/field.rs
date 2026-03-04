use std::collections::HashMap;

use serde_json::Value;

/// Schema type information for fields.
#[derive(Debug, Clone)]
pub enum SchemaFieldType {
    Integer,
    Number,
    Boolean,
    String,
    Expr,
    Array(Box<SchemaFieldType>),
    Object,
    Enum(Vec<std::string::String>),
    AnyOf(Vec<SchemaFieldType>),
    Ref(std::string::String),
    Any,
}

impl SchemaFieldType {
    pub fn to_json(&self, definitions: &mut HashMap<std::string::String, Value>) -> Value {
        match self {
            SchemaFieldType::Integer => serde_json::json!({"type": "integer"}),
            SchemaFieldType::Number => serde_json::json!({"type": "number"}),
            SchemaFieldType::Boolean => serde_json::json!({"type": "boolean"}),
            SchemaFieldType::String => serde_json::json!({"type": "string"}),
            SchemaFieldType::Expr => {
                serde_json::json!({"type": "string", "pattern": "^@\\{.*\\}$"})
            }
            SchemaFieldType::Array(item_type) => {
                serde_json::json!({
                    "type": "array",
                    "items": item_type.to_json(definitions)
                })
            }
            SchemaFieldType::Object => {
                serde_json::json!({
                    "type": "object",
                    "additionalProperties": true
                })
            }
            SchemaFieldType::Enum(values) => {
                serde_json::json!({
                    "type": "string",
                    "enum": values
                })
            }
            SchemaFieldType::AnyOf(types) => {
                let any_of: Vec<Value> = types.iter().map(|t| t.to_json(definitions)).collect();
                serde_json::json!({"anyOf": any_of})
            }
            SchemaFieldType::Ref(name) => {
                // Resolve the reference: add to definitions if not already present.
                // A Null placeholder is inserted first to prevent infinite recursion
                // on circular references (e.g. DivContainer -> DivContainer).
                if !definitions.contains_key(name.as_str()) {
                    definitions.insert(name.clone(), Value::Null);

                    if let Some(descriptors) =
                        crate::generated::schema_registry::entity_field_descriptors(name)
                    {
                        // Build the entity schema, sharing our definitions map
                        // to avoid infinite recursion on circular refs.
                        let entity_schema = Self::build_ref_schema(&descriptors, definitions);
                        definitions.insert(name.clone(), entity_schema);
                    }
                }
                serde_json::json!({"$ref": format!("#/definitions/{}", name)})
            }
            SchemaFieldType::Any => serde_json::json!({}),
        }
    }

    /// Build a JSON schema for a referenced entity, sharing the definitions map
    /// to avoid infinite recursion on circular references.
    fn build_ref_schema(
        descriptors: &[FieldDescriptor],
        definitions: &mut HashMap<std::string::String, Value>,
    ) -> Value {
        let mut properties = serde_json::Map::new();
        let mut required = Vec::new();

        for desc in descriptors {
            let mut field_schema = if desc.name == "type" {
                if let Some(ref default) = desc.default {
                    serde_json::json!({"type": "string", "enum": [default]})
                } else {
                    serde_json::json!({"type": "string"})
                }
            } else if let Some(ref schema_type) = desc.schema_type {
                schema_type.to_json(definitions)
            } else {
                serde_json::json!({"type": "string"})
            };

            if let Some(ref description) = desc.description {
                if let Some(m) = field_schema.as_object_mut() {
                    m.insert(
                        "description".to_string(),
                        Value::String(description.clone()),
                    );
                }
            }
            if let Some(ref default) = desc.default {
                if let Some(m) = field_schema.as_object_mut() {
                    m.insert("default".to_string(), default.clone());
                }
            }
            for (k, v) in desc.constraints.to_json_map() {
                if let Some(m) = field_schema.as_object_mut() {
                    m.insert(k, v);
                }
            }

            properties.insert(desc.name.clone(), field_schema);
            if desc.required {
                required.push(Value::String(desc.name.clone()));
            }
        }

        let mut schema = serde_json::Map::new();
        schema.insert("type".to_string(), Value::String("object".to_string()));
        if !properties.is_empty() {
            schema.insert("properties".to_string(), Value::Object(properties));
        }
        if !required.is_empty() {
            schema.insert("required".to_string(), Value::Array(required));
        }
        Value::Object(schema)
    }
}

/// Rich schema info for a field, produced by the code generator.
#[derive(Debug, Clone)]
pub struct FieldSchemaInfo {
    pub schema_type: SchemaFieldType,
    pub description: Option<&'static str>,
    pub default_value: Option<Value>,
    pub constraints: Constraints,
}

/// Constraints on a field, mirroring Python's Field constraints.
#[derive(Debug, Clone, Default)]
pub struct Constraints {
    pub format: Option<String>,
    pub exclusive_minimum: Option<f64>,
    pub minimum: Option<f64>,
    pub exclusive_maximum: Option<f64>,
    pub maximum: Option<f64>,
    pub multiple_of: Option<f64>,
    pub min_items: Option<usize>,
    pub max_items: Option<usize>,
    pub unique_items: Option<bool>,
    pub min_length: Option<usize>,
    pub max_length: Option<usize>,
    pub regex: Option<String>,
}

impl Constraints {
    pub fn to_json_map(&self) -> HashMap<String, Value> {
        let mut m = HashMap::new();
        if let Some(ref f) = self.format {
            m.insert("format".into(), Value::String(f.clone()));
        }
        if let Some(v) = self.exclusive_minimum {
            m.insert("exclusiveMinimum".into(), serde_json::json!(v));
        }
        if let Some(v) = self.minimum {
            m.insert("minimum".into(), serde_json::json!(v));
        }
        if let Some(v) = self.exclusive_maximum {
            m.insert("exclusiveMaximum".into(), serde_json::json!(v));
        }
        if let Some(v) = self.maximum {
            m.insert("maximum".into(), serde_json::json!(v));
        }
        if let Some(v) = self.multiple_of {
            m.insert("multipleOf".into(), serde_json::json!(v));
        }
        if let Some(v) = self.min_items {
            m.insert("minItems".into(), serde_json::json!(v));
        }
        if let Some(v) = self.max_items {
            m.insert("maxItems".into(), serde_json::json!(v));
        }
        if let Some(v) = self.unique_items {
            m.insert("uniqueItems".into(), serde_json::json!(v));
        }
        if let Some(v) = self.min_length {
            m.insert("minLength".into(), serde_json::json!(v));
        }
        if let Some(v) = self.max_length {
            m.insert("maxLength".into(), serde_json::json!(v));
        }
        if let Some(ref v) = self.regex {
            m.insert("regex".into(), Value::String(v.clone()));
        }
        m
    }

    pub fn is_empty(&self) -> bool {
        self.format.is_none()
            && self.exclusive_minimum.is_none()
            && self.minimum.is_none()
            && self.exclusive_maximum.is_none()
            && self.maximum.is_none()
            && self.multiple_of.is_none()
            && self.min_items.is_none()
            && self.max_items.is_none()
            && self.unique_items.is_none()
            && self.min_length.is_none()
            && self.max_length.is_none()
            && self.regex.is_none()
    }
}

/// Field metadata descriptor.
/// Mirrors Python's `_Field` / `Field()`.
#[derive(Debug, Clone)]
pub struct FieldDescriptor {
    pub name: String,
    pub description: Option<String>,
    pub default: Option<Value>,
    pub constraints: Constraints,
    pub required: bool,
    pub schema_type: Option<SchemaFieldType>,
}

impl FieldDescriptor {
    pub fn new(name: &str) -> Self {
        FieldDescriptor {
            name: name.to_string(),
            description: None,
            default: None,
            constraints: Constraints::default(),
            required: false,
            schema_type: None,
        }
    }

    pub fn with_schema_type(mut self, st: SchemaFieldType) -> Self {
        self.schema_type = Some(st);
        self
    }

    pub fn with_default(mut self, default: Value) -> Self {
        self.default = Some(default);
        self
    }

    pub fn with_description(mut self, desc: &str) -> Self {
        self.description = Some(desc.to_string());
        self
    }

    pub fn with_constraints(mut self, c: Constraints) -> Self {
        self.constraints = c;
        self
    }

    pub fn required(mut self) -> Self {
        self.required = true;
        self
    }
}

/// Builder for creating a FieldDescriptor fluently.
/// Mirrors Python's `Field(name=..., description=..., default=..., ...)`.
pub struct FieldBuilder {
    name: Option<String>,
    description: Option<String>,
    default: Option<Value>,
    constraints: Constraints,
}

impl FieldBuilder {
    pub fn new() -> Self {
        FieldBuilder {
            name: None,
            description: None,
            default: None,
            constraints: Constraints::default(),
        }
    }

    pub fn name(mut self, n: &str) -> Self {
        self.name = Some(n.to_string());
        self
    }

    pub fn description(mut self, d: &str) -> Self {
        self.description = Some(d.to_string());
        self
    }

    pub fn default_value(mut self, v: Value) -> Self {
        self.default = Some(v);
        self
    }

    pub fn format(mut self, f: &str) -> Self {
        self.constraints.format = Some(f.to_string());
        self
    }

    pub fn gt(mut self, v: f64) -> Self {
        self.constraints.exclusive_minimum = Some(v);
        self
    }

    pub fn ge(mut self, v: f64) -> Self {
        self.constraints.minimum = Some(v);
        self
    }

    pub fn lt(mut self, v: f64) -> Self {
        self.constraints.exclusive_maximum = Some(v);
        self
    }

    pub fn le(mut self, v: f64) -> Self {
        self.constraints.maximum = Some(v);
        self
    }

    pub fn multiple_of(mut self, v: f64) -> Self {
        self.constraints.multiple_of = Some(v);
        self
    }

    pub fn min_items(mut self, v: usize) -> Self {
        self.constraints.min_items = Some(v);
        self
    }

    pub fn max_items(mut self, v: usize) -> Self {
        self.constraints.max_items = Some(v);
        self
    }

    pub fn unique_items(mut self, v: bool) -> Self {
        self.constraints.unique_items = Some(v);
        self
    }

    pub fn min_length(mut self, v: usize) -> Self {
        self.constraints.min_length = Some(v);
        self
    }

    pub fn max_length(mut self, v: usize) -> Self {
        self.constraints.max_length = Some(v);
        self
    }

    pub fn regex(mut self, v: &str) -> Self {
        self.constraints.regex = Some(v.to_string());
        self
    }

    pub fn build(self, field_name: &str) -> FieldDescriptor {
        FieldDescriptor {
            name: self.name.unwrap_or_else(|| field_name.to_string()),
            description: self.description,
            default: self.default,
            constraints: self.constraints,
            required: false,
            schema_type: None,
        }
    }
}

impl Default for FieldBuilder {
    fn default() -> Self {
        Self::new()
    }
}
