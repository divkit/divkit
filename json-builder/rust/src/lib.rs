//! # divkit-json-builder
//!
//! A Rust port of the DivKit Python json-builder.
//!
//! Provides entity types with `build()`/`dict()` and `schema()` methods
//! for constructing DivKit JSON layouts programmatically.
//!
//! ## Quick Start
//!
//! ```rust
//! use divkit_json_builder::generated::*;
//! use divkit_json_builder::entity::Entity;
//! use divkit_json_builder::value::DivValue;
//!
//! let text = DivText::new().text("Hello from Rust!");
//! let json = text.dict();
//! // => {"type": "text", "text": "Hello from Rust!"}
//!
//! let text2 = DivText::new().text("Hello from Rust!");
//! let container = DivContainer::new()
//!     .items(vec![DivValue::from(text2)]);
//! let json = container.dict();
//! // => {"type": "container", "items": [{"type": "text", "text": "Hello from Rust!"}]}
//! ```

#[macro_use]
pub mod macros;

pub mod generated;
pub mod entity;
pub mod expr;
pub mod field;
pub mod schema;
pub mod value;

// Python bindings
pub mod py_entity;
pub mod py_value;
pub mod python;

// Re-export commonly used items
pub use entity::{make_card, make_div, DivData, DivDataState, Entity, Template};
pub use expr::Expr;
pub use field::{Constraints, FieldBuilder, FieldDescriptor, FieldSchemaInfo, SchemaFieldType};
pub use value::DivValue;

#[cfg(test)]
mod tests {
    use super::generated::*;
    use super::entity::Entity;
    use super::expr::Expr;
    use super::value::DivValue;
    use serde_json::{json, Value};

    #[test]
    fn test_div_text_basic() {
        let text = DivText::new().text("Hello from pydivkit");
        let result = text.dict();
        assert_eq!(
            result,
            json!({"type": "text", "text": "Hello from pydivkit"})
        );
    }

    #[test]
    fn test_div_text_build() {
        let text = DivText::new().text("Hello");
        let result = text.build();
        assert_eq!(result, json!({"type": "text", "text": "Hello"}));
    }

    #[test]
    fn test_div_fixed_size() {
        let size = DivFixedSize::new().value(32);
        assert_eq!(size.dict(), json!({"type": "fixed", "value": 32}));
    }

    #[test]
    fn test_div_wrap_content_size() {
        let size = DivWrapContentSize::new();
        assert_eq!(size.dict(), json!({"type": "wrap_content"}));
    }

    #[test]
    fn test_div_match_parent_size() {
        let size = DivMatchParentSize::new();
        assert_eq!(size.dict(), json!({"type": "match_parent"}));
    }

    #[test]
    fn test_div_edge_insets() {
        let insets = DivEdgeInsets::new()
            .left(12)
            .right(12)
            .top(10)
            .bottom(10);
        assert_eq!(
            insets.dict(),
            json!({"left": 12, "right": 12, "top": 10, "bottom": 10})
        );
    }

    #[test]
    fn test_div_border() {
        let border = DivBorder::new().corner_radius(12);
        assert_eq!(border.dict(), json!({"corner_radius": 12}));
    }

    #[test]
    fn test_div_solid_background() {
        let bg = DivSolidBackground::new().color("#f0f0f0");
        assert_eq!(bg.dict(), json!({"type": "solid", "color": "#f0f0f0"}));
    }

    #[test]
    fn test_div_container() {
        let text = DivText::new().text("Hello from pydivkit");
        let gallery = DivGallery::new().items(vec![DivValue::from(text)]);
        let container = DivContainer::new().items(vec![DivValue::from(gallery)]);

        let result = container.dict();
        assert_eq!(
            result,
            json!({
                "type": "container",
                "items": [{
                    "type": "gallery",
                    "items": [{
                        "type": "text",
                        "text": "Hello from pydivkit"
                    }]
                }]
            })
        );
    }

    #[test]
    fn test_div_image() {
        let img = DivImage::new()
            .image_url("https://example.com/image.png")
            .width(DivFixedSize::new().value(100))
            .height(DivFixedSize::new().value(100));
        let result = img.dict();
        assert_eq!(
            result,
            json!({
                "type": "image",
                "image_url": "https://example.com/image.png",
                "width": {"type": "fixed", "value": 100},
                "height": {"type": "fixed", "value": 100}
            })
        );
    }

    #[test]
    fn test_slider_example() {
        let slider = DivSlider::new()
            .width(DivMatchParentSize::new())
            .max_value(10)
            .min_value(1)
            .thumb_style(
                DivShapeDrawable::new()
                    .color("#00b300")
                    .stroke(DivStroke::new().color("#ffffff").width(3))
                    .shape(
                        DivRoundedRectangleShape::new()
                            .item_width(DivFixedSize::new().value(32))
                            .item_height(DivFixedSize::new().value(32))
                            .corner_radius(DivFixedSize::new().value(100)),
                    ),
            )
            .track_active_style(
                DivShapeDrawable::new()
                    .color("#00b300")
                    .shape(
                        DivRoundedRectangleShape::new()
                            .item_height(DivFixedSize::new().value(6)),
                    ),
            )
            .track_inactive_style(
                DivShapeDrawable::new()
                    .color("#20000000")
                    .shape(
                        DivRoundedRectangleShape::new()
                            .item_height(DivFixedSize::new().value(6)),
                    ),
            );

        let result = slider.dict();

        assert_eq!(result["type"], "slider");
        assert_eq!(result["max_value"], 10);
        assert_eq!(result["min_value"], 1);
        assert_eq!(result["width"]["type"], "match_parent");
        assert_eq!(result["thumb_style"]["type"], "shape_drawable");
        assert_eq!(result["thumb_style"]["color"], "#00b300");
        assert_eq!(result["thumb_style"]["stroke"]["color"], "#ffffff");
        assert_eq!(result["thumb_style"]["stroke"]["width"], 3);
        assert_eq!(
            result["thumb_style"]["shape"]["type"],
            "rounded_rectangle"
        );
        assert_eq!(result["thumb_style"]["shape"]["corner_radius"]["value"], 100);
        assert_eq!(result["thumb_style"]["shape"]["item_width"]["value"], 32);
        assert_eq!(result["thumb_style"]["shape"]["item_height"]["value"], 32);
        assert_eq!(result["track_active_style"]["color"], "#00b300");
        assert_eq!(result["track_inactive_style"]["color"], "#20000000");
    }

    #[test]
    fn test_schema_basic() {
        let text = DivText::new();
        let schema = text.schema(None);
        assert_eq!(schema["type"], "object");

        let props = schema["properties"].as_object().unwrap();
        assert!(props.contains_key("type"));
        assert!(props.contains_key("text"));
        assert!(props.contains_key("font_size"));
    }

    #[test]
    fn test_schema_exclude_fields() {
        let text = DivText::new();
        let schema = text.schema(Some(&["accessibility", "actions"]));

        let props = schema["properties"].as_object().unwrap();
        assert!(!props.contains_key("accessibility"));
        assert!(!props.contains_key("actions"));
        assert!(props.contains_key("text"));
    }

    #[test]
    fn test_expr() {
        assert!(Expr::new("@{value}").is_ok());
        assert!(Expr::new("@{some_expr + 1}").is_ok());
        assert!(Expr::new("not_an_expr").is_err());
        assert!(Expr::new("@{before").is_err());
        assert!(Expr::new("{before").is_err());
        assert!(Expr::new("after}").is_err());

        let e = Expr::new("@{my_var}").unwrap();
        assert_eq!(e.to_string(), "@{my_var}");
    }

    #[test]
    fn test_div_enum_values() {
        assert_eq!(DivAlignmentVertical::Top.value(), "top");
        assert_eq!(DivAlignmentVertical::Center.value(), "center");
        assert_eq!(DivAlignmentVertical::Bottom.value(), "bottom");
        assert_eq!(DivAlignmentVertical::Baseline.value(), "baseline");

        assert_eq!(DivContainerOrientation::Horizontal.value(), "horizontal");
        assert_eq!(DivContainerOrientation::Vertical.value(), "vertical");
    }

    #[test]
    fn test_container_with_enum() {
        let container = DivContainer::new()
            .orientation(DivContainerOrientation::Horizontal)
            .content_alignment_vertical(DivAlignmentVertical::Center)
            .items(vec![DivValue::from(DivText::new().text("Hi"))]);

        let result = container.dict();
        assert_eq!(result["orientation"], "horizontal");
        assert_eq!(result["content_alignment_vertical"], "center");
    }

