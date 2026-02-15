// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivAnimatorBase {
        fields: {
            cancel_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions performed when the animation is canceled. For example, when a command with the 'animator_stop' type is received."), default_value: None, constraints: Constraints::default() },
            direction: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["normal".into(), "reverse".into(), "alternate".into(), "alternate_reverse".into()]), SchemaFieldType::Expr]), description: Some("Animation direction. Determines whether the animation should be played forward, backward, or alternate between forward and backward."), default_value: Some(serde_json::json!("normal")), constraints: Constraints::default() },
            #[required]
            duration: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Animation duration in milliseconds."), default_value: None, constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            end_actions: Vec<DivAction> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivAction".into()))), description: Some("Actions when the animation is completed."), default_value: None, constraints: Constraints::default() },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Animator ID."), default_value: None, constraints: Constraints::default() },
            interpolator: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["linear".into(), "ease".into(), "ease_in".into(), "ease_out".into(), "ease_in_out".into(), "spring".into()]), SchemaFieldType::Expr]), description: Some("Animated value interpolation function."), default_value: Some(serde_json::json!("linear")), constraints: Constraints::default() },
            repeat_count: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivInfinityCount".into()), SchemaFieldType::Ref("DivFixedCount".into())]), description: Some("Number of times the animation will repeat before stopping. A value of `0` enables infinite looping."), default_value: Some(serde_json::json!("{\"type\": \"fixed\", \"value\": 1}")), constraints: Constraints::default() },
            start_delay: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Delay before the animation is launched in milliseconds."), default_value: Some(serde_json::json!(0)), constraints: Constraints { minimum: Some(0.0), ..Constraints::default() } },
            #[required]
            variable_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name of the variable being animated."), default_value: None, constraints: Constraints::default() },
        }
    }
}
