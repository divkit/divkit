// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A set of animations to be applied simultaneously.
    pub struct DivAppearanceSetTransition {
        type_name: "set",
        fields: {
            #[required]
            items: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivAppearanceSetTransition".into()), SchemaFieldType::Ref("DivFadeTransition".into()), SchemaFieldType::Ref("DivScaleTransition".into()), SchemaFieldType::Ref("DivSlideTransition".into())]))), description: Some("An array of animations."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
        }
    }
}