    #[test]
    fn test_nested_entity_composition() {
        let container = DivContainer::new()
            .width(DivWrapContentSize::new())
            .background(vec![DivValue::from(DivSolidBackground::new().color("#f0f0f0"))])
            .border(DivBorder::new().corner_radius(12))
            .paddings(
                DivEdgeInsets::new()
                    .left(12)
                    .right(12)
                    .top(10)
                    .bottom(10),
            )
            .items(vec![
                DivValue::from(DivImage::new()
                    .width(DivFixedSize::new().value(20))
                    .height(DivFixedSize::new().value(20))
                    .margins(DivEdgeInsets::new().right(6))
                    .image_url("https://example.com/icon.png")),
                DivValue::from(DivText::new()
                    .width(DivWrapContentSize::new())
                    .max_lines(1)
                    .text("Category")),
            ]);

        let result = container.dict();
        assert_eq!(result["type"], "container");
        assert_eq!(result["width"]["type"], "wrap_content");
        assert_eq!(result["background"][0]["type"], "solid");
        assert_eq!(result["background"][0]["color"], "#f0f0f0");
        assert_eq!(result["border"]["corner_radius"], 12);
        assert_eq!(result["paddings"]["left"], 12);
        assert_eq!(result["items"].as_array().unwrap().len(), 2);
        assert_eq!(result["items"][0]["type"], "image");
        assert_eq!(result["items"][1]["type"], "text");
        assert_eq!(result["items"][1]["text"], "Category");
    }

    #[test]
    fn test_div_data() {
        let text = DivText::new().text("Hello");
        let data = super::DivData {
            log_id: "sample_card".to_string(),
            states: vec![super::DivDataState {
                state_id: 0,
                div: Box::new(text),
            }],
        };

        let result = data.dict();
        assert_eq!(result["log_id"], "sample_card");
        assert_eq!(result["states"][0]["state_id"], 0);
        assert_eq!(result["states"][0]["div"]["type"], "text");
        assert_eq!(result["states"][0]["div"]["text"], "Hello");
    }

    #[test]
    fn test_field_builder() {
        let fb = super::FieldBuilder::new()
            .description("A test field")
            .gt(0.0)
            .le(100.0)
            .min_length(1);
        let desc = fb.build("test_field");
        assert_eq!(desc.name, "test_field");
        assert_eq!(desc.description.as_deref(), Some("A test field"));
        assert_eq!(desc.constraints.exclusive_minimum, Some(0.0));
        assert_eq!(desc.constraints.maximum, Some(100.0));
        assert_eq!(desc.constraints.min_length, Some(1));
    }

    #[test]
    fn test_gallery_with_items() {
        let gallery = DivGallery::new().items(vec![
            DivValue::from(DivText::new().text("Item 1")),
            DivValue::from(DivText::new().text("Item 2")),
            DivValue::from(DivText::new().text("Item 3")),
        ]);

        let result = gallery.dict();
        assert_eq!(result["type"], "gallery");
        let items = result["items"].as_array().unwrap();
        assert_eq!(items.len(), 3);
        assert_eq!(items[0]["text"], "Item 1");
        assert_eq!(items[1]["text"], "Item 2");
        assert_eq!(items[2]["text"], "Item 3");
    }

    // ================================================================
    // Entity types — background types
    // ================================================================

    #[test]
    fn test_div_linear_gradient_background() {
        let bg = DivLinearGradient::new()
            .angle(90)
            .colors(vec![DivValue::from("#ff0000"), DivValue::from("#0000ff")]);
        let result = bg.dict();
        assert_eq!(result["type"], "gradient");
        assert_eq!(result["angle"], 90);
        let colors = result["colors"].as_array().unwrap();
        assert_eq!(colors.len(), 2);
        assert_eq!(colors[0], "#ff0000");
        assert_eq!(colors[1], "#0000ff");
    }

    #[test]
    fn test_div_image_background() {
        let bg = DivImageBackground::new()
            .image_url("https://example.com/bg.png")
            .alpha(0.8)
            .scale("fill")
            .preload_required(true);
        let result = bg.dict();
        assert_eq!(result["type"], "image");
        assert_eq!(result["image_url"], "https://example.com/bg.png");
        assert_eq!(result["alpha"], 0.8);
        assert_eq!(result["scale"], "fill");
        assert_eq!(result["preload_required"], true);
    }

    // ================================================================
    // Entity types — shape types
    // ================================================================

    #[test]
    fn test_div_circle_shape() {
        let circle = DivCircleShape::new()
            .radius(DivFixedSize::new().value(24))
            .background_color("#ff0000")
            .stroke(DivStroke::new().color("#000000").width(2));
        let result = circle.dict();
        assert_eq!(result["type"], "circle");
        assert_eq!(result["radius"]["type"], "fixed");
        assert_eq!(result["radius"]["value"], 24);
        assert_eq!(result["background_color"], "#ff0000");
        assert_eq!(result["stroke"]["color"], "#000000");
        assert_eq!(result["stroke"]["width"], 2);
    }

    #[test]
    fn test_div_rounded_rectangle_shape_standalone() {
        let shape = DivRoundedRectangleShape::new()
            .item_width(DivFixedSize::new().value(50))
            .item_height(DivFixedSize::new().value(30))
            .corner_radius(DivFixedSize::new().value(8));
        let result = shape.dict();
        assert_eq!(result["type"], "rounded_rectangle");
        assert_eq!(result["item_width"]["value"], 50);
        assert_eq!(result["item_height"]["value"], 30);
        assert_eq!(result["corner_radius"]["value"], 8);
    }

    #[test]
    fn test_div_shape_drawable_standalone() {
        let drawable = DivShapeDrawable::new()
            .color("#00ff00")
            .shape(DivRoundedRectangleShape::new().item_height(DivFixedSize::new().value(4)));
        let result = drawable.dict();
        assert_eq!(result["type"], "shape_drawable");
        assert_eq!(result["color"], "#00ff00");
        assert_eq!(result["shape"]["type"], "rounded_rectangle");
    }

    // ================================================================
    // Entity types — main div components
    // ================================================================

    #[test]
    fn test_div_gif_image() {
        let gif = DivGifImage::new()
            .gif_url("https://example.com/anim.gif")
            .width(DivFixedSize::new().value(200))
            .height(DivFixedSize::new().value(200))
            .alpha(0.9);
        let result = gif.dict();
        assert_eq!(result["type"], "gif");
        assert_eq!(result["gif_url"], "https://example.com/anim.gif");
        assert_eq!(result["width"]["type"], "fixed");
        assert_eq!(result["width"]["value"], 200);
        assert_eq!(result["alpha"], 0.9);
    }

    #[test]
    fn test_div_separator() {
        let sep = DivSeparator::new()
            .delimiter_style(
                DivSeparatorDelimiterStyle::new()
                    .color("#cccccc")
                    .orientation("horizontal"),
            )
            .width(DivMatchParentSize::new())
            .height(DivFixedSize::new().value(1));
        let result = sep.dict();
        assert_eq!(result["type"], "separator");
        assert_eq!(result["delimiter_style"]["color"], "#cccccc");
        assert_eq!(result["delimiter_style"]["orientation"], "horizontal");
        assert_eq!(result["width"]["type"], "match_parent");
        assert_eq!(result["height"]["type"], "fixed");
        assert_eq!(result["height"]["value"], 1);
    }

    #[test]
    fn test_div_input() {
        let input = DivInput::new()
            .text_variable("user_name")
            .font_size(16)
            .hint_text("Enter name")
            .hint_color("#888888")
            .max_visible_lines(1)
            .keyboard_type("default");
        let result = input.dict();
        assert_eq!(result["type"], "input");
        assert_eq!(result["text_variable"], "user_name");
        assert_eq!(result["font_size"], 16);
        assert_eq!(result["hint_text"], "Enter name");
        assert_eq!(result["hint_color"], "#888888");
        assert_eq!(result["max_visible_lines"], 1);
        assert_eq!(result["keyboard_type"], "default");
    }

    #[test]
    fn test_div_pager() {
        let pager = DivPager::new()
            .items(vec![
                DivValue::from(DivText::new().text("Page 1")),
                DivValue::from(DivText::new().text("Page 2")),
            ])
            .layout_mode(
                DivNeighbourPageSize::new()
                    .neighbour_page_width(DivFixedSize::new().value(20)),
            )
            .orientation("horizontal");
        let result = pager.dict();
        assert_eq!(result["type"], "pager");
        assert_eq!(result["items"].as_array().unwrap().len(), 2);
        assert_eq!(result["orientation"], "horizontal");
        assert_eq!(
            result["layout_mode"]["neighbour_page_width"]["type"],
            "fixed"
        );
        assert_eq!(result["layout_mode"]["neighbour_page_width"]["value"], 20);
    }

    #[test]
    fn test_div_percentage_size() {
        let size = DivPercentageSize::new().value(0.5);
        let result = size.dict();
        assert_eq!(result["type"], "percentage");
        assert_eq!(result["value"], 0.5);
    }

    #[test]
    fn test_div_tabs() {
        let tabs = DivTabs::new()
            .items(vec![
                DivValue::from(
                    DivTabsItem::new()
                        .title("Tab 1")
                        .div(DivText::new().text("Content 1")),
                ),
                DivValue::from(
                    DivTabsItem::new()
                        .title("Tab 2")
                        .div(DivText::new().text("Content 2")),
                ),
            ])
            .selected_tab(0);
        let result = tabs.dict();
        assert_eq!(result["type"], "tabs");
        assert_eq!(result["selected_tab"], 0);
        let items = result["items"].as_array().unwrap();
        assert_eq!(items.len(), 2);
        assert_eq!(items[0]["title"], "Tab 1");
        assert_eq!(items[0]["div"]["type"], "text");
        assert_eq!(items[0]["div"]["text"], "Content 1");
        assert_eq!(items[1]["title"], "Tab 2");
    }

