# Project Description

This is the DivKit project repository.
DivKit is a backend-driven UI framework.
Its main goal is to create a platform view (iOS, Android, Web, Flutter) based on markup in JSON format.

## DivKit JSON Structure

A correct DivKit JSON should have the following structure:

```json
{
  "templates": {
    // Div element templates (optional)
  },
  "card": {
    // div-data (required)
  }
}
```

### Using Templates

1. Templates are defined in the `templates` section and used in `div` by specifying `type` equal to the template name.
2. Template properties are marked with a `$` prefix in the property name (not in the value).

```json
{
  "templates": {
    "title": {
      "type": "text",
      "font_size": 18,
      "$text": "title_text"
    }
  },
  "card": {
    "log_id": "sample_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "container",
          "items": [
            {
              "type": "title",
              "title_text": "Hello, DivKit!"
            },
            {
              "type": "title",
              "title_text": "This is a DivKit card example"
            }
          ]
        }
      }
    ]
  }
}
```

## Using Schema for Correct Element Properties

It's crucial to always check the schema files to ensure correct property types and values of every element. The schema files are located in the `schema/` directory. If the usage of an element is not completely clear, you should check its description in the translations.json file.

**IMPORTANT:** Always verify that your DivKit JSON structure matches the schema requirements at all levels of object nesting. This includes checking field types (string, number, object, array, etc.) and ensuring all required fields are present at every level of the JSON hierarchy. Each nested object must conform to its corresponding schema definition. If properties defined by objects are used in the layout, you need to check the schema of each such object. Failure to follow the schema at any nesting level will result in rendering errors or unexpected behavior across platforms. Schema validation should be your first step when troubleshooting issues.

The schema files define:
- Required properties for each element
- Correct data types for properties
- Allowed values for enum properties
- Default values for optional properties

If you encounter errors when creating a DivKit layout, always verify your element properties against the schema. Common issues include:
- Using incorrect data types (e.g., using a number instead of an object)
- Missing required properties
- Using invalid enum values
- Incorrect action formats
- Nested objects that don't conform to their respective schema definitions

Remember that some properties require complex object structures rather than simple values. Always check the schema when in doubt, and ensure that all nested objects within your JSON structure conform to their respective schema definitions.

## Recommendations for Creating Correct DivKit JSON

1. Always check the div-data schema to understand the field types and required fields.
2. Check the div.json file to know which elements exist.
3. Always verify the schema of needed elements and all objects used in them to use correct types and required properties.
4. Always specify required fields for each element type.
5. Use correct data types for properties (strings, numbers, objects).
6. Monitor element nesting and their sizes.
7. Use templates for repeating elements.
8. Use variables for values that may change.
9. Verify JSON correctness before use.
10. Consider the specifics of platforms where the UI will be displayed.
11. Ensure that all nested objects conform to their respective schema definitions at every level of the JSON hierarchy.


**IMPORTANT**:
1. Don't wrap single elements in containers.
2. Ensure that containers with `wrap_content` don't contain elements with `match_parent` along the main axis.
3. Use unique identifiers for elements and actions.

## DivKit Elements

DivKit supports the following elements:

1. **div-image** — Image.
2. **div-gif-image** — Gif image.
3. **div-text** — Text.
4. **div-separator** — Separator.
5. **div-container** — Container.
6. **div-grid** — Grid.
7. **div-gallery** — Gallery.
8. **div-pager** — Pager.
9. **div-tabs** — Tabs.
10. **div-state** — State.
11. **div-custom** — Host-specific element.
12. **div-indicator** — Progress indicator for pager.
13. **div-slider** — Slider.
14. **div-switch** — Toggle.
15. **div-input** — Text input field.
16. **div-select** — List of options with only one to be selected.
17. **div-video** — Video.

### Element Descriptions

#### div-image
Image. Allows displaying static images with various scaling and alignment settings.

#### div-gif-image
Animated GIF image. Specialized element for displaying GIF animations.

#### div-text
Text. Allows displaying text content with various styles, supports formatting, embedding images, and character ranges with different styles.

#### div-separator
A separating line between elements. Used for visual content separation.

#### div-container
Container. It contains other elements and is responsible for their location. It is used to arrange elements vertically, horizontally, and with an overlay in a certain order, simulating the third dimension.

#### div-grid
A grid with an option to merge cells vertically and horizontally. Allows creating tabular data structures.

#### div-gallery
Gallery. It contains a horizontal or vertical set of cards that can be scrolled. Supports various scrolling modes and element alignments.

#### div-pager
Pager. It contains a horizontal set of cards that can be scrolled page by page. It shows the main page and the beginning of the next one.

#### div-tabs
Tabs. Allow organizing content in the form of tabs. Height of the first tab is determined by its contents, and height of the remaining depends on the platform.

#### div-state
State. It contains sets of states for visual elements and switches between them. Allows creating interactive elements with different visual states.

#### div-custom
Custom element. It is delegated to a host application to create native elements depending on the platform.

#### div-indicator
Progress indicator for pager. Displays the current position in the pager and allows visualizing viewing progress.

#### div-slider
Slider for selecting a value in the range. Allows users to select numeric values in a given range.

#### div-switch
Two-state toggle that allows the user to control a Boolean variable. The element's look-and-feel varies by platform.

#### div-input
Text input element. Allows users to enter text information with various validation and formatting settings.

#### div-select
List of options with only one to be selected. Provides the user with the ability to choose from a predefined list of options.

#### div-video
Video. Allows playing video content with various playback control settings.

## Using Actions

To make the layout interactive, DivKit uses actions. To add an action for an element, you need to use the `actions` property. The `action` property is deprecated and should not be used. An action can be described in one of two ways:

* Using a typed action:
```json
"actions": [
  {
    "log_id": "typed_action",
    "typed": {
      "$ref": "div-action-typed.json"
    }
  }
]
```
The list of action types, as well as their property types and required fields, should be checked in the schema before use.

* Using a URL:
```json
"actions": [
  {
    "log_id": "some_action",
    "url": "div-action://some_action?[param1=value1][&param2=value2]"
  }
]
```
`some_action` corresponds to the name of typed actions, and the parameters correspond to the properties of the respective typed action.

### State Switching with Actions

When using the `set_state` action, the `state_id` parameter contains the full path from the root to the state element:
- For root states, it's just a number (e.g., `state_id=0` or `state_id=1`)
- For nested states, the path includes:
  1. The root state_id (numeric)
  2. The id of the state element
  3. The state_id of the target state (string)

Example for nested states:
```
div-action://set_state?state_id=0/nested_state/selected
```

For deeply nested states, the path would include all pairs of id/state_id along the way.
