/// Macro to define a DivKit entity with typed fields.
///
/// This generates a struct that implements `Entity` with `dict()`, `build()`,
/// and `schema()` methods, mirroring the Python `BaseEntity`/`BaseDiv` pattern.
///
/// # Examples
///
/// ```
/// use divkit_json_builder::*;
///
/// // Simple entity with a type field
/// div_entity! {
///     pub struct DivFixedSize {
///         type_name: "fixed",
///         fields: {
///             #[required]
///             value: i64,
///             unit: Option<String>,
///         }
///     }
/// }
///
/// let size = DivFixedSize::new().value(32).build_entity();
/// assert_eq!(size.dict(), serde_json::json!({"type": "fixed", "value": 32}));
/// ```
#[macro_export]
macro_rules! div_entity {
    (
        $(#[$meta:meta])*
        pub struct $name:ident {
            type_name: $type_val:expr,
            fields: {
                $(
                    $(#[$field_meta:ident])*
                    $field_name:ident : $field_type:ty $(=> $schema_info:expr)?
                ),* $(,)?
            }
        }
    ) => {
        $(#[$meta])*
        #[derive(Debug, Clone)]
        pub struct $name {
            $(pub $field_name: Option<$crate::value::DivValue>,)*
        }

        impl $name {
            pub fn new() -> Self {
                $name {
                    $($field_name: None,)*
                }
            }

            // Generate builder methods for each field
            $(
                #[allow(clippy::should_implement_trait)]
                pub fn $field_name<V: Into<$crate::value::DivValue>>(mut self, v: V) -> Self {
                    self.$field_name = Some(v.into());
                    self
                }
            )*

            pub fn build_entity(self) -> Self {
                self
            }
        }

        impl Default for $name {
            fn default() -> Self {
                Self::new()
            }
        }

        impl $crate::entity::Entity for $name {
            fn type_name(&self) -> Option<&str> {
                Some($type_val)
            }

            fn field_descriptors(&self) -> Vec<$crate::field::FieldDescriptor> {
                #[allow(unused_mut)]
                let mut descs = vec![
                    $crate::field::FieldDescriptor::new("type")
                        .with_default(serde_json::Value::String($type_val.to_string()))
                        .required(),
                ];
                $(
                    descs.push({
                        #[allow(unused_mut)]
                        let mut d = $crate::field::FieldDescriptor::new(stringify!($field_name));
                        $( div_entity!(@apply_meta d, $field_meta); )*
                        $(
                            let info: $crate::field::FieldSchemaInfo = $schema_info;
                            d.schema_type = Some(info.schema_type);
                            if let Some(s) = info.description { d.description = Some(s.to_string()); }
                            if let Some(v) = info.default_value { d.default = Some(v); }
                            if !info.constraints.is_empty() { d.constraints = info.constraints; }
                        )?
                        d
                    });
                )*
                descs
            }

            fn field_values(&self) -> Vec<(String, $crate::value::DivValue)> {
                #[allow(unused_mut)]
                let mut values = vec![
                    (
                        "type".to_string(),
                        $crate::value::DivValue::String($type_val.to_string()),
                    ),
                ];
                $(
                    if let Some(ref v) = self.$field_name {
                        values.push((stringify!($field_name).to_string(), v.clone()));
                    }
                )*
                values
            }
        }

        impl From<$name> for $crate::value::DivValue {
            fn from(v: $name) -> $crate::value::DivValue {
                $crate::value::DivValue::Entity(Box::new(v))
            }
        }
    };

    // Entity without a type field (like DivEdgeInsets, DivBorder)
    (
        $(#[$meta:meta])*
        pub struct $name:ident {
            fields: {
                $(
                    $(#[$field_meta:ident])*
                    $field_name:ident : $field_type:ty $(=> $schema_info:expr)?
                ),* $(,)?
            }
        }
    ) => {
        $(#[$meta])*
        #[derive(Debug, Clone)]
        pub struct $name {
            $(pub $field_name: Option<$crate::value::DivValue>,)*
        }

        impl $name {
            pub fn new() -> Self {
                $name {
                    $($field_name: None,)*
                }
            }

            $(
                #[allow(clippy::should_implement_trait)]
                pub fn $field_name<V: Into<$crate::value::DivValue>>(mut self, v: V) -> Self {
                    self.$field_name = Some(v.into());
                    self
                }
            )*

            pub fn build_entity(self) -> Self {
                self
            }
        }

        impl Default for $name {
            fn default() -> Self {
                Self::new()
            }
        }

        impl $crate::entity::Entity for $name {
            fn field_descriptors(&self) -> Vec<$crate::field::FieldDescriptor> {
                let mut descs = Vec::new();
                $(
                    descs.push({
                        #[allow(unused_mut)]
                        let mut d = $crate::field::FieldDescriptor::new(stringify!($field_name));
                        $( div_entity!(@apply_meta d, $field_meta); )*
                        $(
                            let info: $crate::field::FieldSchemaInfo = $schema_info;
                            d.schema_type = Some(info.schema_type);
                            if let Some(s) = info.description { d.description = Some(s.to_string()); }
                            if let Some(v) = info.default_value { d.default = Some(v); }
                            if !info.constraints.is_empty() { d.constraints = info.constraints; }
                        )?
                        d
                    });
                )*
                descs
            }

            fn field_values(&self) -> Vec<(String, $crate::value::DivValue)> {
                let mut values = Vec::new();
                $(
                    if let Some(ref v) = self.$field_name {
                        values.push((stringify!($field_name).to_string(), v.clone()));
                    }
                )*
                values
            }
        }

        impl From<$name> for $crate::value::DivValue {
            fn from(v: $name) -> $crate::value::DivValue {
                $crate::value::DivValue::Entity(Box::new(v))
            }
        }
    };

    // Helper for applying field metadata attributes
    (@apply_meta $d:ident, required) => {
        $d.required = true;
    };
}

/// Macro to define an enum type (mirrors Python enum types like DivAlignmentVertical).
#[macro_export]
macro_rules! div_enum {
    (
        $(#[$meta:meta])*
        pub enum $name:ident {
            $(
                $variant:ident => $value:expr
            ),* $(,)?
        }
    ) => {
        $(#[$meta])*
        #[derive(Debug, Clone, Copy, PartialEq, Eq, Hash)]
        pub enum $name {
            $($variant,)*
        }

        impl $name {
            pub fn value(&self) -> &'static str {
                match self {
                    $(Self::$variant => $value,)*
                }
            }

            pub fn variants() -> &'static [&'static str] {
                &[$($value),*]
            }
        }

        impl std::fmt::Display for $name {
            fn fmt(&self, f: &mut std::fmt::Formatter<'_>) -> std::fmt::Result {
                f.write_str(self.value())
            }
        }

        impl From<$name> for $crate::value::DivValue {
            fn from(v: $name) -> $crate::value::DivValue {
                $crate::value::DivValue::Enum(v.value().to_string())
            }
        }
    };
}
