// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Fixed width value of the visible part of a neighbouring page.
    pub struct DivNeighbourPageSize {
        type_name: "fixed",
        fields: {
            #[required]
            neighbour_page_width: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("Width of the visible part of a neighbouring page."), default_value: None, constraints: Constraints::default() },
        }
    }
}
