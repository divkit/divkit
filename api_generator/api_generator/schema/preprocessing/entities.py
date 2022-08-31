from __future__ import annotations
from abc import ABC, abstractmethod
from typing import Dict, List, Optional
import os
import json

from ...config import GeneratedLanguage
from . import utils
from . import errors
from . import preprocessor
from ...utils import sha256_dir


class SchemaFilesystemItem(ABC):

    def __str__(self) -> str:
        return json.dumps(
            obj=self.as_json,
            default=lambda x: x.as_json if isinstance(x, SchemaFilesystemItem) else str(x),
            indent=2
        )

    @property
    @abstractmethod
    def parent_dir(self) -> SchemaFilesystemItem:
        pass

    @property
    @abstractmethod
    def name(self) -> str:
        pass

    @property
    @abstractmethod
    def has_unresolved_references(self) -> bool:
        pass

    @abstractmethod
    def merge_all_ofs(self, lang: GeneratedLanguage):
        pass

    @abstractmethod
    def resolve_references(self, lang: GeneratedLanguage):
        pass

    @abstractmethod
    def clean_unused_definitions(self):
        pass

    @property
    @abstractmethod
    def as_json(self) -> Dict[str, any]:
        pass


class SchemaDirectory(SchemaFilesystemItem):
    def __init__(self, path: str, parent_dir: Optional[SchemaDirectory], lang: GeneratedLanguage):
        self._name: str = os.path.basename(os.path.normpath(path))
        self._parent_dir: SchemaDirectory = parent_dir
        self._hash = sha256_dir(path)
        self._items: List[SchemaFilesystemItem] = list(
            filter(
                lambda schema_file: not (isinstance(schema_file, SchemaFile) and utils.code_generation_disabled(
                    lang=lang,
                    dictionary=schema_file.contents
                )),
                map(
                    lambda content: preprocessor.internal_resolve_structure(
                        path=f'{path}/{content}',
                        lang=lang,
                        parent_dir=self
                    ),
                    filter(
                        lambda file: file.endswith('.json') or '.' not in file,
                        os.listdir(path)
                    )
                )
            )
        )

    @property
    def hash(self) -> str:
        return self._hash

    @property
    def as_json(self) -> Dict[str, any]:
        return {self._name: list(map(lambda i: i.as_json, self._items))}

    @property
    def parent_dir(self) -> SchemaDirectory:
        return self._parent_dir

    @property
    def name(self) -> str:
        return self._name

    @property
    def items(self) -> List[SchemaFilesystemItem]:
        return self._items

    @property
    def has_unresolved_references(self) -> bool:
        for item in self._items:
            if item.has_unresolved_references:
                return True
        return False

    def merge_all_ofs(self, lang: GeneratedLanguage):
        for item in self._items:
            item.merge_all_ofs(lang)

    def resolve_references(self, lang: GeneratedLanguage):
        while self.has_unresolved_references:
            for item in self._items:
                item.resolve_references(lang)

    def clean_unused_definitions(self):
        for item in self._items:
            item.clean_unused_definitions()

    def resolve(self, relative_path: str) -> SchemaFilesystemItem:
        components = list(filter(lambda comp: comp, relative_path.split('/')))

        if len(components) < 1:
            raise errors.InvalidReferenceError
        first_component = components[0]

        item: SchemaFilesystemItem
        if first_component == '..':
            if self._parent_dir is None:
                raise errors.InvalidReferenceError
            item = self._parent_dir
        else:
            filtered_items = list(filter(lambda it: it.name == first_component, self._items))
            if len(filtered_items) < 1:
                raise errors.InvalidReferenceError
            item = filtered_items[0]

        if len(components) == 1:
            return item

        if not isinstance(item, SchemaDirectory):
            raise errors.InvalidReferenceError

        return item.resolve(relative_path='/'.join(components[1::]))


