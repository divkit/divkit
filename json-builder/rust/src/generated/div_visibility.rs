// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_enum! {
    /// Element visibility:`visible` — the element is visible;`invisible` — the element
    /// is invisible, but a place is reserved for it;`gone` — the element is invisible, a
    /// place isn't reserved.
    pub enum DivVisibility {
        Visible => "visible",
        Invisible => "invisible",
        Gone => "gone",
    }
}
