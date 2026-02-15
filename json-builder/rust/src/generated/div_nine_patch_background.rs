// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    /// Image in 9-patch format (https://developer.android.com/studio/write/draw9patch).
    pub struct DivNinePatchBackground {
        type_name: "nine_patch_image",
        fields: {
            #[required]
            image_url: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("Image URL."), default_value: None, constraints: Constraints { format: Some("uri".into()), ..Constraints::default() } },
            #[required]
            insets: DivAbsoluteEdgeInsets => FieldSchemaInfo { schema_type: SchemaFieldType::Ref("DivAbsoluteEdgeInsets".into()), description: Some("Margins that break the image into parts using the same rules as the CSS `border-image-slice` property (https://developer.mozilla.org/en-US/docs/Web/CSS/border-image-slice)."), default_value: None, constraints: Constraints::default() },
        }
    }
}
