// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// A mask to hide text (spoiler). Looks like randomly distributed particles, same as
    /// in Telegram.
    pub struct DivTextRangeMaskParticles {
        type_name: "particles",
        fields: {
            #[required]
            color: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("The color of particles on the mask."), default_value: None, constraints: Constraints { format: Some("color".into()), ..Constraints::default() } },
            density: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("The density of particles on the mask. Interpreted as the probability of a particle to appear in a given point on the mask."), default_value: Some(serde_json::json!(0.8)), constraints: Constraints { exclusive_minimum: Some(0.0), maximum: Some(1.0), ..Constraints::default() } },
            is_animated: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Enables animation for particles on the mask. The animation looks like a smooth movement of particles across the mask, same as in Telegram."), default_value: Some(serde_json::json!(false)), constraints: Constraints::default() },
            is_enabled: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("Controls the mask state. If set to `true`, the mask will hide the specified part of text. Otherwise, the text will be shown."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
            particle_size: DivFixedSize => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivFixedSize".into()), description: Some("The size of a single particle on the mask."), default_value: Some(serde_json::json!("{\"type\":\"fixed\",\"value\":1}")), constraints: Constraints::default() },
        }
    }
}
