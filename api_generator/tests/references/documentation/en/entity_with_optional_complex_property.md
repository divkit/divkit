# entity_with_optional_complex_property
No description yet.

## Parameters
| Parameters | Description |
| --- | --- |
| `type` | <p>**string**</p><p>Required parameter.</p><p>The value must always be `entity_with_optional_complex_property`.</p> |
| `property` | <p>**object**</p><p>The value has the type `property`.</p> |

### property
| Parameters | Description |
| --- | --- |
| `value` | <p>**string**</p><p>Required parameter.</p><p>The value must be a valid URL.</p> |

<details>
<summary>JSON</summary>

```json
{
  type*: "entity_with_optional_complex_property",
  property: {
    value*: "string"
  }
}
```
</details>
