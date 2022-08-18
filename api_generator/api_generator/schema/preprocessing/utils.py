from typing import Dict, List, TypeVar, Optional
from copy import deepcopy

from . import errors
from .entities import ElementLocation, SchemaFile
from ...config import GeneratedLanguage

Key = TypeVar('T')
Value = TypeVar('V')


def enclosed_dict_for(keys: List[Key], dictionary: Dict[Key, any]) -> Dict[Key, any]:
    if not keys:
        return dictionary
    d = deepcopy(dictionary)
    for key in keys:
        d = d.get(key)
    return d


def get_value_with_optional_by_lang(key: str, lang: GeneratedLanguage, dictionary: Dict[str, any]) -> Optional[Value]:
    if key in dictionary:
        return dictionary[key]
    return dictionary.get(f'{key}_{lang.value}')


def is_list_of_type(value, list_type) -> bool:
    return isinstance(value, List) and all(isinstance(element, list_type) for element in value)


def number_of_references(obj_name: str, dictionary: Dict[str, any]) -> int:
    result = 0
    for item in dictionary.values():
        if isinstance(item, Dict):
            result += number_of_references(obj_name, item)
        elif is_list_of_type(item, list_type=Dict):
            result += sum(map(lambda element: number_of_references(obj_name=obj_name, dictionary=element), item))

    ref = dictionary.get('$ref')
    if isinstance(ref, str) and ref.endswith('/' + obj_name):
        result += 1

    def_type = dictionary.get('type')
    if isinstance(def_type, str) and def_type == f'$defined_{obj_name}':
        result += 1

    return result


def code_generation_disabled(lang: GeneratedLanguage, dictionary: Dict[str, any]) -> bool:
    value = get_value_with_optional_by_lang('code_generation_disabled', lang, dictionary)
    return value or False


def get_full_reference_location(location: ElementLocation, ref: str) -> (str, SchemaFile, List[str]):
    invalid_reference_error = errors.InvalidFileReferenceError(location, ref)
    components: List[str] = ref.split('#')

    if len(components) > 2:
        raise invalid_reference_error

    if len(components) == 2 and not components[0]:
        path: List[str] = list(filter(None, components[1].split('/')))
        return '', location.file, path

    try:
        parent_directory = location.file.parent_dir
        if parent_directory is None:
            raise invalid_reference_error
        ref = parent_directory.resolve(components[0])
        if not isinstance(ref, SchemaFile):
            raise invalid_reference_error

        if len(components) == 2:
            path: List[str] = list(filter(None, components[1].split('/')))
        else:
            path: List[str] = []

        ref_dir_path = '/'.join(components[0].split('/')[:-1])
        prefix = '' if not ref_dir_path else ref_dir_path + '/'
        return prefix, ref, path
    except errors.InvalidReferenceError:
        raise invalid_reference_error