class SchemaFile(SchemaFilesystemItem):
    def __init__(self, name: str, parent_dir: Optional[SchemaDirectory], contents: Dict[str, any]):
        self._name: str = name
        self._parent_dir: Optional[SchemaDirectory] = parent_dir
        self._contents: Dict[str, any] = contents

    @property
    def as_json(self) -> Dict[str, any]:
        return {self._name: self._contents}

    @property
    def parent_dir(self) -> Optional[SchemaDirectory]:
        return self._parent_dir

    @property
    def name(self) -> str:
        return self._name

    @property
    def contents(self) -> Dict[str, any]:
        return self._contents

    @property
    def has_unresolved_references(self) -> bool:
        return preprocessor.has_unresolved_references(self._contents)

    def resolve_references(self, lang: GeneratedLanguage):
        self._contents = preprocessor.resolve_references(
            dictionary=self._contents,
            location=ElementLocation(file=self),
            lang=lang
        )

    def merge_all_ofs(self, lang: GeneratedLanguage):
        self._contents = preprocessor.merge_all_ofs(
            dictionary=self._contents,
            lang=lang,
            location=ElementLocation(file=self)
        )

    def clean_unused_definitions(self):
        self._contents = preprocessor.clean_unused_definitions(self._contents)


class ElementLocation:
    def __init__(self, file: SchemaFile, path: List[str] = None):
        self._file: SchemaFile = file
        self._path: List[str] = path or []

    def __add__(self, other):
        assert isinstance(other, str)
        return ElementLocation(self._file, self._path + [other])

    def __str__(self) -> str:
        joined_path = '.'.join(self._path)
        return f'in {self._file.name} at "{joined_path}"'

    def __repr__(self):
        return str(self)

    @property
    def file(self) -> SchemaFile:
        return self._file

    @property
    def path(self) -> List[str]:
        return self._path


class Reference:
    def __init__(self, location: ElementLocation, ref: str):
        prefix: str
        self._file: SchemaFile
        self._path: List[str]
        self._value: Dict[str, any]

        prefix, self._file, self._path = utils.get_full_reference_location(location, ref)

        definition = preprocessor.prepend_prefix_to_references(
            prefix=prefix,
            dictionary=utils.enclosed_dict_for(
                keys=self._path,
                dictionary=self._file.contents
            )
        )

        def_type: Optional[str] = definition.get('type')
        is_obj = def_type == 'object'
        is_str_enum = def_type == 'string' and definition.get('enum') is not None
        is_entity_enum = definition.get('anyOf') is not None
        is_top_level_entity = def_type.startswith('$defined_') if isinstance(def_type, str) else False
        is_referenced_from_top_level = len(location.path) == 0
        should_inline = is_referenced_from_top_level or not (
            is_obj or is_str_enum or is_entity_enum or is_top_level_entity)

        if not self._path and not should_inline:
            file_first_comp = self._file.name.split('.')[0]
            self._value = {'type': f'$defined_{file_first_comp}'}
            return

        if def_type is None and not is_entity_enum:
            self._value = definition
            return

        inlining_suppressed = definition.get('suppress_inline', False)
        is_unique_reference_in_same_file: bool
        if self._file == location.file:
            obj_name = self._path[-1] if self._path else '#'
            num_of_refs = utils.number_of_references(obj_name=obj_name, dictionary=location.file.contents)
            is_unique_reference_in_same_file = num_of_refs == 1
        else:
            is_unique_reference_in_same_file = False
        top_level_entity_name = self._file.name.replace('.json', '')

        if is_unique_reference_in_same_file and not inlining_suppressed:
            typename_value = self.path[-1] if self._path else top_level_entity_name
            definition |= {'$typename': typename_value}
            self._value = definition
        elif should_inline:
            self._value = definition
        else:
            type_value = self.path[-1] if self._path else top_level_entity_name
            self._value = {'type': f'$defined_{type_value}'}

    def __eq__(self, other) -> bool:
        assert isinstance(other, Reference)
        return self.file == other.file and self.path == other.path

    @property
    def file(self) -> SchemaFile:
        return self._file

    @property
    def path(self) -> List[str]:
        return self._path

    @property
    def value(self) -> Dict[str, any]:
        return self._value


class DescriptionReference:
    def __init__(self, location: ElementLocation, ref: str):
        file: SchemaFile
        path: List[str]
        _, file, path = utils.get_full_reference_location(location, ref)
        self._value = utils.enclosed_dict_for(keys=path, dictionary=file.contents)

    @property
    def value(self) -> Dict[str, str]:
        return self._value
