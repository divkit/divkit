# entity_with_optional_complex_property
Описания пока нет.

## Параметры
| Параметры | Описание |
| --- | --- |
| `type` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение всегда должно равняться `entity_with_optional_complex_property`.</p> |
| `property` | <p>**object**</p><p>Значение имеет тип `property`.</p> |

### property
| Параметры | Описание |
| --- | --- |
| `value` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение должно быть валидным URL.</p> |

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
