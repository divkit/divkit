// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Manages video playback.
    pub struct DivActionVideo {
        type_name: "video",
        fields: {
            #[required]
            action: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Enum(vec!["start".into(), "pause".into()]), SchemaFieldType::Expr]), description: Some("Defines the action for the video: <li>`start` — starts playing the video if the video is ready to be played, or schedules playback</li><li>`pause' — stops the video playback</li>"), default_value: None, constraints: Constraints::default() },
            #[required]
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Video ID."), default_value: None, constraints: Constraints::default() },
        }
    }
}

div_enum! {
    /// Defines the action for the video: `start` — starts playing the video if the video
    /// is ready to be played, or schedules playback`pause' — stops the video playback
    pub enum DivActionVideoAction {
        Start => "start",
        Pause => "pause",
    }
}
