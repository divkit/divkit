// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Relative radius of the gradient transition.
    pub struct DivRadialGradientRelativeRadius {
        type_name: "relative",
        fields: {
            #[required]
            value: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["nearest_corner".into(), "farthest_corner".into(), "nearest_side".into(), "farthest_side".into()]), SchemaFieldType::Expr]), description: Some("Type of the relative radius of the gradient transition."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Type of the relative radius of the gradient transition.
    pub enum DivRadialGradientRelativeRadiusValue {
        NearestCorner => "nearest_corner",
        FarthestCorner => "farthest_corner",
        NearestSide => "nearest_side",
        FarthestSide => "farthest_side",
    }
}