    #[test]
    fn test_div_state() {
        let state = DivState::new()
            .states(vec![
                DivValue::from(
                    DivStateState::new()
                        .state_id("active")
                        .div(DivText::new().text("Active")),
                ),
                DivValue::from(
                    DivStateState::new()
                        .state_id("inactive")
                        .div(DivText::new().text("Inactive")),
                ),
            ])
            .div_id("my_state")
            .default_state_id("active");
        let result = state.dict();
        assert_eq!(result["type"], "state");
        assert_eq!(result["div_id"], "my_state");
        assert_eq!(result["default_state_id"], "active");
        let states = result["states"].as_array().unwrap();
        assert_eq!(states.len(), 2);
        assert_eq!(states[0]["state_id"], "active");
        assert_eq!(states[0]["div"]["text"], "Active");
    }

    #[test]
    fn test_div_custom() {
        let custom = DivCustom::new()
            .custom_type("my_widget")
            .custom_props("some_data")
            .width(DivMatchParentSize::new());
        let result = custom.dict();
        assert_eq!(result["type"], "custom");
        assert_eq!(result["custom_type"], "my_widget");
        assert_eq!(result["custom_props"], "some_data");
        assert_eq!(result["width"]["type"], "match_parent");
    }

    #[test]
    fn test_div_indicator() {
        let indicator = DivIndicator::new()
            .pager_id("pager1")
            .active_item_color("#ff0000")
            .inactive_item_color("#cccccc")
            .space_between_centers(DivFixedSize::new().value(12))
            .shape(DivRoundedRectangleShape::new()
                .item_width(DivFixedSize::new().value(8))
                .item_height(DivFixedSize::new().value(8)));
        let result = indicator.dict();
        assert_eq!(result["type"], "indicator");
        assert_eq!(result["pager_id"], "pager1");
        assert_eq!(result["active_item_color"], "#ff0000");
        assert_eq!(result["inactive_item_color"], "#cccccc");
        assert_eq!(result["space_between_centers"]["value"], 12);
        assert_eq!(result["shape"]["type"], "rounded_rectangle");
        assert_eq!(result["shape"]["item_width"]["value"], 8);
    }

    #[test]
    fn test_div_grid() {
        let grid = DivGrid::new()
            .column_count(2)
            .items(vec![
                DivValue::from(DivText::new().text("Cell 1").column_span(1).row_span(1)),
                DivValue::from(DivText::new().text("Cell 2").column_span(1).row_span(1)),
                DivValue::from(DivText::new().text("Cell 3").column_span(2).row_span(1)),
            ])
            .width(DivMatchParentSize::new());
        let result = grid.dict();
        assert_eq!(result["type"], "grid");
        assert_eq!(result["column_count"], 2);
        assert_eq!(result["items"].as_array().unwrap().len(), 3);
        assert_eq!(result["width"]["type"], "match_parent");
        assert_eq!(result["items"][2]["column_span"], 2);
    }

    // ================================================================
    // Entity types — animation and transform
    // ================================================================

    #[test]
    fn test_div_animation() {
        let anim = DivAnimation::new()
            .name("fade")
            .duration(300)
            .start_delay(100)
            .interpolator("ease_in_out")
            .start_value(0.0)
            .end_value(1.0);
        let result = anim.dict();
        // DivAnimation has no type_name in the schema
        assert_eq!(result["name"], "fade");
        assert_eq!(result["duration"], 300);
        assert_eq!(result["start_delay"], 100);
        assert_eq!(result["interpolator"], "ease_in_out");
        assert_eq!(result["start_value"], 0);
        assert_eq!(result["end_value"], 1);
    }

    #[test]
    fn test_div_transform() {
        let transform = DivTransform::new()
            .rotation(45.0)
            .pivot_x(DivPivotPercentage::new().value(0.5))
            .pivot_y(DivPivotPercentage::new().value(0.5));
        let result = transform.dict();
        assert_eq!(result["rotation"], 45);
        assert_eq!(result["pivot_x"]["type"], "pivot-percentage");
        assert_eq!(result["pivot_x"]["value"], 0.5);
        assert_eq!(result["pivot_y"]["type"], "pivot-percentage");
    }

    // ================================================================
    // Entity types — tooltip
    // ================================================================

    #[test]
    fn test_div_tooltip() {
        let tooltip = DivTooltip::new()
            .id("tooltip_1")
            .div(DivText::new().text("Tooltip text"))
            .position("top")
            .duration(3000)
            .offset(DivPoint::new()
                .x(DivDimension::new().value(0.0))
                .y(DivDimension::new().value(-8.0)));
        let result = tooltip.dict();
        assert_eq!(result["id"], "tooltip_1");
        assert_eq!(result["div"]["type"], "text");
        assert_eq!(result["div"]["text"], "Tooltip text");
        assert_eq!(result["position"], "top");
        assert_eq!(result["duration"], 3000);
        assert_eq!(result["offset"]["x"]["value"], 0);
        assert_eq!(result["offset"]["y"]["value"], -8);
    }

    // ================================================================
    // Entity types — gradient
    // ================================================================

    #[test]
    fn test_div_linear_gradient() {
        let gradient = DivLinearGradient::new()
            .angle(135)
            .colors(vec![DivValue::from("#ff0000"), DivValue::from("#00ff00")]);
        let result = gradient.dict();
        assert_eq!(result["type"], "gradient");
        assert_eq!(result["angle"], 135);
        assert_eq!(result["colors"].as_array().unwrap().len(), 2);
    }

    // ================================================================
    // Entity types — animator and actions
    // ================================================================

    #[test]
    fn test_div_number_animator() {
        let animator = DivNumberAnimator::new()
            .id("anim_1")
            .variable_name("progress")
            .duration(500)
            .start_value(0.0)
            .end_value(100.0)
            .interpolator("ease_out");
        let result = animator.dict();
        assert_eq!(result["type"], "number_animator");
        assert_eq!(result["id"], "anim_1");
        assert_eq!(result["variable_name"], "progress");
        assert_eq!(result["duration"], 500);
        assert_eq!(result["start_value"], 0);
        assert_eq!(result["end_value"], 100);
        assert_eq!(result["interpolator"], "ease_out");
    }

    #[test]
    fn test_div_action_animator_start() {
        let action = DivActionAnimatorStart::new()
            .animator_id("anim_1")
            .direction("forward")
            .duration(300)
            .start_value(0.0)
            .end_value(1.0);
        let result = action.dict();
        assert_eq!(result["type"], "animator_start");
        assert_eq!(result["animator_id"], "anim_1");
        assert_eq!(result["direction"], "forward");
        assert_eq!(result["duration"], 300);
    }

    #[test]
    fn test_div_action_set_variable() {
        let action = DivActionSetVariable::new()
            .variable_name("counter")
            .value(DivValue::Int(42));
        let result = action.dict();
        assert_eq!(result["type"], "set_variable");
        assert_eq!(result["variable_name"], "counter");
        assert_eq!(result["value"], 42);
    }

    // ================================================================
    // Entity types — variables
    // ================================================================

    #[test]
    fn test_string_variable() {
        let var = StringVariable::new().name("greeting").value("hello");
        let result = var.dict();
        assert_eq!(result["type"], "string");
        assert_eq!(result["name"], "greeting");
        assert_eq!(result["value"], "hello");
    }

    #[test]
    fn test_number_variable() {
        let var = NumberVariable::new().name("count").value(3.14);
        let result = var.dict();
        assert_eq!(result["type"], "number");
        assert_eq!(result["name"], "count");
        assert_eq!(result["value"], 3.14);
    }

    #[test]
    fn test_boolean_variable() {
        let var = BooleanVariable::new().name("is_active").value(true);
        let result = var.dict();
        assert_eq!(result["type"], "boolean");
        assert_eq!(result["name"], "is_active");
        assert_eq!(result["value"], true);
    }

    // ================================================================
    // Entity types — value types
    // ================================================================

    #[test]
    fn test_string_value() {
        let val = StringValue::new().value("test_string");
        let result = val.dict();
        assert_eq!(result["type"], "string");
        assert_eq!(result["value"], "test_string");
    }

    #[test]
    fn test_boolean_value() {
        let val = BooleanValue::new().value(false);
        let result = val.dict();
        assert_eq!(result["type"], "boolean");
        assert_eq!(result["value"], false);
    }

    // ================================================================
    // Entity types — utility types
    // ================================================================

    #[test]
    fn test_div_page_size() {
        let ps = DivPageSize::new()
            .page_width(DivPercentageSize::new().value(80.0));
        let result = ps.dict();
        assert_eq!(result["type"], "percentage");
        assert_eq!(result["page_width"]["type"], "percentage");
        assert_eq!(result["page_width"]["value"], 80);
    }

    #[test]
    fn test_div_aspect() {
        let aspect = DivAspect::new().ratio(1.5);
        let result = aspect.dict();
        assert_eq!(result["ratio"], 1.5);
    }

