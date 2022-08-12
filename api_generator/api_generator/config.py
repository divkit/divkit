from __future__ import annotations
from typing import Text, List, Optional
from pydantic import BaseModel
from enum import Enum, auto


class Platform(str, Enum):
    ANDROID = 'android'
    IOS = 'ios'
    WEB = 'web'


class GeneratedLanguage(str, Enum):
    SWIFT = 'swift'
    KOTLIN = 'kotlin'
    KOTLIN_DSL = 'kotlinDsl'
    TYPES_SCRIPT = 'typescript'
    PYTHON = 'python'
    DOCUMENTATION = 'documentation'


class GeneratedAccessLevel(str, Enum):
    PUBLIC = 'public'
    INTERNAL = 'internal'


TEMPLATE_SUFFIX = '_template'


class GenerationMode:
    class Type(Enum):
        NORMAL_WITH_TEMPLATES = auto()
        NORMAL_WITHOUT_TEMPLATES = auto()
        TEMPLATE = auto()

        def is_template(self):
            if self in [GenerationMode.Type.NORMAL_WITH_TEMPLATES, GenerationMode.Type.NORMAL_WITHOUT_TEMPLATES]:
                return False
            elif self is GenerationMode.Type.TEMPLATE:
                return True

        def name_suffix(self):
            if self in [GenerationMode.Type.NORMAL_WITH_TEMPLATES, GenerationMode.Type.NORMAL_WITHOUT_TEMPLATES]:
                return ''
            elif self is GenerationMode.Type.TEMPLATE:
                return TEMPLATE_SUFFIX

    def __init__(self, generation_type: GenerationMode.Type):
        self._type = generation_type

    @property
    def type(self) -> GenerationMode.Type:
        return self._type

    @property
    def is_template(self) -> bool:
        return self._type.is_template()


class Config:
    class GenerationConfig(BaseModel):
        class Swift(BaseModel):
            access_level: GeneratedAccessLevel = GeneratedAccessLevel.PUBLIC
            generate_serialization: bool = False

        lang: GeneratedLanguage
        header: Text = ''
        swift: Optional[Swift] = None
        errors_collectors: List[str] = []

    def __init__(self, config_path: str, schema_path: str, output_path: str):
        self.schema_path: str = schema_path
        self.output_path: str = output_path
        self.generation: Config.GenerationConfig = Config.GenerationConfig.parse_file(path=config_path)
