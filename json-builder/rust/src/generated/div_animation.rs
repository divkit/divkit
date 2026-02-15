// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Element animation parameters.
    pub struct DivAnimation {
        fields: {
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Animation duration in milliseconds."), default_value: Some(serde_json::json!(300)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            end_value: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Final value of an animation."), default_value: None, constraints: Constraints::default() },
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Animation speed nature. When the value is set to `spring` — animation of damping fluctuations cut to 0.7 with the `damping=1` parameter. Other options correspond to the Bezier curve:<li>`linear` — cubic-bezier(0, 0, 1, 1);</li><li>`ease` — cubic-bezier(0.25, 0.1, 0.25, 1);</li><li>`ease_in` — cubic-bezier(0.42, 0, 1, 1);</li><li>`ease_out` — cubic-bezier(0, 0, 0.58, 1);</li><li>`ease_in_out` — cubic-bezier(0.42, 0, 0.58, 1).</li>"), default_value: Some(serde_json::json!("spring")), constraints: Constraints::default() },
            items: Vec<DivAnimation> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAnimation".into()))), description: Some("Animation elements."), default_value: None, constraints: Constraints::default() },
            #[required]
            name: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["fade".into(), "translate".into(), "scale".into(), "native".into(), "set".into(), "no_animation".into()]), SchemaFieldType::Expr]), description: Some("Animation type."), default_value: None, constraints: Constraints::default() },
            repeat: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivInfinityCount".into()), SchemaFieldType::Ref("DivFixedCount".into())]), description: Some("Number of animation repetitions."), default_value: Some(serde_json::json!("{ \"type\": \"infinity\" }")), constraints: Constraints::default() },
            start_delay: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Delay in milliseconds before animation starts."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            start_value: f64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Number, SchemaFieldType::Expr]), description: Some("Starting value of an animation."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Animation type.
    pub enum DivAnimationName {
        Fade => "fade",
        Translate => "translate",
        Scale => "scale",
        Native => "native",
        Set => "set",
        NoAnimation => "no_animation",
    }
}
