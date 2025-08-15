from __future__ import annotations
from typing import Text, List, Optional
import json
from enum import Enum, auto
from .utils import sha256_file, sha256_dir, print_warning
import os


class Platform(str, Enum):
    ANDROID = 'android'
    IOS = 'ios'
    WEB = 'web'
    FLUTTER = 'flutter'


class GeneratedLanguage(str, Enum):
    SWIFT = 'swift'
    KOTLIN = 'kotlin'
    KOTLIN_DSL = 'kotlinDsl'
    TYPE_SCRIPT = 'typescript'
    PYTHON = 'python'
    DIVAN = 'divan'
    DOCUMENTATION = 'documentation'
    DART = 'dart'


TEMPLATE_SUFFIX = '_template'
SERIALIZER_SUFFIX = '_json_parser'


class GenerationMode(Enum):
    NORMAL_WITH_TEMPLATES = auto()
    NORMAL_WITHOUT_TEMPLATES = auto()
    TEMPLATE = auto()
    SERIALIZER = auto()

    @property
    def is_template(self) -> bool:
        return self is GenerationMode.TEMPLATE

    @property
    def is_serializer(self) -> bool:
        return self is GenerationMode.SERIALIZER

    @property
    def name_suffix(self) -> str:
        if self in [GenerationMode.NORMAL_WITH_TEMPLATES, GenerationMode.NORMAL_WITHOUT_TEMPLATES]:
            return ''
        if self is GenerationMode.SERIALIZER:
            return SERIALIZER_SUFFIX
        elif self is GenerationMode.TEMPLATE:
            return TEMPLATE_SUFFIX

    def protocol_name(self, lang: GeneratedLanguage, name: str) -> Optional[str]:
        if self.is_template:
            return 'TemplateValue' if lang is GeneratedLanguage.SWIFT else f'JsonTemplate<{name}>'
        return None


class Config:
    class KotlinAnnotations:
        def __init__(self, dictionary):
            self.classes: List[str] = dictionary.get('classes') or []
            self.constructors: List[str] = dictionary.get('constructors') or []
            self.top_level_definitions: List[str] = dictionary.get('topLevelDefinitions') or []

    class GenerationConfig:
        def __init__(self, dictionary):
            self.lang: GeneratedLanguage = GeneratedLanguage(dictionary['lang'])
            self.header: Text = dictionary.get('header') or ''
            self.errors_collectors: List[str] = dictionary.get('errorsCollectors') or []
            self.kotlin_annotations: Config.KotlinAnnotations = Config.KotlinAnnotations(dictionary.get('kotlinAnnotations') or {})
            self.generate_equality: bool = dictionary.get('generateEquality') or False
            self.remove_prefix: str = dictionary.get('removePrefix') or ''
            self.supertype_entities: List[str] = dictionary.get('supertypeEntities') or []

            self.generate_templates: bool = dictionary.get('generateTemplates', True)
            self.generate_serialization: bool = dictionary.get('generateSerialization', True)

            if ('generateTemplates' in dictionary or 'generateSerialization' in dictionary) and \
                    self.lang not in [GeneratedLanguage.KOTLIN, GeneratedLanguage.SWIFT]:
                raise NotImplementedError('generateTemplates & generateSerialization flags are supported only for '
                                          'Kotlin and Swift languages')

            if self.generate_templates and not self.generate_serialization:
                if 'generateTemplates' in dictionary:
                    print_warning('Templates can not be generated without Serialization. '
                                  'Templates will not be generated.')
                self.generate_templates = False

            self.generate_serializers: bool = dictionary.get('generateSerializers', False)

            if ('generateSerializers' in dictionary) and \
                    self.lang != GeneratedLanguage.KOTLIN:
                raise NotImplementedError('generateSerializers flag is supported only for Kotlin language')

    def __init__(self, generator_path: Optional[str], config_path: str, schema_path: str, output_path: str):
        self.schema_path: str = schema_path
        self.output_path: str = output_path
        with open(config_path) as f:
            dictionary = json.loads(f.read())
            f.close()
            self.generation: Config.GenerationConfig = Config.GenerationConfig(dictionary)

        if generator_path:
            self.generator_hash = sha256_dir(generator_path)
            stored_generator_hash = _stored_sha256(os.path.join(output_path, 'generator_hash'))
            self.generator_changed = self.generator_hash != stored_generator_hash
        else:
            self.generator_hash = None
            self.generator_changed = True

        self.config_hash = sha256_file(config_path)
        stored_config_hash = _stored_sha256(os.path.join(output_path, 'config_hash'))
        self.config_changed = self.config_hash != stored_config_hash

        self.stored_input_hash = _stored_sha256(os.path.join(output_path, 'input_hash'))
        self.stored_output_hash = _stored_sha256(os.path.join(output_path, 'output_hash'))


def _stored_sha256(path: str) -> Optional[str]:
    if os.path.exists(path):
        with open(path, "r") as f:
            data = f.read()
            f.close()
            return str(data)
    return None