    #[test]
    fn test_div_download_callbacks() {
        let cb = DivDownloadCallbacks::new()
            .on_fail_actions(vec![DivValue::from(
                DivAction::new().log_id("fail_log").url("div-action://fail"),
            )])
            .on_success_actions(vec![DivValue::from(
                DivAction::new().log_id("success_log").url("div-action://ok"),
            )]);
        let result = cb.dict();
        assert_eq!(result["on_fail_actions"].as_array().unwrap().len(), 1);
        assert_eq!(result["on_fail_actions"][0]["log_id"], "fail_log");
        assert_eq!(result["on_success_actions"].as_array().unwrap().len(), 1);
        assert_eq!(result["on_success_actions"][0]["log_id"], "success_log");
    }

    #[test]
    fn test_div_extension() {
        let ext = DivExtension::new().id("my_extension");
        let result = ext.dict();
        assert_eq!(result["id"], "my_extension");
    }

    #[test]
    fn test_div_visibility_action() {
        let va = DivVisibilityAction::new()
            .log_id("visibility_log")
            .visibility_duration(800)
            .visibility_percentage(50)
            .is_enabled(true);
        let result = va.dict();
        assert_eq!(result["log_id"], "visibility_log");
        assert_eq!(result["visibility_duration"], 800);
        assert_eq!(result["visibility_percentage"], 50);
        assert_eq!(result["is_enabled"], true);
    }

    #[test]
    fn test_div_trigger() {
        let trigger = DivTrigger::new()
            .condition("@{count > 5}")
            .mode("on_condition")
            .actions(vec![DivValue::from(
                DivAction::new().log_id("trigger_action").url("div-action://trigger"),
            )]);
        let result = trigger.dict();
        assert_eq!(result["condition"], "@{count > 5}");
        assert_eq!(result["mode"], "on_condition");
        assert_eq!(result["actions"].as_array().unwrap().len(), 1);
    }

    #[test]
    fn test_div_timer() {
        let timer = DivTimer::new()
            .id("timer_1")
            .duration(5000)
            .tick_interval(1000)
            .value_variable("elapsed")
            .tick_actions(vec![DivValue::from(
                DivAction::new().log_id("tick"),
            )])
            .end_actions(vec![DivValue::from(
                DivAction::new().log_id("end"),
            )]);
        let result = timer.dict();
        assert_eq!(result["id"], "timer_1");
        assert_eq!(result["duration"], 5000);
        assert_eq!(result["tick_interval"], 1000);
        assert_eq!(result["value_variable"], "elapsed");
        assert_eq!(result["tick_actions"].as_array().unwrap().len(), 1);
        assert_eq!(result["end_actions"].as_array().unwrap().len(), 1);
    }

    #[test]
    fn test_div_action() {
        let action = DivAction::new()
            .log_id("click_action")
            .url("https://example.com");
        let result = action.dict();
        assert_eq!(result["log_id"], "click_action");
        assert_eq!(result["url"], "https://example.com");
    }

    #[test]
    fn test_div_accessibility() {
        let acc = DivAccessibility::new()
            .description("A button")
            .hint("Tap to proceed")
            .mode("default")
            .r#type("button");
        let result = acc.dict();
        assert_eq!(result["description"], "A button");
        assert_eq!(result["hint"], "Tap to proceed");
        assert_eq!(result["mode"], "default");
        // r#type field name contains "r#" prefix due to Rust raw identifier stringify
        assert!(result.get("r#type").is_some() || result.get("type").is_some());
    }

    #[test]
    fn test_div_corners_radius() {
        let cr = DivCornersRadius::new()
            .top_left(8)
            .top_right(8)
            .bottom_left(0)
            .bottom_right(0);
        let result = cr.dict();
        assert_eq!(result["top_left"], 8);
        assert_eq!(result["top_right"], 8);
        assert_eq!(result["bottom_left"], 0);
        assert_eq!(result["bottom_right"], 0);
    }

    #[test]
    fn test_div_shadow() {
        let shadow = DivShadow::new()
            .alpha(0.5)
            .blur(10)
            .color("#000000")
            .offset(DivPoint::new()
                .x(DivDimension::new().value(2.0))
                .y(DivDimension::new().value(4.0)));
        let result = shadow.dict();
        assert_eq!(result["alpha"], 0.5);
        assert_eq!(result["blur"], 10);
        assert_eq!(result["color"], "#000000");
        assert_eq!(result["offset"]["x"]["value"], 2);
        assert_eq!(result["offset"]["y"]["value"], 4);
    }

    #[test]
    fn test_div_slider_text_style() {
        let style = DivSliderTextStyle::new()
            .font_size(14)
            .font_weight("bold")
            .text_color("#ffffff");
        let result = style.dict();
        assert_eq!(result["font_size"], 14);
        assert_eq!(result["font_weight"], "bold");
        assert_eq!(result["text_color"], "#ffffff");
    }

    #[test]
    fn test_div_tabs_tab_title_style() {
        let style = DivTabsTabTitleStyle::new()
            .font_size(14)
            .font_weight("medium")
            .active_text_color("#000000")
            .inactive_text_color("#888888")
            .active_background_color("#ffffff")
            .corner_radius(8)
            .item_spacing(4);
        let result = style.dict();
        assert_eq!(result["font_size"], 14);
        assert_eq!(result["font_weight"], "medium");
        assert_eq!(result["active_text_color"], "#000000");
        assert_eq!(result["inactive_text_color"], "#888888");
        assert_eq!(result["corner_radius"], 8);
        assert_eq!(result["item_spacing"], 4);
    }

    #[test]
    fn test_div_wrap_content_size_with_constraints() {
        let size = DivWrapContentSize::new()
            .constrained(true)
            .max_size(DivFixedSize::new().value(200))
            .min_size(DivFixedSize::new().value(50));
        let result = size.dict();
        assert_eq!(result["type"], "wrap_content");
        assert_eq!(result["constrained"], true);
        assert_eq!(result["max_size"]["value"], 200);
        assert_eq!(result["min_size"]["value"], 50);
    }

    #[test]
    fn test_div_match_parent_size_with_weight() {
        let size = DivMatchParentSize::new().weight(2.0);
        let result = size.dict();
        assert_eq!(result["type"], "match_parent");
        assert_eq!(result["weight"], 2);
    }

    #[test]
    fn test_div_fixed_size_with_unit() {
        let size = DivFixedSize::new().value(16).unit("sp");
        let result = size.dict();
        assert_eq!(result["type"], "fixed");
        assert_eq!(result["value"], 16);
        assert_eq!(result["unit"], "sp");
    }

    // ================================================================
    // Enum tests
    // ================================================================

    #[test]
    fn test_div_alignment_horizontal() {
        assert_eq!(DivAlignmentHorizontal::Left.value(), "left");
        assert_eq!(DivAlignmentHorizontal::Center.value(), "center");
        assert_eq!(DivAlignmentHorizontal::Right.value(), "right");
        assert_eq!(DivAlignmentHorizontal::Start.value(), "start");
        assert_eq!(DivAlignmentHorizontal::End.value(), "end");
    }

    #[test]
    fn test_div_visibility_enum() {
        assert_eq!(DivVisibility::Visible.value(), "visible");
        assert_eq!(DivVisibility::Invisible.value(), "invisible");
        assert_eq!(DivVisibility::Gone.value(), "gone");
    }

    #[test]
    fn test_div_font_weight_enum() {
        assert_eq!(DivFontWeight::Light.value(), "light");
        assert_eq!(DivFontWeight::Regular.value(), "regular");
        assert_eq!(DivFontWeight::Medium.value(), "medium");
        assert_eq!(DivFontWeight::Bold.value(), "bold");
    }

    #[test]
    fn test_div_line_style_enum() {
        assert_eq!(DivLineStyle::None.value(), "none");
        assert_eq!(DivLineStyle::Single.value(), "single");
    }

    #[test]
    fn test_div_image_scale_enum() {
        assert_eq!(DivImageScale::Fill.value(), "fill");
        assert_eq!(DivImageScale::NoScale.value(), "no_scale");
        assert_eq!(DivImageScale::Fit.value(), "fit");
        assert_eq!(DivImageScale::Stretch.value(), "stretch");
    }

    #[test]
    fn test_div_size_unit_enum() {
        assert_eq!(DivSizeUnit::Dp.value(), "dp");
        assert_eq!(DivSizeUnit::Sp.value(), "sp");
        assert_eq!(DivSizeUnit::Px.value(), "px");
    }

    #[test]
    fn test_div_gallery_scroll_mode_enum() {
        assert_eq!(DivGalleryScrollMode::Paging.value(), "paging");
        assert_eq!(DivGalleryScrollMode::Default.value(), "default");
    }

    #[test]
    fn test_div_gallery_cross_content_alignment_enum() {
        assert_eq!(DivGalleryCrossContentAlignment::Start.value(), "start");
        assert_eq!(DivGalleryCrossContentAlignment::Center.value(), "center");
        assert_eq!(DivGalleryCrossContentAlignment::End.value(), "end");
    }

    #[test]
    fn test_div_content_alignment_horizontal_enum() {
        assert_eq!(DivContentAlignmentHorizontal::Center.value(), "center");
        assert_eq!(DivContentAlignmentHorizontal::SpaceAround.value(), "space-around");
        assert_eq!(DivContentAlignmentHorizontal::SpaceBetween.value(), "space-between");
        assert_eq!(DivContentAlignmentHorizontal::SpaceEvenly.value(), "space-evenly");
    }

