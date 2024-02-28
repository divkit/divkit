# entity_with_property_with_default_value
No description yet.

## Parameters
| Parameters | Description |
| --- | --- |
| `type` | <p>**string**</p><p>Required parameter.</p><p>The value must always be `entity_with_property_with_default_value`.</p> |
| `int` | <p>**int**</p><p>Restriction for the value `x`: `x >= 0`.</p><p>Default value: `0`.</p> |
| `nested` | <p>**object**</p><p>non_optional is used to suppress auto-generation of default value for object with all-optional fields.</p><p>The value has the type `nested`.</p> |
| `url` | <p>**string**</p><p>The value must be a valid URL.</p><p>Allowed schemes: `https`.</p><p>Default value: `https://yandex.ru`.</p> |

### nested
| Parameters | Description |
| --- | --- |
| `non_optional` | <p>**string**</p><p>Required parameter.</p> |
| `int` | <p>**int**</p><p>Restriction for the value `x`: `x >= 0`.</p><p>Default value: `0`.</p> |
| `url` | <p>**string**</p><p>The value must be a valid URL.</p><p>Allowed schemes: `https`.</p><p>Default value: `https://yandex.ru`.</p> |

<details>
<summary>JSON</summary>

```json
{
  type*: "entity_with_property_with_default_value",
  int: "int",
  nested: {
    int: "int",
    non_optional*: "string",
    url: "string"
  },
  url: "string"
}
```
</details>
