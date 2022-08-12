from __future__ import annotations
from typing import Dict, List, Optional
from copy import deepcopy
import os
import json

from ...config import Config, GeneratedLanguage
from ..utils import code_generation_disabled
from .utils import is_list_of_type, Key, Value, number_of_references
from . import errors
from . import entities


def has_unresolved_references(dictionary: Dict) -> bool:
    if isinstance(dictionary.get('$ref'), str):
        return True
    for value in dictionary.values():
        if isinstance(value, Dict) and has_unresolved_references(value):
            return True
        elif is_list_of_type(value, Dict):
            for inner_dict in value:
                if has_unresolved_references(inner_dict):
                    return True
    return False


def prepend_prefix_to_references(prefix: str, dictionary: Dict[Key, any]) -> Dict[Key, Value]:
    if not prefix:
        return dictionary
    result = deepcopy(dictionary)
    reference = result.get('$ref')
    if isinstance(reference, str):
        new_value = prefix + reference
        if type(new_value) is not Value:
            raise errors.InvalidReferenceError
        if not reference.startswith('#/'):
            result['$ref'] = new_value
    else:
        for key, value in dictionary.items():
            if isinstance(value, Dict):
                result[key] = prepend_prefix_to_references(prefix, value)
            elif is_list_of_type(value, list_type=Dict):
                result[key] = list(map(lambda element: prepend_prefix_to_references(prefix, element), value))
    return result


def __merge(d1: Dict[str, any], d2: Dict[str, any], location: entities.ElementLocation):
    result = deepcopy(d1)
    other = d2
    result.pop('allOf', None)
    for key in other:
        if key in result:
            error = errors.AmbiguousMergeError(location=location + key)
            result_val, other_val = result[key], other[key]
            if isinstance(result_val, Dict) and isinstance(other_val, Dict):
                result[key] = __merge(result_val, other_val, location=location + key)

            elif is_list_of_type(result_val, str) and is_list_of_type(other_val, str):
                if set(result_val) & set(other_val):
                    raise error

                result[key] = result_val + other_val
            else:
                raise error
        else:
            result[key] = other[key]

    return result


def merge_all_ofs(dictionary: Dict[str, any], lang: GeneratedLanguage, location: entities.ElementLocation
                  ) -> Dict[str, any]:
    result = deepcopy(dictionary)
    for key in result:
        value = dictionary.get(key)
        if isinstance(value, Dict):
            result[key] = merge_all_ofs(dictionary=value, lang=lang, location=location + key)
        elif is_list_of_type(value, Dict):
            result[key] = list(map(
                lambda element: merge_all_ofs(dictionary=element, lang=lang, location=location + key),
                value
            ))
    if 'allOf' in result:
        if not isinstance(result['allOf'], List):
            raise errors.InvalidAllOfError(location)
        dicts_to_merge: List[Dict[str, any]] = list(filter(
            lambda element: not code_generation_disabled(lang, element),
            result['allOf']
        ))
        for d in dicts_to_merge:
            ref_str = d.get('$ref')
            if isinstance(ref_str, str):
                ref = entities.Reference(location, ref_str)
                value = merge_all_ofs(dictionary=ref.value, lang=lang, location=location)
                result = __merge(d1=result, d2=value, location=location)
            else:
                result = __merge(d1=result, d2=d, location=location)
        result.pop('allOf', None)
    return result


def clean_unused_definitions(dictionary: Dict[str, any]) -> Dict[str, any]:
    definitions: Dict[str, any] = dictionary.get('definitions')
    if definitions is None:
        return dictionary
    used_definitions: Dict[str, any] = dict()
    for name, definition in definitions.items():
        if number_of_references(obj_name=name, dictionary=dictionary) > 0:
            used_definitions[name] = definition
        else:
            print(f'Purging unused definition "{name}"')
    dictionary['definitions'] = used_definitions
    return dictionary


def resolve_references(
    dictionary: Dict[str, any],
    location: entities.ElementLocation,
    lang: GeneratedLanguage,
    reference_stack: List[entities.Reference] = None
) -> Dict[str, any]:
    if reference_stack is None:
        reference_stack = []
    reference = dictionary.get('$ref')

    def loop_str_transform(r: entities.Reference) -> str:
        joined_path = '/'.join(r.path)
        return f'{r.file.name}#{joined_path}'

    if isinstance(reference, str):
        ref = entities.Reference(location=location, ref=reference)
        if ref in reference_stack:
            loop_str = ' -> '.join(map(loop_str_transform, reference_stack + [ref]))
            print(f'Handling circular reference: {loop_str}')

            ref_type = ref.value.get('type')
            if not (isinstance(ref_type, str) and ref_type.startswith('$defined_')):
                raise errors.UnsupportedCircularReferenceError(description=loop_str)
            return ref.value
        result = deepcopy(dictionary)
        result['$resolved_refs'] = result.get('$resolved_refs', []) + [reference]
        result.pop('$ref', None)
        return __merge(d1=result, d2=ref.value, location=location)

    resolved_refs = dictionary.get('$resolved_refs')
    if not is_list_of_type(resolved_refs, list_type=str):
        resolved_refs = []
    new_stack = reference_stack + list(map(lambda it: entities.Reference(location=location, ref=it), resolved_refs))

    resolved_dict = deepcopy(dictionary)
    for key in list(resolved_dict):
        value = resolved_dict.get(key)
        if isinstance(value, Dict):
            if not code_generation_disabled(lang=lang, dictionary=value):
                resolved_dict[key] = resolve_references(
                    dictionary=value,
                    location=location + key,
                    lang=lang,
                    reference_stack=new_stack
                )
            else:
                resolved_dict.pop(key, None)
        elif is_list_of_type(value, list_type=Dict):
            resolved_dict[key] = list(map(
                lambda element: resolve_references(dictionary=element, lang=lang, location=location + key),
                filter(
                    lambda element: not code_generation_disabled(lang=lang, dictionary=element),
                    value
                )
            ))
    return resolved_dict


def internal_resolve_structure(
    path: str,
    lang: GeneratedLanguage,
    parent_dir: Optional[entities.SchemaDirectory] = None
) -> entities.SchemaFilesystemItem:
    print(f'Processing path: {path}')

    if not os.path.exists(path):
        raise errors.PathNotFoundError(path)

    if os.path.isdir(path):
        return entities.SchemaDirectory(path, parent_dir, lang)

    json_data = json.loads(open(path).read())
    if not isinstance(json_data, Dict):
        raise errors.NonDictionaryContentError(path)

    name = os.path.basename(os.path.normpath(path))
    return entities.SchemaFile(name=name, parent_dir=parent_dir, contents=json_data)


def resolve_structure(config: Config) -> entities.SchemaDirectory:
    result = internal_resolve_structure(path=config.schema_path, lang=config.generation.lang)
    if not isinstance(result, entities.SchemaDirectory):
        raise errors.FileAtInputPathError(config.schema_path)
    return result


def schema_preprocessing(config: Config) -> entities.SchemaDirectory:
    root_directory = resolve_structure(config)
    root_directory.merge_all_ofs(config.generation.lang)
    root_directory.resolve_references(config.generation.lang)
    root_directory.clean_unused_definitions()
    return root_directory
