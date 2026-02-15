use std::fmt;

use serde_json::Value;

/// Expression wrapper: must be of the form `@{...}`.
/// Mirrors Python's `Expr` class.
#[derive(Debug, Clone, PartialEq, Eq, Hash)]
pub struct Expr(String);

impl Expr {
    pub fn new(v: &str) -> Result<Self, String> {
        if v.starts_with("@{") && v.ends_with('}') {
            Ok(Expr(v.to_string()))
        } else {
            Err(format!("failed to initiate Expr with {v}"))
        }
    }

    pub fn value(&self) -> &str {
        &self.0
    }
}

impl fmt::Display for Expr {
    fn fmt(&self, f: &mut fmt::Formatter<'_>) -> fmt::Result {
        f.write_str(&self.0)
    }
}

impl From<Expr> for Value {
    fn from(e: Expr) -> Value {
        Value::String(e.0)
    }
}

impl From<&Expr> for Value {
    fn from(e: &Expr) -> Value {
        Value::String(e.0.clone())
    }
}
