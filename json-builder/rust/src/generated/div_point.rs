// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A point with fixed coordinates.
    pub struct DivPoint {
        fields: {
            #[required]
            x: DivDimension => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivDimension".into()), description: Some("`X` coordinate."), default_value: None, constraints: Constraints::default() },
            #[required]
            y: DivDimension => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivDimension".into()), description: Some("`Y` coordinate."), default_value: None, constraints: Constraints::default() },
        }
    }
}