    #[test]
    fn test_div_content_alignment_vertical_enum() {
        assert_eq!(DivContentAlignmentVertical::Baseline.value(), "baseline");
        assert_eq!(DivContentAlignmentVertical::Bottom.value(), "bottom");
        assert_eq!(DivContentAlignmentVertical::SpaceAround.value(), "space-around");
        assert_eq!(DivContentAlignmentVertical::Top.value(), "top");
    }

    #[test]
    fn test_div_blend_mode_enum() {
        assert_eq!(DivBlendMode::Darken.value(), "darken");
        assert_eq!(DivBlendMode::Lighten.value(), "lighten");
        assert_eq!(DivBlendMode::Multiply.value(), "multiply");
        assert_eq!(DivBlendMode::Screen.value(), "screen");
        assert_eq!(DivBlendMode::SourceAtop.value(), "source_atop");
        assert_eq!(DivBlendMode::SourceIn.value(), "source_in");
    }

    #[test]
    fn test_div_accessibility_mode_enum() {
        assert_eq!(DivAccessibilityMode::Default.value(), "default");
        assert_eq!(DivAccessibilityMode::Exclude.value(), "exclude");
        assert_eq!(DivAccessibilityMode::Merge.value(), "merge");
    }

    #[test]
    fn test_div_accessibility_type_enum() {
        assert_eq!(DivAccessibilityType::Auto.value(), "auto");
        assert_eq!(DivAccessibilityType::Button.value(), "button");
        assert_eq!(DivAccessibilityType::Text.value(), "text");
        assert_eq!(DivAccessibilityType::Image.value(), "image");
        assert_eq!(DivAccessibilityType::None.value(), "none");
    }

    #[test]
    fn test_div_container_layout_mode_enum() {
        assert_eq!(DivContainerLayoutMode::NoWrap.value(), "no_wrap");
        assert_eq!(DivContainerLayoutMode::Wrap.value(), "wrap");
    }

    #[test]
    fn test_div_animation_interpolator_enum() {
        assert_eq!(DivAnimationInterpolator::Ease.value(), "ease");
        assert_eq!(DivAnimationInterpolator::EaseIn.value(), "ease_in");
        assert_eq!(DivAnimationInterpolator::EaseInOut.value(), "ease_in_out");
        assert_eq!(DivAnimationInterpolator::EaseOut.value(), "ease_out");
        assert_eq!(DivAnimationInterpolator::Linear.value(), "linear");
        assert_eq!(DivAnimationInterpolator::Spring.value(), "spring");
    }

    #[test]
    fn test_tab_title_style_animation_type_enum() {
        assert_eq!(TabTitleStyleAnimationType::Fade.value(), "fade");
        assert_eq!(TabTitleStyleAnimationType::None.value(), "none");
        assert_eq!(TabTitleStyleAnimationType::Slide.value(), "slide");
    }

    #[test]
    fn test_enum_display_trait() {
        assert_eq!(format!("{}", DivVisibility::Visible), "visible");
        assert_eq!(format!("{}", DivFontWeight::Bold), "bold");
        assert_eq!(format!("{}", DivImageScale::Fit), "fit");
    }

    #[test]
    fn test_enum_variants() {
        let variants = DivAlignmentVertical::variants();
        assert_eq!(variants, &["top", "center", "bottom", "baseline"]);

        let variants = DivVisibility::variants();
        assert_eq!(variants, &["visible", "invisible", "gone"]);
    }

    #[test]
    fn test_enum_into_divvalue() {
        let v: DivValue = DivVisibility::Visible.into();
        match v {
            DivValue::Enum(s) => assert_eq!(s, "visible"),
            _ => panic!("Expected DivValue::Enum"),
        }
    }

    // ================================================================
    // DivValue tests
    // ================================================================

    #[test]
    fn test_divvalue_is_null() {
        assert!(DivValue::Null.is_null());
        assert!(!DivValue::Int(0).is_null());
        assert!(!DivValue::Bool(false).is_null());
        assert!(!DivValue::String("".to_string()).is_null());
        assert!(!DivValue::Float(0.0).is_null());
    }

    #[test]
    fn test_divvalue_to_json_primitives() {
        assert_eq!(DivValue::Null.to_json(), Value::Null);
        assert_eq!(DivValue::Int(42).to_json(), json!(42));
        assert_eq!(DivValue::Float(3.14).to_json(), json!(3.14));
        assert_eq!(DivValue::String("hello".into()).to_json(), json!("hello"));
        assert_eq!(DivValue::Bool(true).to_json(), json!(true));
        assert_eq!(DivValue::Bool(false).to_json(), json!(false));
        // Whole-number floats serialize as integers
        assert_eq!(DivValue::Float(1.0).to_json(), json!(1));
        assert_eq!(DivValue::Float(-3.0).to_json(), json!(-3));
        // Fractional floats stay as floats
        assert_eq!(DivValue::Float(1.5).to_json(), json!(1.5));
    }

    #[test]
    fn test_divvalue_to_json_expr() {
        let expr = Expr::new("@{my_var}").unwrap();
        let val = DivValue::Expr(expr);
        assert_eq!(val.to_json(), json!("@{my_var}"));
    }

    #[test]
    fn test_divvalue_to_json_enum() {
        let val = DivValue::Enum("center".to_string());
        assert_eq!(val.to_json(), json!("center"));
    }

    #[test]
    fn test_divvalue_to_json_array() {
        let val = DivValue::Array(vec![
            DivValue::Int(1),
            DivValue::Int(2),
            DivValue::Int(3),
        ]);
        assert_eq!(val.to_json(), json!([1, 2, 3]));
    }

    #[test]
    fn test_divvalue_to_json_map() {
        let val = DivValue::Map(vec![
            ("key1".to_string(), DivValue::String("val1".into())),
            ("key2".to_string(), DivValue::Int(99)),
        ]);
        let result = val.to_json();
        assert_eq!(result["key1"], "val1");
        assert_eq!(result["key2"], 99);
    }

    #[test]
    fn test_divvalue_to_json_entity() {
        let text = DivText::new().text("hi");
        let val = DivValue::Entity(Box::new(text));
        let result = val.to_json();
        assert_eq!(result["type"], "text");
        assert_eq!(result["text"], "hi");
    }

    #[test]
    fn test_divvalue_schema_type_name() {
        assert_eq!(DivValue::Null.schema_type_name(), "null");
        assert_eq!(DivValue::Bool(true).schema_type_name(), "boolean");
        assert_eq!(DivValue::Int(1).schema_type_name(), "integer");
        assert_eq!(DivValue::Float(1.0).schema_type_name(), "number");
        assert_eq!(DivValue::String("s".into()).schema_type_name(), "string");
        assert_eq!(
            DivValue::Expr(Expr::new("@{x}").unwrap()).schema_type_name(),
            "string"
        );
        assert_eq!(DivValue::Enum("e".into()).schema_type_name(), "string");
        assert_eq!(
            DivValue::Entity(Box::new(DivText::new())).schema_type_name(),
            "object"
        );
        assert_eq!(DivValue::Array(vec![]).schema_type_name(), "array");
        assert_eq!(DivValue::Map(vec![]).schema_type_name(), "object");
    }

    // ================================================================
    // DivValue From conversions
    // ================================================================

    #[test]
    fn test_divvalue_from_bool() {
        let v: DivValue = true.into();
        match v {
            DivValue::Bool(b) => assert!(b),
            _ => panic!("Expected Bool"),
        }
    }

    #[test]
    fn test_divvalue_from_i32() {
        let v: DivValue = 42i32.into();
        match v {
            DivValue::Int(n) => assert_eq!(n, 42),
            _ => panic!("Expected Int"),
        }
    }

    #[test]
    fn test_divvalue_from_i64() {
        let v: DivValue = 100i64.into();
        match v {
            DivValue::Int(n) => assert_eq!(n, 100),
            _ => panic!("Expected Int"),
        }
    }

    #[test]
    fn test_divvalue_from_u32() {
        let v: DivValue = 255u32.into();
        match v {
            DivValue::Int(n) => assert_eq!(n, 255),
            _ => panic!("Expected Int"),
        }
    }

    #[test]
    fn test_divvalue_from_f64() {
        let v: DivValue = 2.718f64.into();
        match v {
            DivValue::Float(f) => assert!((f - 2.718).abs() < f64::EPSILON),
            _ => panic!("Expected Float"),
        }
    }

    #[test]
    fn test_divvalue_from_str() {
        let v: DivValue = "hello".into();
        match v {
            DivValue::String(s) => assert_eq!(s, "hello"),
            _ => panic!("Expected String"),
        }
    }

    #[test]
    fn test_divvalue_from_string() {
        let v: DivValue = String::from("world").into();
        match v {
            DivValue::String(s) => assert_eq!(s, "world"),
            _ => panic!("Expected String"),
        }
    }

    #[test]
    fn test_divvalue_from_expr() {
        let expr = Expr::new("@{x + 1}").unwrap();
        let v: DivValue = expr.into();
        match v {
            DivValue::Expr(e) => assert_eq!(e.to_string(), "@{x + 1}"),
            _ => panic!("Expected Expr"),
        }
    }

