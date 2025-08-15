# entity_with_property_with_default_value
Описания пока нет.

## Параметры
| Параметры | Описание |
| --- | --- |
| `type` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение всегда должно равняться `entity_with_property_with_default_value`.</p> |
| `int` | <p>**int**</p><p>Ограничение для значения `x`: `x >= 0`.</p><p>Значение по умолчанию: `0`.</p> |
| `nested` | <p>**object**</p><p>non_optional используется, чтобы запретить автогенерацию дефолтного значения для объекта, все свойства которого опциональны.</p><p>Значение имеет тип `nested`.</p> |
| `url` | <p>**string**</p><p>Значение должно быть валидным URL.</p><p>Разрешенные схемы: `https`.</p><p>Значение по умолчанию: `https://yandex.ru`.</p> |

### nested
| Параметры | Описание |
| --- | --- |
| `non_optional` | <p>**string**</p><p>Обязательный параметр.</p> |
| `int` | <p>**int**</p><p>Ограничение для значения `x`: `x >= 0`.</p><p>Значение по умолчанию: `0`.</p> |
| `url` | <p>**string**</p><p>Значение должно быть валидным URL.</p><p>Разрешенные схемы: `https`.</p><p>Значение по умолчанию: `https://yandex.ru`.</p> |

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
