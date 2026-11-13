# entity_with_array
Описания пока нет.

## Параметры
| Параметры | Описание |
| --- | --- |
| `array` | <p>**array**</p><p>Обязательный параметр.</p><p>Массив должен быть непустым.</p><p>Доступные платформы: Android, iOS, веб.</p><p>Список возможных значений:<li>[entity_with_array](entity_with_array.md)</li><li>[entity_with_array_of_enums](entity_with_array_of_enums.md)</li><li>[entity_with_array_of_expressions](entity_with_array_of_expressions.md)</li><li>[entity_with_array_of_nested_items](entity_with_array_of_nested_items.md)</li><li>[entity_with_array_with_transform](entity_with_array_with_transform.md)</li><li>[entity_with_complex_property](entity_with_complex_property.md)</li><li>[entity_with_complex_property_with_default_value](entity_with_complex_property_with_default_value.md)</li><li>[entity_with_entity_property](entity_with_entity_property.md)</li><li>[entity_with_optional_complex_property](entity_with_optional_complex_property.md)</li><li>[entity_with_optional_property](entity_with_optional_property.md)</li><li>[entity_with_optional_string_enum_property](entity_with_optional_string_enum_property.md)</li><li>[entity_with_property_with_default_value](entity_with_property_with_default_value.md)</li><li>[entity_with_raw_array](entity_with_raw_array.md)</li><li>[entity_with_required_property](entity_with_required_property.md)</li><li>[entity_with_simple_properties](entity_with_simple_properties.md)</li><li>[entity_with_string_array_property](entity_with_string_array_property.md)</li><li>[entity_with_string_enum_property](entity_with_string_enum_property.md)</li><li>[entity_with_string_enum_property_with_default_value](entity_with_string_enum_property_with_default_value.md)</li><li>[entity_without_properties](entity_without_properties.md)</li></p> |
| `type` | <p>**string**</p><p>Обязательный параметр.</p><p>Значение всегда должно равняться `entity_with_array`.</p><p>Доступные платформы: Android, iOS, веб.</p> |

<details>
<summary>JSON</summary>

```json
{
  type*: "entity_with_array",
  array*: [ entity, ... ]
}
```
</details>