    #[test]
    fn test_divvalue_from_vec() {
        let v: DivValue = vec![1i32, 2i32, 3i32].into();
        match v {
            DivValue::Array(arr) => {
                assert_eq!(arr.len(), 3);
                assert!(matches!(arr[0], DivValue::Int(1)));
            }
            _ => panic!("Expected Array"),
        }
    }

    #[test]
    fn test_divvalue_from_option_some() {
        let v: DivValue = Some(42i32).into();
        match v {
            DivValue::Int(n) => assert_eq!(n, 42),
            _ => panic!("Expected Int"),
        }
    }

    #[test]
    fn test_divvalue_from_option_none() {
        let v: DivValue = Option::<i32>::None.into();
        assert!(v.is_null());
    }

    #[test]
    fn test_divvalue_from_boxed_entity() {
        let text = Box::new(DivText::new().text("boxed"));
        let v: DivValue = text.into();
        match v {
            DivValue::Entity(e) => {
                let json = e.dict();
                assert_eq!(json["text"], "boxed");
            }
            _ => panic!("Expected Entity"),
        }
    }

    // ================================================================
    // Expr tests
    // ================================================================

    #[test]
    fn test_expr_value_method() {
        let e = Expr::new("@{my_var}").unwrap();
        assert_eq!(e.value(), "@{my_var}");
    }

    #[test]
    fn test_expr_into_serde_value() {
        let e = Expr::new("@{val}").unwrap();
        let v: serde_json::Value = e.into();
        assert_eq!(v, json!("@{val}"));
    }

    #[test]
    fn test_expr_ref_into_serde_value() {
        let e = Expr::new("@{val}").unwrap();
        let v: serde_json::Value = (&e).into();
        assert_eq!(v, json!("@{val}"));
    }

    #[test]
    fn test_expr_equality() {
        let e1 = Expr::new("@{x}").unwrap();
        let e2 = Expr::new("@{x}").unwrap();
        let e3 = Expr::new("@{y}").unwrap();
        assert_eq!(e1, e2);
        assert_ne!(e1, e3);
    }

    // ================================================================
    // Entity helper functions
    // ================================================================

    #[test]
    fn test_make_card() {
        let text = DivText::new().text("hello");
        let card = super::make_card("test_card", &text);
        let result = card.dict();
        assert_eq!(result["log_id"], "test_card");
        assert_eq!(result["states"].as_array().unwrap().len(), 1);
        assert_eq!(result["states"][0]["state_id"], 0);
        assert_eq!(result["states"][0]["div"]["type"], "text");
        assert_eq!(result["states"][0]["div"]["text"], "hello");
    }

    #[test]
    fn test_make_div() {
        let text = DivText::new().text("div content");
        let result = super::make_div(&text);
        assert!(result["templates"].is_object());
        assert!(result["card"].is_object());
        assert_eq!(result["card"]["log_id"], "card");
        assert_eq!(result["card"]["states"][0]["div"]["type"], "text");
        assert_eq!(result["card"]["states"][0]["div"]["text"], "div content");
    }

    #[test]
    fn test_entity_to_value() {
        let text = DivText::new().text("wrapped");
        let val = super::entity::entity_to_value(text);
        match val {
            DivValue::Entity(e) => {
                let json = e.dict();
                assert_eq!(json["type"], "text");
                assert_eq!(json["text"], "wrapped");
            }
            _ => panic!("Expected Entity"),
        }
    }

    #[test]
    fn test_collect_templates_empty() {
        let val = DivValue::String("not an entity".into());
        let templates = super::entity::collect_templates(&val);
        assert!(templates.is_empty());
    }

    #[test]
    fn test_collect_templates_from_array() {
        let arr = DivValue::Array(vec![
            DivValue::String("a".into()),
            DivValue::Int(1),
        ]);
        let templates = super::entity::collect_templates(&arr);
        assert!(templates.is_empty());
    }

    #[test]
    fn test_collect_templates_from_entity() {
        let text = DivText::new().text("test");
        let val = DivValue::Entity(Box::new(text));
        let templates = super::entity::collect_templates(&val);
        // DivText has no related_templates by default
        assert!(templates.is_empty());
    }

    // ================================================================
    // Entity trait default methods
    // ================================================================

    #[test]
    fn test_entity_type_name_with_type() {
        let text = DivText::new();
        assert_eq!(text.type_name(), Some("text"));
    }

    #[test]
    fn test_entity_type_name_without_type() {
        // Entities without type_name return None
        let insets = DivEdgeInsets::new();
        assert_eq!(insets.type_name(), None);
    }

    #[test]
    fn test_entity_related_templates_default() {
        let text = DivText::new();
        assert!(text.related_templates().is_empty());
    }

    #[test]
    fn test_entity_clone_box() {
        use super::entity::EntityClone;
        let text = DivText::new().text("clone me");
        let cloned = text.clone_box();
        let result = cloned.dict();
        assert_eq!(result["type"], "text");
        assert_eq!(result["text"], "clone me");
    }

    #[test]
    fn test_boxed_entity_clone() {
        let text = DivText::new().text("boxed clone");
        let boxed: Box<dyn super::entity::Entity> = Box::new(text);
        let cloned = boxed.clone();
        assert_eq!(cloned.dict()["text"], "boxed clone");
    }

    // ================================================================
    // Field system tests
    // ================================================================

    #[test]
    fn test_constraints_is_empty() {
        let c = super::Constraints::default();
        assert!(c.is_empty());
    }

    #[test]
    fn test_constraints_is_not_empty() {
        let mut c = super::Constraints::default();
        c.minimum = Some(0.0);
        assert!(!c.is_empty());
    }

    #[test]
    fn test_constraints_to_json_map() {
        let mut c = super::Constraints::default();
        c.minimum = Some(0.0);
        c.maximum = Some(100.0);
        c.min_length = Some(1);
        c.max_length = Some(255);
        c.format = Some("uri".to_string());
        c.regex = Some("^https://".to_string());
        c.min_items = Some(1);
        c.max_items = Some(10);
        c.unique_items = Some(true);
        c.multiple_of = Some(2.0);
        c.exclusive_minimum = Some(-1.0);
        c.exclusive_maximum = Some(101.0);

        let map = c.to_json_map();
        assert_eq!(map["minimum"], json!(0.0));
        assert_eq!(map["maximum"], json!(100.0));
        assert_eq!(map["minLength"], json!(1));
        assert_eq!(map["maxLength"], json!(255));
        assert_eq!(map["format"], json!("uri"));
        assert_eq!(map["regex"], json!("^https://"));
        assert_eq!(map["minItems"], json!(1));
        assert_eq!(map["maxItems"], json!(10));
        assert_eq!(map["uniqueItems"], json!(true));
        assert_eq!(map["multipleOf"], json!(2.0));
        assert_eq!(map["exclusiveMinimum"], json!(-1.0));
        assert_eq!(map["exclusiveMaximum"], json!(101.0));
    }

    #[test]
    fn test_field_descriptor_with_default() {
        let fd = super::FieldDescriptor::new("color")
            .with_default(json!("#000000"));
        assert_eq!(fd.name, "color");
        assert_eq!(fd.default, Some(json!("#000000")));
    }

    #[test]
    fn test_field_descriptor_with_description() {
        let fd = super::FieldDescriptor::new("width")
            .with_description("The width in pixels");
        assert_eq!(fd.description, Some("The width in pixels".to_string()));
    }

    #[test]
    fn test_field_descriptor_with_constraints() {
        let mut c = super::Constraints::default();
        c.minimum = Some(0.0);
        let fd = super::FieldDescriptor::new("value")
            .with_constraints(c);
        assert_eq!(fd.constraints.minimum, Some(0.0));
    }

    #[test]
    fn test_field_descriptor_required() {
        let fd = super::FieldDescriptor::new("text").required();
        assert!(fd.required);
    }

    #[test]
    fn test_field_builder_all_methods() {
        let fb = super::FieldBuilder::new()
            .name("custom_name")
            .description("A description")
            .default_value(json!(42))
            .format("uri")
            .gt(0.0)
            .ge(1.0)
            .lt(100.0)
            .le(99.0)
            .multiple_of(5.0)
            .min_items(1)
            .max_items(10)
            .unique_items(true)
            .min_length(2)
            .max_length(50)
            .regex("^[a-z]+$");

        let desc = fb.build("fallback_name");
        assert_eq!(desc.name, "custom_name");
        assert_eq!(desc.description, Some("A description".to_string()));
        assert_eq!(desc.default, Some(json!(42)));
        assert_eq!(desc.constraints.format, Some("uri".to_string()));
        assert_eq!(desc.constraints.exclusive_minimum, Some(0.0));
        assert_eq!(desc.constraints.minimum, Some(1.0));
        assert_eq!(desc.constraints.exclusive_maximum, Some(100.0));
        assert_eq!(desc.constraints.maximum, Some(99.0));
        assert_eq!(desc.constraints.multiple_of, Some(5.0));
        assert_eq!(desc.constraints.min_items, Some(1));
        assert_eq!(desc.constraints.max_items, Some(10));
        assert_eq!(desc.constraints.unique_items, Some(true));
        assert_eq!(desc.constraints.min_length, Some(2));
        assert_eq!(desc.constraints.max_length, Some(50));
        assert_eq!(desc.constraints.regex, Some("^[a-z]+$".to_string()));
    }

