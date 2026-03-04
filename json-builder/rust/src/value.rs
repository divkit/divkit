use serde_json::Value;

use crate::entity::Entity;
use crate::expr::Expr;

/// A dynamically-typed value that can be stored in entity fields.
/// Mirrors how Python's entities store heterogeneous values.
#[derive(Debug, Clone)]
pub enum DivValue {
    Null,
    Bool(bool),
    Int(i64),
    Float(f64),
    String(String),
    Expr(Expr),
    Enum(String),
    Entity(Box<dyn Entity>),
    Array(Vec<DivValue>),
    Map(Vec<(String, DivValue)>),
}

impl DivValue {
    pub fn is_null(&self) -> bool {
        matches!(self, DivValue::Null)
    }

    /// Convert to JSON value, matching Python's `dump()` function.
    pub fn to_json(&self) -> Value {
        match self {
            DivValue::Null => Value::Null,
            DivValue::Bool(b) => Value::Bool(*b),
            DivValue::Int(n) => Value::from(*n),
            DivValue::Float(f) => {
                // Whole-number floats serialize as integers to match DivKit spec
                if f.fract() == 0.0
                    && f.is_finite()
                    && *f >= i64::MIN as f64
                    && *f <= i64::MAX as f64
                {
                    Value::from(*f as i64)
                } else {
                    serde_json::json!(*f)
                }
            }
            DivValue::String(s) => Value::String(s.clone()),
            DivValue::Expr(e) => Value::String(e.to_string()),
            DivValue::Enum(s) => Value::String(s.clone()),
            DivValue::Entity(e) => e.dict(),
            DivValue::Array(arr) => Value::Array(arr.iter().map(|v| v.to_json()).collect()),
            DivValue::Map(entries) => {
                let map: serde_json::Map<String, Value> = entries
                    .iter()
                    .map(|(k, v)| (k.clone(), v.to_json()))
                    .collect();
                Value::Object(map)
            }
        }
    }

    /// Get the schema type string for this value's type.
    pub fn schema_type_name(&self) -> &str {
        match self {
            DivValue::Null => "null",
            DivValue::Bool(_) => "boolean",
            DivValue::Int(_) => "integer",
            DivValue::Float(_) => "number",
            DivValue::String(_) | DivValue::Expr(_) | DivValue::Enum(_) => "string",
            DivValue::Entity(_) => "object",
            DivValue::Array(_) => "array",
            DivValue::Map(_) => "object",
        }
    }
}

// Conversion traits for ergonomic construction

impl From<bool> for DivValue {
    fn from(v: bool) -> Self {
        DivValue::Bool(v)
    }
}

impl From<i32> for DivValue {
    fn from(v: i32) -> Self {
        DivValue::Int(v as i64)
    }
}

impl From<i64> for DivValue {
    fn from(v: i64) -> Self {
        DivValue::Int(v)
    }
}

impl From<u32> for DivValue {
    fn from(v: u32) -> Self {
        DivValue::Int(v as i64)
    }
}

impl From<f64> for DivValue {
    fn from(v: f64) -> Self {
        DivValue::Float(v)
    }
}

impl From<&str> for DivValue {
    fn from(v: &str) -> Self {
        DivValue::String(v.to_string())
    }
}

impl From<String> for DivValue {
    fn from(v: String) -> Self {
        DivValue::String(v)
    }
}

impl From<Expr> for DivValue {
    fn from(v: Expr) -> Self {
        DivValue::Expr(v)
    }
}

impl<T: Into<DivValue>> From<Vec<T>> for DivValue {
    fn from(v: Vec<T>) -> Self {
        DivValue::Array(v.into_iter().map(|x| x.into()).collect())
    }
}

impl<T: Into<DivValue>> From<Option<T>> for DivValue {
    fn from(v: Option<T>) -> Self {
        match v {
            Some(x) => x.into(),
            None => DivValue::Null,
        }
    }
}

impl<T: Entity + 'static> From<Box<T>> for DivValue {
    fn from(v: Box<T>) -> Self {
        DivValue::Entity(v)
    }
}
