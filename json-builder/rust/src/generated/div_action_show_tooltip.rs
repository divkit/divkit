// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Shows the tooltip.
    pub struct DivActionShowTooltip {
        type_name: "show_tooltip",
        fields: {
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Tooltip ID."), default_value: None, constraints: Constraints::default() },
            multiple: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Sets whether the tooltip can be shown again after it’s closed."), default_value: None, constraints: Constraints::default() },
        }
    }
}