    #[test]
    fn test_field_builder_name_fallback() {
        let fb = super::FieldBuilder::new()
            .description("no explicit name");
        let desc = fb.build("fallback");
        assert_eq!(desc.name, "fallback");
    }

    #[test]
    fn test_field_builder_default() {
        let fb = super::FieldBuilder::default();
        let desc = fb.build("test");
        assert_eq!(desc.name, "test");
        assert!(desc.description.is_none());
        assert!(desc.default.is_none());
        assert!(desc.constraints.is_empty());
    }

    // ================================================================
    // Schema generation tests
    // ================================================================

    #[test]
    fn test_schema_with_required_fields() {
        // DivFixedSize has "type" and "value" as required fields
        let fixed_size = DivFixedSize::new();
        let schema = fixed_size.schema(None);
        let required = schema["required"].as_array().unwrap();
        let required_strs: Vec<&str> = required
            .iter()
            .map(|v: &Value| v.as_str().unwrap())
            .collect();
        assert!(required_strs.contains(&"type"));
        assert!(required_strs.contains(&"value"));
    }

    #[test]
    fn test_schema_type_field_has_enum() {
        let text = DivText::new();
        let schema = text.schema(None);
        let type_prop = &schema["properties"]["type"];
        assert_eq!(type_prop["type"], "string");
        let enum_vals = type_prop["enum"].as_array().unwrap();
        assert_eq!(enum_vals.len(), 1);
        assert_eq!(enum_vals[0], "text");
    }

    #[test]
    fn test_schema_non_typed_entity() {
        let insets = DivEdgeInsets::new();
        let schema = insets.schema(None);
        assert_eq!(schema["type"], "object");
        let props = schema["properties"].as_object().unwrap();
        assert!(props.contains_key("left"));
        assert!(props.contains_key("top"));
        assert!(props.contains_key("right"));
        assert!(props.contains_key("bottom"));
        // No "type" field in non-typed entity
        assert!(!props.contains_key("type"));
    }

    #[test]
    fn test_schema_generator_new() {
        let gen = super::schema::SchemaGenerator::new();
        assert!(gen.definitions.is_empty());

        let gen_default = super::schema::SchemaGenerator::default();
        assert!(gen_default.definitions.is_empty());
    }

    #[test]
    fn test_schema_field_type_to_json() {
        use super::schema::SchemaFieldType;
        let mut defs = std::collections::HashMap::new();

        assert_eq!(
            SchemaFieldType::Integer.to_json(&mut defs),
            json!({"type": "integer"})
        );
        assert_eq!(
            SchemaFieldType::Number.to_json(&mut defs),
            json!({"type": "number"})
        );
        assert_eq!(
            SchemaFieldType::String.to_json(&mut defs),
            json!({"type": "string"})
        );
        assert_eq!(
            SchemaFieldType::Boolean.to_json(&mut defs),
            json!({"type": "boolean"})
        );
        assert_eq!(
            SchemaFieldType::Expr.to_json(&mut defs),
            json!({"type": "string", "pattern": "^@\\{.*\\}$"})
        );
        assert_eq!(
            SchemaFieldType::Object.to_json(&mut defs),
            json!({"type": "object", "additionalProperties": true})
        );
        assert_eq!(
            SchemaFieldType::Any.to_json(&mut defs),
            json!({})
        );
    }

    #[test]
    fn test_schema_field_type_array() {
        use super::schema::SchemaFieldType;
        let mut defs = std::collections::HashMap::new();
        let arr_type = SchemaFieldType::Array(Box::new(SchemaFieldType::String));
        let result = arr_type.to_json(&mut defs);
        assert_eq!(result["type"], "array");
        assert_eq!(result["items"]["type"], "string");
    }

    #[test]
    fn test_schema_field_type_enum() {
        use super::schema::SchemaFieldType;
        let mut defs = std::collections::HashMap::new();
        let enum_type = SchemaFieldType::Enum(vec!["a".into(), "b".into()]);
        let result = enum_type.to_json(&mut defs);
        assert_eq!(result["type"], "string");
        assert_eq!(result["enum"], json!(["a", "b"]));
    }

    #[test]
    fn test_schema_field_type_anyof() {
        use super::schema::SchemaFieldType;
        let mut defs = std::collections::HashMap::new();
        let anyof_type = SchemaFieldType::AnyOf(vec![
            SchemaFieldType::Integer,
            SchemaFieldType::String,
        ]);
        let result = anyof_type.to_json(&mut defs);
        let any_of = result["anyOf"].as_array().unwrap();
        assert_eq!(any_of.len(), 2);
        assert_eq!(any_of[0]["type"], "integer");
        assert_eq!(any_of[1]["type"], "string");
    }

    #[test]
    fn test_schema_field_type_ref() {
        use super::schema::SchemaFieldType;
        let mut defs = std::collections::HashMap::new();
        let ref_type = SchemaFieldType::Ref("DivText".into());
        let result = ref_type.to_json(&mut defs);
        assert_eq!(result["$ref"], "#/definitions/DivText");
    }

    #[test]
    fn test_schema_with_description_and_default() {
        use super::schema::SchemaGenerator;

        let descriptors = vec![
            super::FieldDescriptor::new("color")
                .with_description("The background color")
                .with_default(json!("#ffffff")),
        ];

        let mut gen = SchemaGenerator::new();
        let schema = gen.build_entity_schema(&descriptors, None);
        let color_prop = &schema["properties"]["color"];
        assert_eq!(color_prop["description"], "The background color");
        assert_eq!(color_prop["default"], "#ffffff");
    }

    #[test]
    fn test_schema_with_constraints() {
        use super::schema::SchemaGenerator;

        let mut constraints = super::Constraints::default();
        constraints.minimum = Some(0.0);
        constraints.maximum = Some(100.0);

        let descriptors = vec![
            super::FieldDescriptor::new("value")
                .with_constraints(constraints),
        ];

        let mut gen = SchemaGenerator::new();
        let schema = gen.build_entity_schema(&descriptors, None);
        let val_prop = &schema["properties"]["value"];
        assert_eq!(val_prop["minimum"], 0.0);
        assert_eq!(val_prop["maximum"], 100.0);
    }

    #[test]
    fn test_schema_empty_descriptors() {
        use super::schema::SchemaGenerator;

        let mut gen = SchemaGenerator::new();
        let schema = gen.build_entity_schema(&[], None);
        assert_eq!(schema["type"], "object");
        // No properties key when empty
        assert!(schema.get("properties").is_none());
    }

    // ================================================================
    // Entity field_descriptors / field_values tests
    // ================================================================

    #[test]
    fn test_typed_entity_field_descriptors() {
        let text = DivText::new();
        let descs = text.field_descriptors();
        let names: Vec<&str> = descs.iter().map(|d| d.name.as_str()).collect();
        // First field should be "type" for typed entities
        assert_eq!(names[0], "type");
        assert!(names.contains(&"text"));
        assert!(names.contains(&"font_size"));
        assert!(names.contains(&"width"));
        assert!(names.contains(&"height"));
    }

    #[test]
    fn test_untyped_entity_field_descriptors() {
        let insets = DivEdgeInsets::new();
        let descs = insets.field_descriptors();
        let names: Vec<&str> = descs.iter().map(|d| d.name.as_str()).collect();
        assert!(names.contains(&"left"));
        assert!(names.contains(&"top"));
        assert!(names.contains(&"right"));
        assert!(names.contains(&"bottom"));
        // No "type" field for untyped entity
        assert!(!names.contains(&"type"));
    }

    #[test]
    fn test_field_values_only_set_fields() {
        let text = DivText::new().text("hello").font_size(16);
        let values = text.field_values();
        let names: Vec<&str> = values.iter().map(|(n, _)| n.as_str()).collect();
        // Should contain type (always present for typed entities), text, font_size
        assert!(names.contains(&"type"));
        assert!(names.contains(&"text"));
        assert!(names.contains(&"font_size"));
        // Should NOT contain unset fields
        assert!(!names.contains(&"font_weight"));
        assert!(!names.contains(&"alpha"));
    }

    #[test]
    fn test_untyped_field_values_empty() {
        let insets = DivEdgeInsets::new();
        let values = insets.field_values();
        // No fields set, no type field
        assert!(values.is_empty());
    }

    #[test]
    fn test_null_fields_omitted_from_dict() {
        let text = DivText::new().text("only text");
        let result = text.dict();
        // Only "type" and "text" should be present
        let obj = result.as_object().unwrap();
        assert_eq!(obj.len(), 2);
        assert!(obj.contains_key("type"));
        assert!(obj.contains_key("text"));
    }

    // ================================================================
    // DivDataState deterministic output
    // ================================================================

    #[test]
    fn test_div_data_state_sorted_keys() {
        let text = DivText::new().text("sort test");
        let state = super::DivDataState {
            state_id: 5,
            div: Box::new(text),
        };
        let val = state.to_div_value();
        // The Map entries should be sorted: "div" before "state_id"
        if let DivValue::Map(entries) = val {
            assert_eq!(entries[0].0, "div");
            assert_eq!(entries[1].0, "state_id");
        } else {
            panic!("Expected DivValue::Map");
        }
    }

    // ================================================================
    // Complex integration tests
    // ================================================================

