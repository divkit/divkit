// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Requests focus for an element. May require a user action on the web.
    pub struct DivActionFocusElement {
        type_name: "focus_element",
        fields: {
            #[required]
            element_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
        }
    }
}
