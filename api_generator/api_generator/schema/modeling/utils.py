from typing import Dict, Optional, List

from ...config import GeneratedLanguage, Platform, DescriptionLanguage
from ..utils import get_value_with_optional_by_lang


def generate_cases_for_templates(lang: GeneratedLanguage, dictionary: Dict[str, any]) -> bool:
    return get_value_with_optional_by_lang('generate_case_for_templates', lang, dictionary) or False


def alias(lang: GeneratedLanguage, dictionary: Dict[str, any]) -> Optional[str]:
    return get_value_with_optional_by_lang('alias', lang, dictionary)


def fixing_reserved_typename(typename: str, lang: GeneratedLanguage) -> str:
    if lang is GeneratedLanguage.SWIFT:
        return 'kind' if typename.lower() == 'type' else typename
    else:
        return typename


def platforms(dictionary: Dict[str, any]) -> Optional[List[Platform]]:
    platforms_list: List[str] = dictionary.get('platforms')
    if platforms_list is None:
        return None
    return list(map(lambda raw: Platform[raw.upper()], platforms_list))


def description_doc(description_translations: Dict[str, str], lang: DescriptionLanguage, description: str) -> str:
    try:
        return description_translations[lang.value]
    except KeyError:
        return description