    #[test]
    fn test_container_with_all_common_fields() {
        let container = DivContainer::new()
            .orientation(DivContainerOrientation::Vertical)
            .width(DivMatchParentSize::new())
            .height(DivWrapContentSize::new())
            .alpha(0.95)
            .visibility(DivVisibility::Visible)
            .paddings(DivEdgeInsets::new().left(8).right(8).top(4).bottom(4))
            .margins(DivEdgeInsets::new().top(16))
            .border(DivBorder::new()
                .corner_radius(12)
                .has_shadow(true)
                .stroke(DivStroke::new().color("#ddd").width(1)))
            .background(vec![DivValue::from(DivSolidBackground::new().color("#fff"))])
            .id("main_container")
            .items(vec![DivValue::from(DivText::new().text("content"))]);

        let result = container.dict();
        assert_eq!(result["type"], "container");
        assert_eq!(result["orientation"], "vertical");
        assert_eq!(result["width"]["type"], "match_parent");
        assert_eq!(result["height"]["type"], "wrap_content");
        assert_eq!(result["alpha"], 0.95);
        assert_eq!(result["visibility"], "visible");
        assert_eq!(result["paddings"]["left"], 8);
        assert_eq!(result["margins"]["top"], 16);
        assert_eq!(result["border"]["corner_radius"], 12);
        assert_eq!(result["border"]["has_shadow"], true);
        assert_eq!(result["border"]["stroke"]["color"], "#ddd");
        assert_eq!(result["id"], "main_container");
    }

    #[test]
    fn test_text_with_expr_field() {
        let expr = Expr::new("@{title_text}").unwrap();
        let text = DivText::new().text(DivValue::Expr(expr));
        let result = text.dict();
        assert_eq!(result["type"], "text");
        assert_eq!(result["text"], "@{title_text}");
    }

    #[test]
    fn test_pager_with_indicator() {
        let pager = DivPager::new()
            .items(vec![
                DivValue::from(DivText::new().text("Page 1")),
                DivValue::from(DivText::new().text("Page 2")),
                DivValue::from(DivText::new().text("Page 3")),
            ]);

        let indicator = DivIndicator::new()
            .pager_id("my_pager")
            .active_item_color("#0000ff")
            .inactive_item_color("#cccccc");

        let container = DivContainer::new()
            .items(vec![
                DivValue::from(pager),
                DivValue::from(indicator),
            ]);

        let result = container.dict();
        assert_eq!(result["items"][0]["type"], "pager");
        assert_eq!(result["items"][0]["items"].as_array().unwrap().len(), 3);
        assert_eq!(result["items"][1]["type"], "indicator");
        assert_eq!(result["items"][1]["pager_id"], "my_pager");
    }

    #[test]
    fn test_build_entity_method() {
        let text = DivText::new().text("build entity test").build_entity();
        let result = text.dict();
        assert_eq!(result["text"], "build entity test");
    }

    #[test]
    fn test_default_trait_for_entities() {
        let text = DivText::default();
        let result = text.dict();
        // Default should produce entity with only type field
        assert_eq!(result["type"], "text");
        let obj = result.as_object().unwrap();
        assert_eq!(obj.len(), 1);

        let insets = DivEdgeInsets::default();
        let result = insets.dict();
        let obj = result.as_object().unwrap();
        assert_eq!(obj.len(), 0);
    }

    // ================================================================
    // Rich schema generation tests
    // ================================================================

    #[test]
    fn test_rich_schema_container_orientation_has_enum() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let orientation = &schema["properties"]["orientation"];
        // orientation supports expressions, so it should be anyOf
        let any_of = orientation["anyOf"].as_array().unwrap();
        assert!(any_of.len() >= 2);
        // First should be an enum
        let enum_vals = any_of[0]["enum"].as_array().unwrap();
        let vals: Vec<&str> = enum_vals.iter().map(|v| v.as_str().unwrap()).collect();
        assert!(vals.contains(&"vertical"));
        assert!(vals.contains(&"horizontal"));
        assert!(vals.contains(&"overlap"));
    }

    #[test]
    fn test_rich_schema_container_items_is_array() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let items = &schema["properties"]["items"];
        assert_eq!(items["type"], "array");
        // items should contain anyOf with refs to div types
        let items_inner = &items["items"];
        assert!(items_inner["anyOf"].is_array());
    }

    #[test]
    fn test_rich_schema_container_alpha_is_number() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let alpha = &schema["properties"]["alpha"];
        // alpha supports expressions, so anyOf [number, expr]
        let any_of = alpha["anyOf"].as_array().unwrap();
        let types: Vec<&str> = any_of.iter().filter_map(|v| v["type"].as_str()).collect();
        assert!(types.contains(&"number"));
    }

    #[test]
    fn test_rich_schema_container_alpha_constraints() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let alpha = &schema["properties"]["alpha"];
        // Should have minimum and maximum constraints
        assert_eq!(alpha["minimum"], 0.0);
        assert_eq!(alpha["maximum"], 1.0);
    }

    #[test]
    fn test_rich_schema_container_alpha_default() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let alpha = &schema["properties"]["alpha"];
        assert_eq!(alpha["default"], 1.0);
    }

    #[test]
    fn test_rich_schema_container_clip_to_bounds_is_boolean() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let clip = &schema["properties"]["clip_to_bounds"];
        // clip_to_bounds supports expressions, so anyOf [boolean, expr]
        let any_of = clip["anyOf"].as_array().unwrap();
        let types: Vec<&str> = any_of.iter().filter_map(|v| v["type"].as_str()).collect();
        assert!(types.contains(&"boolean"));
    }

    #[test]
    fn test_rich_schema_descriptions_present() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let alpha_desc = schema["properties"]["alpha"]["description"].as_str();
        assert!(alpha_desc.is_some());
        assert!(alpha_desc.unwrap().contains("transparent"));
    }

    #[test]
    fn test_rich_schema_exclude_fields_still_works() {
        let container = DivContainer::new();
        let schema = container.schema(Some(&["accessibility", "actions", "alpha"]));
        let props = schema["properties"].as_object().unwrap();
        assert!(!props.contains_key("accessibility"));
        assert!(!props.contains_key("actions"));
        assert!(!props.contains_key("alpha"));
        // But other fields should still be present
        assert!(props.contains_key("orientation"));
        assert!(props.contains_key("items"));
    }

    #[test]
    fn test_rich_schema_text_has_descriptions() {
        let text = DivText::new();
        let schema = text.schema(None);
        let text_desc = schema["properties"]["text"]["description"].as_str();
        assert!(text_desc.is_some());
    }

    #[test]
    fn test_rich_schema_container_column_span_integer() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        let col_span = &schema["properties"]["column_span"];
        // column_span supports expressions, so anyOf [integer, expr]
        let any_of = col_span["anyOf"].as_array().unwrap();
        let types: Vec<&str> = any_of.iter().filter_map(|v| v["type"].as_str()).collect();
        assert!(types.contains(&"integer"));
    }

    #[test]
    fn test_rich_schema_ref_resolution_produces_definitions() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        // The schema should have definitions for referenced entities
        let definitions = schema["definitions"].as_object();
        assert!(definitions.is_some());
        let defs = definitions.unwrap();
        // DivAccessibility should be resolved
        assert!(defs.contains_key("DivAccessibility"));
        // Resolved definitions should be objects with properties
        let acc_def = &defs["DivAccessibility"];
        assert_eq!(acc_def["type"], "object");
        assert!(acc_def["properties"].is_object());
    }

    #[test]
    fn test_rich_schema_fixed_size_value_has_constraints() {
        let fixed = DivFixedSize::new();
        let schema = fixed.schema(None);
        let value_prop = &schema["properties"]["value"];
        // value should have minimum: 0 constraint
        assert_eq!(value_prop["minimum"], 0.0);
    }

    #[test]
    fn test_rich_schema_container_defaults_present() {
        let container = DivContainer::new();
        let schema = container.schema(None);
        // orientation default should be "vertical"
        assert_eq!(
            schema["properties"]["orientation"]["default"],
            "vertical"
        );
        // visibility default should be "visible"
        assert_eq!(
            schema["properties"]["visibility"]["default"],
            "visible"
        );
    }

    #[test]
    fn test_rich_schema_edge_insets_no_type_field() {
        let insets = DivEdgeInsets::new();
        let schema = insets.schema(None);
        assert_eq!(schema["type"], "object");
        let props = schema["properties"].as_object().unwrap();
        // Should not have "type" in properties
        assert!(!props.contains_key("type"));
        // All fields should be present with rich types
        assert!(props.contains_key("left"));
        assert!(props.contains_key("top"));
        assert!(props.contains_key("right"));
        assert!(props.contains_key("bottom"));
    }

    #[test]
    fn test_schema_registry_lookup() {
        use super::generated::schema_registry::entity_field_descriptors;
        // Known entities should be found
        let descs = entity_field_descriptors("DivText");
        assert!(descs.is_some());
        let descs = descs.unwrap();
        let names: Vec<&str> = descs.iter().map(|d| d.name.as_str()).collect();
        assert!(names.contains(&"type"));
        assert!(names.contains(&"text"));

        // Unknown entities return None
        assert!(entity_field_descriptors("NonExistentEntity").is_none());
    }
}
