from __future__ import annotations
from typing import Text, List, Optional
from pydantic import BaseModel, Field
from enum import Enum, auto
from .utils import sha256_file, sha256_dir
import os


class Platform(str, Enum):
    ANDROID = 'android'
    IOS = 'ios'
    WEB = 'web'


class GeneratedLanguage(str, Enum):
    SWIFT = 'swift'
    KOTLIN = 'kotlin'
    KOTLIN_DSL = 'kotlinDsl'
    TYPE_SCRIPT = 'typescript'
    PYTHON = 'python'
    DOCUMENTATION = 'documentation'


TEMPLATE_SUFFIX = '_template'


class GenerationMode(Enum):
    NORMAL_WITH_TEMPLATES = auto()
    NORMAL_WITHOUT_TEMPLATES = auto()
    TEMPLATE = auto()

    @property
    def is_template(self) -> bool:
        if self in [GenerationMode.NORMAL_WITH_TEMPLATES, GenerationMode.NORMAL_WITHOUT_TEMPLATES]:
            return False
        elif self is GenerationMode.TEMPLATE:
            return True

    @property
    def name_suffix(self) -> str:
        if self in [GenerationMode.NORMAL_WITH_TEMPLATES, GenerationMode.NORMAL_WITHOUT_TEMPLATES]:
            return ''
        elif self is GenerationMode.TEMPLATE:
            return TEMPLATE_SUFFIX

    def protocol_name(self, lang: GeneratedLanguage, name: str) -> Optional[str]:
        if self.is_template:
            return 'TemplateValue' if lang is GeneratedLanguage.SWIFT else f'JsonTemplate<{name}>'
        return None


class Config:
    class GenerationConfig(BaseModel):
        lang: GeneratedLanguage
        header: Text = ''
        errors_collectors: List[str] = Field([], alias='errorsCollectors')
        kotlin_annotations: List[str] = Field([], alias='kotlinAnnotations')
        generate_equality: bool = Field(False, alias='generateEquality')

    def __init__(self, generator_path: str, config_path: str, schema_path: str, output_path: str):
        self.schema_path: str = schema_path
        self.output_path: str = output_path
        self.generation: Config.GenerationConfig = Config.GenerationConfig.parse_file(path=config_path)

        self.generator_hash = sha256_dir(generator_path)
        stored_generator_hash = _stored_sha256(os.path.join(output_path, 'generator_hash'))
        self.generator_changed = self.generator_hash != stored_generator_hash

        self.config_hash = sha256_file(config_path)
        stored_config_hash = _stored_sha256(os.path.join(output_path, 'config_hash'))
        self.config_changed = self.config_hash != stored_config_hash

        self.stored_input_hash = _stored_sha256(os.path.join(output_path, 'input_hash'))
        self.stored_output_hash = _stored_sha256(os.path.join(output_path, 'output_hash'))


def _stored_sha256(path: str) -> Optional[str]:
    if os.path.exists(path):
        with open(path, "r") as f:
            data = f.read()
            return str(data)
    return None
