from typing import List, Dict, Tuple, Optional

from ...config import Config, GenerationMode
from ..utils import is_list_of_type, is_dict_with_keys_of_type
from ..preprocessing.entities import SchemaDirectory, SchemaFile, ElementLocation

from . import builders
from .entities import Declarable, StringEnumeration, _build_documentation_generator_properties
from .utils import (
    alias,
    generate_cases_for_templates
)


def __generate_objects(
        file: SchemaFile,
        config: Config.GenerationConfig,
        generated_entities: List[Declarable]
) -> List[Declarable]:
    contents: Dict[str, any] = file.contents
    name: str = file.name.replace('.json', '')
    type_value: Optional[str] = contents.get('type')
    location = ElementLocation(file)
    documentation_properties = _build_documentation_generator_properties(dictionary=contents,
                                                                         location=location,
                                                                         mode=GenerationMode.NORMAL_WITH_TEMPLATES)
    include_in_documentation_toc = False
    if documentation_properties is not None:
        include_in_documentation_toc = documentation_properties.include_in_toc
    if type_value == 'object':
        return builders.entity_build(name=name,
                                     dictionary=contents,
                                     location=location,
                                     config=config)
    elif type_value == 'string' and contents.get('enum') is not None:
        cases: List[str] = contents.get('enum')
        if not is_list_of_type(cases, str):
            raise TypeError(f'Enum at {file.name} must have format [String]')
        return [StringEnumeration(name=alias(config.lang, contents) or name,
                                  original_name=name,
                                  cases=cases,
                                  description=contents.get('description', ''),
                                  description_object=contents.get('description_translations', {}),
                                  include_in_documentation_toc=include_in_documentation_toc)]
    elif contents.get('anyOf') is not None:
        entities: List[Dict[str, any]] = contents.get('anyOf')
        if not (is_list_of_type(entities, Dict) and all(is_dict_with_keys_of_type(entity, str) for entity in entities)):
            raise TypeError('AnyOf must have format [[String : Any]]')
        return builders.entity_enumeration_build(
            generated_entities=generated_entities,
            entities=entities,
            name=alias(config.lang, contents) or name,
            original_name=name,
            include_in_documentation_toc=include_in_documentation_toc,
            generate_case_for_templates=generate_cases_for_templates(config.lang, contents),
            location=location,
            config=config
        )
    return []


def __build_objects(schema_dir: SchemaDirectory,
                    config: Config.GenerationConfig) -> List[Tuple[ElementLocation, Declarable]]:
    result: List[Tuple[ElementLocation, Declarable]] = []
    files = __sort_by_any_of(__flatten_dirs(schema_dir))

    for file in files:
        objects: List[Declarable] = __generate_objects(file, config, __get_entities(result))
        result.extend(map(lambda obj: (ElementLocation(file), obj), objects))

    return result


def build_objects(schema_dir: SchemaDirectory, config: Config.GenerationConfig) -> List[Declarable]:
    result: List[Tuple[ElementLocation, Declarable]] = __build_objects(schema_dir, config)

    for _, entity in result:
        entity.resolve_dependencies(global_objects=__get_entities(result))
    for location, entity in result:
        entity.check_dependencies_resolved(location=location, stack=[])

    return __get_entities(result)


def __flatten_dirs(schema_dir: SchemaDirectory) -> List[SchemaFile]:
    result: List[SchemaFile] = []
    for item in schema_dir.items:
        if isinstance(item, SchemaFile):
            result.append(item)
        if isinstance(item, SchemaDirectory):
            result.extend(__flatten_dirs(item))
    return result


def __sort_by_any_of(source: List[SchemaFile]) -> List[SchemaFile]:
    return list(sorted(source, key=lambda file: file.contents.get('anyOf') is not None))


def __get_entities(tuple: List[Tuple[ElementLocation, Declarable]]) -> List[Declarable]:
    return list(map(lambda pair: pair[1], tuple))
