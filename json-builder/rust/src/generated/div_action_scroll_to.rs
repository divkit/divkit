// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Scrolls to a position or switches to the container element specified by the
    /// `destination` parameter.
    pub struct DivActionScrollTo {
        type_name: "scroll_to",
        fields: {
            animated: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Enables scrolling animation."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
            #[required]
            destination: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("OffsetDestination".into()), SchemaFieldType::Ref("IndexDestination".into()), SchemaFieldType::Ref("StartDestination".into()), SchemaFieldType::Ref("EndDestination".into())]), description: Some("Defines the scrolling end position:<li>`index`: Scroll to the element with the index provided in `value`</li><li>`offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;</li><li>`start`: Scroll to the container start;</li><li>`end`: Scroll to the container end.</li>"), default_value: None, constraints: Constraints::default() },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("ID of the element where the action should be performed."), default_value: None, constraints: Constraints::default() },
        }
    }
}
