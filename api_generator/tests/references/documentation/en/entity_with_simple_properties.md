# entity_with_simple_properties
Entity with simple properties.

## Parameters
| Parameters | Description |
| --- | --- |
| `type` | <p>**string**</p><p>Required parameter.</p><p>The value must always be `entity_with_simple_properties`.</p> |
| `boolean` | <p>**bool**</p><p>Boolean property.</p> |
| `boolean_int` | <p>**bool_int**</p><p>Parameter is deprecated.</p><p>Boolean value in numeric format.</p><p>Available platforms: Android.</p> |
| `color` | <p>**string**</p><p>Color.</p><p>Valid formats: `#RGB`, `#ARGB`, `#RRGGBB`, `#AARRGGBB`.</p> |
| `double` | <p>**number**</p><p>Floating point number.</p> |
| `id` | <p>**int**</p><p>ID. Can't contain expressions.</p><p>Default value: `0`.</p> |
| `integer` | <p>**int**</p><p>Integer.</p><p>Default value: `0`.</p> |
| `positive_integer` | <p>**int**</p><p>Positive integer.</p><p>Restriction for the value `x`: `x > 0`.</p><p>Functionality is under development.</p> |
| `string` | <p>**string**</p><p>String.</p><p>Available platforms: Android, iOS, web.</p> |
| `url` | <p>**string**</p><p>The value must be a valid URL.</p> |

<details>
<summary>JSON</summary>

```json
{
  type*: "entity_with_simple_properties",
  boolean: "bool",
  boolean_int: "bool_int",
  color: "string",
  double: "number",
  id: "int",
  integer: "int",
  positive_integer: "int",
  string: "string",
  url: "string"
}
```
</details>
