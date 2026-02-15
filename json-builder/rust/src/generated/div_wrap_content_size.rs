// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// The size of an element adjusts to its contents.
    pub struct DivWrapContentSize {
        type_name: "wrap_content",
        fields: {
            constrained: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("The final size mustn't exceed the parent one. On iOS and in a default browser `false`. On Android always `true`."), default_value: None, constraints: Constraints::default() },
            max_size: DivSizeUnitValue => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivSizeUnitValue".into()), description: Some("Maximum size of an element."), default_value: None, constraints: Constraints::default() },
            min_size: DivSizeUnitValue => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivSizeUnitValue".into()), description: Some("Minimum size of an element."), default_value: None, constraints: Constraints::default() },
        }
    }
}
