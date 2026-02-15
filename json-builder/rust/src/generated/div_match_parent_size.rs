// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element size adjusts to a parent element.
    pub struct DivMatchParentSize {
        type_name: "match_parent",
        fields: {
            max_size: DivSizeUnitValue => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivSizeUnitValue".into()), description: Some("Maximum size of an element."), default_value: None, constraints: Constraints::default() },
            min_size: DivSizeUnitValue => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivSizeUnitValue".into()), description: Some("Minimum size of an element."), default_value: None, constraints: Constraints::default() },
            weight: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Weight when distributing free space between elements with the size type `match_parent` inside an element. If the weight isn't specified, the elements will divide the place equally."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
