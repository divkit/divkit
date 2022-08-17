from ...config import GenerationMode
from typing import Optional


def fixing_keywords(string: str) -> str:
    if string == 'default':
        return '`default`'
    return string


def implemented_swift_protocol(mode: GenerationMode) -> Optional[str]:
    if mode is GenerationMode.NORMAL_WITHOUT_TEMPLATES:
        return 'Deserializable'
    elif mode is GenerationMode.TEMPLATE:
        return 'TemplateDeserializable'
    else:
        return None
