from typing import Dict
from ...schema.modeling.entities import DescriptionLanguage

__full_translations: Dict[str, Dict[str, str]] = {
    "div_generator_android": {
        "en": "Android",
        "ru": "Android"
    },
    "div_generator_default_value": {
        "en": "Default value: `{}`.",
        "ru": "Значение по умолчанию: `{}`."
    },
    "div_generator_deprecated_message": {
        "en": "Marked as deprecated in the JSON schema ",
        "ru": "Помечен как устаревший в JSON-схеме "
    },
    "div_generator_description": {
        "en": "Description",
        "ru": "Описание"
    },
    "div_generator_elements_min_number": {
        "en": "The minimum number of elements is {}.",
        "ru": "Минимальное количество элементов — {}."
    },
    "div_generator_entity_enumeration": {
        "en": "Can have one of the following types:",
        "ru": "Может иметь один из следующих типов:"
    },
    "div_generator_factory_method_name": {
        "en": "Can be created using the method [{}].",
        "ru": "Можно создать при помощи метода [{}]."
    },
    "div_generator_html_formatting": {
        "en": "Limited HTML formatting is allowed.",
        "ru": "Допускается ограниченное HTML-форматирование."
    },
    "div_generator_in_progress": {
        "en": "Functionality is under development.",
        "ru": "Функциональность находится в разработке."
    },
    "div_generator_ios": {
        "en": "iOS",
        "ru": "iOS"
    },
    "div_generator_min_length": {
        "en": "Minimum length {}.",
        "ru": "Минимальная длина {}."
    },
    "div_generator_no_description": {
        "en": "No description yet.",
        "ru": "Описания пока нет."
    },
    "div_generator_non_empty_array": {
        "en": "An array must not be empty.",
        "ru": "Массив должен быть непустым."
    },
    "div_generator_non_empty_string": {
        "en": "A string must not be empty.",
        "ru": "Строка должна быть непустой."
    },
    "div_generator_parameter_deprecated": {
        "en": "Parameter is deprecated.",
        "ru": "Параметр устарел."
    },
    "div_generator_parameters": {
        "en": "Parameters",
        "ru": "Параметры"
    },
    "div_generator_platforms": {
        "en": "Available platforms: {}.",
        "ru": "Доступные платформы: {}."
    },
    "div_generator_possible_values": {
        "en": "Possible values: {}.",
        "ru": "Возможные значения: {}."
    },
    "div_generator_required_parameter": {
        "en": "Required parameter.",
        "ru": "Обязательный параметр."
    },
    "div_generator_required_properties": {
        "en": "Required parameters: `{}`.",
        "ru": "Обязательные параметры: `{}`."
    },
    "div_generator_schemes": {
        "en": "Allowed schemes: {}.",
        "ru": "Разрешенные схемы: {}."
    },
    "div_generator_type_deprecated": {
        "en": "Type is deprecated.",
        "ru": "Тип устарел."
    },
    "div_generator_valid_formats_color": {
        "en": "Valid formats: `#RGB`, `#ARGB`, `#RRGGBB`, `#AARRGGBB`.",
        "ru": "Допустимые форматы: `#RGB`, `#ARGB`, `#RRGGBB`, `#AARRGGBB`."
    },
    "div_generator_value_json": {
        "en": "**Attention:** the value is represented as a JSON string, not an object.",
        "ru": "**Внимание:** значение представлено в виде JSON-строки, а не объекта."
    },
    "div_generator_value_must_be": {
        "en": "The value must always be `{}`.",
        "ru": "Значение всегда должно равняться `{}`."
    },
    "div_generator_value_regex": {
        "en": "The value must match the regular expression `{}`.",
        "ru": "Значение должно удовлетворять регулярному выражению `{}`."
    },
    "div_generator_value_restriction": {
        "en": "Restriction for the value `x`: `{}`.",
        "ru": "Ограничение для значения `x`: `{}`."
    },
    "div_generator_value_type": {
        "en": "The value has the type `{}`.",
        "ru": "Значение имеет тип `{}`."
    },
    "div_generator_value_url": {
        "en": "The value must be a valid URL.",
        "ru": "Значение должно быть валидным URL."
    },
    "div_generator_values_list": {
        "en": "List of possible values:",
        "ru": "Список возможных значений:"
    },
    "div_generator_web": {
        "en": "web",
        "ru": "веб"
    }
}


def translations(lang: DescriptionLanguage) -> Dict[str, str]:
    result = dict()
    lang_value = lang.value
    for key, values in __full_translations.items():
        result[key] = values[lang_value]
    return result
