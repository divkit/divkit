// Generated code. Do not modify.

#[allow(unused_imports)]
use crate::{div_entity, div_enum};
#[allow(unused_imports)]
use crate::value::DivValue;
#[allow(unused_imports)]
use crate::field::{FieldSchemaInfo, Constraints, SchemaFieldType};

div_entity! {
    pub struct DivCollectionItemBuilder {
        fields: {
            #[required]
            data: Vec<DivValue> => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Array(Box::new(SchemaFieldType::Any)), SchemaFieldType::Expr]), description: Some("Data that will be used to create collection elements."), default_value: None, constraints: Constraints::default() },
            data_element_name: String => FieldSchemaInfo { schema_type: SchemaFieldType::String, description: Some("Name for accessing the next `data` element in the prototype. Working with this element is the same as with dictionaries."), default_value: Some(serde_json::json!("it")), constraints: Constraints::default() },
            #[required]
            prototypes: Vec<DivCollectionItemBuilderPrototype> => FieldSchemaInfo { schema_type: SchemaFieldType::Array(Box::new(SchemaFieldType::Ref("DivCollectionItemBuilderPrototype".into()))), description: Some("Array of `div` elements from which the collection elements will be created."), default_value: None, constraints: Constraints { min_items: Some(1), ..Constraints::default() } },
        }
    }
}

div_entity! {
    pub struct DivCollectionItemBuilderPrototype {
        fields: {
            #[required]
            div: DivValue => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Ref("DivImage".into()), SchemaFieldType::Ref("DivGifImage".into()), SchemaFieldType::Ref("DivText".into()), SchemaFieldType::Ref("DivSeparator".into()), SchemaFieldType::Ref("DivContainer".into()), SchemaFieldType::Ref("DivGrid".into()), SchemaFieldType::Ref("DivGallery".into()), SchemaFieldType::Ref("DivPager".into()), SchemaFieldType::Ref("DivTabs".into()), SchemaFieldType::Ref("DivState".into()), SchemaFieldType::Ref("DivCustom".into()), SchemaFieldType::Ref("DivIndicator".into()), SchemaFieldType::Ref("DivSlider".into()), SchemaFieldType::Ref("DivSwitch".into()), SchemaFieldType::Ref("DivInput".into()), SchemaFieldType::Ref("DivSelect".into()), SchemaFieldType::Ref("DivVideo".into())]), description: Some("`Div` from which the collection elements will be created. In `Div`, you can use expressions using data from `data`. To access the next `data` element, you need to use the same prefix as in `data_element_prefix`."), default_value: None, constraints: Constraints::default() },
            id: String => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::String, SchemaFieldType::Expr]), description: Some("`id` of the element to be created from the prototype. Unlike the `div-base.id` field, may contain expressions. Has a higher priority than `div-base.id`."), default_value: None, constraints: Constraints::default() },
            selector: bool => FieldSchemaInfo { schema_type: SchemaFieldType::AnyOf(vec![SchemaFieldType::Boolean, SchemaFieldType::Expr]), description: Some("A condition that is used to select the prototype for the next element in the collection. If there is more than 1 true condition, the earlier prototype is selected. If none of the conditions are met, the element from `data` is skipped."), default_value: Some(serde_json::json!(true)), constraints: Constraints::default() },
        }
    }
}
