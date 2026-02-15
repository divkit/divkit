// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element behavior when focusing or losing focus.
    pub struct DivFocus {
        fields: {
            background: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivLinearGradient".into()), SchemaFieldType::Ref("DivRadialGradient".into()), SchemaFieldType::Ref("DivImageBackground".into()), SchemaFieldType::Ref("DivSolidBackground".into()), SchemaFieldType::Ref("DivNinePatchBackground".into())]))), description: Some("Background of an element when it is in focus. It can contain multiple layers."), default_value: None, constraints: Constraints::default() },
            border: DivBorder => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivBorder".into()), description: Some("Border of an element when it's in focus."), default_value: None, constraints: Constraints::default() },
            next_focus_ids: DivFocusNextFocusIds => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFocusNextFocusIds".into()), description: Some("IDs of elements that will be next to get focus."), default_value: None, constraints: Constraints::default() },
            on_blur: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions when an element loses focus."), default_value: None, constraints: Constraints::default() },
            on_focus: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions when an element gets focus."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_entity! {
    /// IDs of elements that will be next to get focus.
    pub struct DivFocusNextFocusIds {
        fields: {
            down: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
            forward: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
            left: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
            right: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
            up: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: None, default_value: None, constraints: Constraints::default() },
        }
    }
}
