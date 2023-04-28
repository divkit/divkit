# entity_with_simple_properties
Объект с простыми свойствами.

## JSON
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

## Параметры
| Параметры | Описание |
| --- | --- |
| `boolean` | <p>**bool**</p><p>Логическое значение.</p> |
| `boolean_int` | <p>**bool_int**</p><p>Параметр устарел.</p><p>Логическое значение в числовом формате.</p><p>Доступные платформы: Android.</p> |
| `color` | <p>**string**</p><p>Цвет.</p><p>Допустимые форматы: `#RGB`, `#ARGB`, `#RRGGBB`, `#AARRGGBB`.</p> |
| `double` | <p>**number**</p><p>Число с плавающей точкой.</p> |
| `id` | <p>**int**</p><p>Идентификатор. Не может содержать выражение.</p> |
| `integer` | <p>**int**</p><p>Целое число.</p> |
| `positive_integer` | <p>**int**</p><p>Положительное целое число.</p><p>Ограничение для значения `x`: `x > 0`.</p><p>Функциональность находится в разработке.</p> |
| `string` | <p>**string**</p><p>Строка.</p><p>Строка должна быть непустой.</p><p>Доступные платформы: Android, iOS, веб.</p> |
| `type` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение всегда должно равняться `entity_with_simple_properties`.</p> |
| `url` | <p>**string**</p><p>Значение должно быть валидным URL.</p> |
