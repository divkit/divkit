// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Page width (%).
    pub struct DivPageSize {
        type_name: "percentage",
        fields: {
            #[required]
            page_width: DivPercentageSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivPercentageSize".into()), description: Some("Page width as a percentage of the parent element width."), default_value: None, constraints: Constraints::default() },
        }
    }
}
