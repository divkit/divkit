# entity_with_complex_property_with_default_value
Описания пока нет.

## Параметры
| Параметры | Описание |
| --- | --- |
| `type` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение всегда должно равняться `entity_with_complex_property_with_default_value`.</p> |
| `property` | <p>**object**</p><p>Значение имеет тип `complex_property`.</p><p>Значение по умолчанию: `{ "value": "Default text" }`.</p> |

### complex_property
| Параметры | Описание |
| --- | --- |
| `value` | <p>**string**</p><p>Обязательный параметр.</p> |

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
