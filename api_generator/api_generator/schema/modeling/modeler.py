from typing import List, Dict, Tuple, Optional

from ...config import Config
from ..utils import is_list_of_type, is_dict_with_keys_of_type
from ..preprocessing.entities import SchemaDirectory, SchemaFile, ElementLocation

from . import builders
from .entities import Declarable, StringEnumeration
from .utils import (
    alias,
    should_generate_swift_serialization,
    generate_cases_for_templates
)


def __generate_objects(file: SchemaFile, config: Config.GenerationConfig) -> List[Declarable]:
    contents: Dict[str, any] = file.contents
    name: str = file.name.replace('.json', '')
    type_value: Optional[str] = contents.get('type')
    location = ElementLocation(file)
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
                                  include_documentation_toc=contents.get('include_in_documentation_toc', False))]
    elif contents.get('anyOf') is not None:
        entities: List[Dict[str, any]] = contents.get('anyOf')
        if not (is_list_of_type(entities, Dict) and all(is_dict_with_keys_of_type(entity, str) for entity in entities)):
            raise TypeError('AnyOf must have format [[String : Any]]')
        return builders.entity_enumeration_build(
            entities=entities,
            name=alias(config.lang, contents) or name,
            original_name=name,
            include_in_documentation_toc=contents.get('include_in_documentation_toc', False),
            root_entity=contents.get('root_entity', False),
            generate_serialization=should_generate_swift_serialization(config, contents),
            generate_case_for_templates=generate_cases_for_templates(config.lang, contents),
            location=location,
            config=config
        )
    return []


def __build_objects(schema_dir: SchemaDirectory,
                    config: Config.GenerationConfig) -> List[Tuple[ElementLocation, Declarable]]:
    result: List[Tuple[ElementLocation, Declarable]] = []
    for item in schema_dir.items:
        if isinstance(item, SchemaFile):
            file: SchemaFile = item
            objects: List[Declarable] = __generate_objects(file, config)
            result.extend(map(lambda obj: (ElementLocation(file), obj), objects))
        elif isinstance(item, SchemaDirectory):
            result.extend(__build_objects(item, config))
    return result


def build_objects(schema_dir: SchemaDirectory, config: Config.GenerationConfig) -> List[Declarable]:
    result: List[Tuple[ElementLocation, Declarable]] = __build_objects(schema_dir, config)

    def get_entities() -> List[Declarable]:
        return list(map(lambda pair: pair[1], result))

    for _, entity in result:
        entity.resolve_dependencies(global_objects=get_entities())
    for location, entity in result:
        entity.check_dependencies_resolved(location=location, stack=[])

    return get_entities()
