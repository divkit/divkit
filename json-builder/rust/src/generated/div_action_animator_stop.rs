// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Stops the specified animator.
    pub struct DivActionAnimatorStop {
        type_name: "animator_stop",
        fields: {
            #[required]
            animator_id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("ID of the animator to be stopped."), default_value: None, constraints: Constraints::default() },
        }
    }
}
