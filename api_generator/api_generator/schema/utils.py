from typing import Dict, List, TypeVar, Optional

from ..config import GeneratedLanguage

Key = TypeVar('T')
Value = TypeVar('V')


def enclosed_dict_for(keys: List[Key], dictionary: Dict[Key, any]) -> Dict[Key, any]:
    if len(keys) == 0:
        return dictionary
    d = dictionary.copy()
    for key in keys:
        d = d.get(key)
    return d


def get_value_with_optional_by_lang(key: str, lang: GeneratedLanguage, dictionary: Dict[str, any]) -> Optional[Value]:
    if key in dictionary:
        return dictionary[key]
    return dictionary.get(f'{key}_{lang.value}')


def code_generation_disabled(lang: GeneratedLanguage, dictionary: Dict[str, any]) -> bool:
    return get_value_with_optional_by_lang('code_generation_disabled', lang, dictionary) or False


def is_list_of_type(value, list_type) -> bool:
    return isinstance(value, List) and all(isinstance(element, list_type) for element in value)


def is_dict_with_keys_of_type(value, key_type) -> bool:
    return isinstance(value, Dict) and all(isinstance(key, key_type) for key in value)
