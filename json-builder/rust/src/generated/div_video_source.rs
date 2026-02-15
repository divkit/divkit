// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivVideoSource {
        type_name: "video_source",
        fields: {
            bitrate: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Media file bitrate: Data transfer rate in a video stream, measured in kilobits per second (kbps)."), default_value: None, constraints: Constraints::default() },
            #[required]
            mime_type: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("MIME type (Multipurpose Internet Mail Extensions): A string that defines the file type and helps process it correctly."), default_value: None, constraints: Constraints::default() },
            resolution: DivVideoSourceResolution => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivVideoSourceResolution".into()), description: Some("Media file resolution."), default_value: None, constraints: Constraints::default() },
            #[required]
            url: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Link to the media file available for playback or download."), default_value: None, constraints: Constraints { format: Some("uri".into()), ..Constraints::default() } },
        }
    }
}

div_entity! {
    /// Media file resolution.
    pub struct DivVideoSourceResolution {
        type_name: "resolution",
        fields: {
            #[required]
            height: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Media file frame height."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
            #[required]
            width: i64 => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Integer, SchemaFieldType::Expr]), description: Some("Media file frame width."), default_value: None, constraints: Constraints { exclusive_minimum: Some(0.0), ..Constraints::default() } },
        }
    }
}
