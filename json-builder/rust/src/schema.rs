use std::collections::{HashMap, HashSet};

use serde_json::Value;

use crate::field::FieldDescriptor;

// Re-export SchemaFieldType from field.rs for backward compatibility
pub use crate::field::SchemaFieldType;

/// JSON Schema generator.
/// Mirrors the schema generation logic in Python's `BaseEntity._build_schema()`.
pub struct SchemaGenerator {
    pub definitions: HashMap<String, Value>,
}

impl SchemaGenerator {
    pub fn new() -> Self {
        SchemaGenerator {
            definitions: HashMap::new(),
        }
    }

    /// Build a JSON schema for an entity given its field descriptors.
    pub fn build_entity_schema(
        &mut self,
        descriptors: &[FieldDescriptor],
        exclude_fields: Option<&[&str]>,
    ) -> Value {
        let exclude: HashSet<&str> = exclude_fields
            .map(|fields| fields.iter().copied().collect())
            .unwrap_or_default();

        let mut properties = serde_json::Map::new();
        let mut required = Vec::new();

        for desc in descriptors {
            if exclude.contains(desc.name.as_str()) {
                continue;
            }

            let mut field_schema = self.field_to_schema(desc);

            // Add extra info from descriptor
            if let Some(ref description) = desc.description {
                field_schema.as_object_mut().map(|m| {
                    m.insert(
                        "description".to_string(),
                        Value::String(description.clone()),
                    )
                });
            }
            if let Some(ref default) = desc.default {
                field_schema
                    .as_object_mut()
                    .map(|m| m.insert("default".to_string(), default.clone()));
            }

            // Add constraints
            for (k, v) in desc.constraints.to_json_map() {
                field_schema.as_object_mut().map(|m| m.insert(k, v));
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

        if !self.definitions.is_empty() {
            let defs: serde_json::Map<String, Value> = self
                .definitions
                .iter()
                .map(|(k, v)| (k.clone(), v.clone()))
                .collect();
            schema.insert("definitions".to_string(), Value::Object(defs));
        }

        Value::Object(schema)
    }

    fn field_to_schema(&mut self, desc: &FieldDescriptor) -> Value {
        // For type fields with a fixed default, emit enum
        if desc.name == "type" {
            if let Some(ref default) = desc.default {
                return serde_json::json!({
                    "type": "string",
                    "enum": [default]
                });
            }
        }

        // Use rich schema type info when available
        if let Some(ref schema_type) = desc.schema_type {
            return schema_type.to_json(&mut self.definitions);
        }

        // Default fallback: just string type
        serde_json::json!({"type": "string"})
    }
}

impl Default for SchemaGenerator {
    fn default() -> Self {
        Self::new()
    }
}
