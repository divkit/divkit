// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_enum! {
    /// Type used for function arguments and return values.
    pub enum DivEvaluableType {
        String => "string",
        Integer => "integer",
        Number => "number",
        Boolean => "boolean",
        Datetime => "datetime",
        Color => "color",
        Url => "url",
        Dict => "dict",
        Array => "array",
    }
}
