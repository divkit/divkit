# entity_with_complex_property_with_default_value
No description yet.

## Parameters
| Parameters | Description |
| --- | --- |
| `type` | <p>**string**</p><p>Required parameter.</p><p>The value must always be `entity_with_complex_property_with_default_value`.</p> |
| `property` | <p>**object**</p><p>The value has the type `property`.</p><p>Default value: `{ "value": "Default text" }`.</p> |

### property
| Parameters | Description |
| --- | --- |
| `value` | <p>**string**</p><p>Required parameter.</p> |

<details>
<summary>JSON</summary>

```json
{
  type*: "entity_with_complex_property_with_default_value",
  property: {
    value*: "string"
  }
}
```
</details>
