# entity_with_simple_properties
Объект с простыми свойствами.

## Параметры
| Параметры | Описание |
| --- | --- |
| `type` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение всегда должно равняться `entity_with_simple_properties`.</p> |
| `boolean` | <p>**bool**</p><p>Логическое значение.</p> |
| `boolean_int` | <p>**bool_int**</p><p>Параметр устарел.</p><p>Логическое значение в числовом формате.</p><p>Доступные платформы: Android.</p> |
| `color` | <p>**string**</p><p>Цвет.</p><p>Допустимые форматы: `#RGB`, `#ARGB`, `#RRGGBB`, `#AARRGGBB`.</p> |
| `double` | <p>**number**</p><p>Число с плавающей точкой.</p> |
| `id` | <p>**int**</p><p>Идентификатор. Не может содержать выражение.</p><p>Значение по умолчанию: `0`.</p> |
| `integer` | <p>**int**</p><p>Целое число.</p><p>Значение по умолчанию: `0`.</p> |
| `positive_integer` | <p>**int**</p><p>Положительное целое число.</p><p>Ограничение для значения `x`: `x > 0`.</p><p>Функциональность находится в разработке.</p> |
| `string` | <p>**string**</p><p>Строка.</p><p>Доступные платформы: Android, iOS, веб.</p> |
| `url` | <p>**string**</p><p>Значение должно быть валидным URL.</p> |

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
