from ... import utils
from typing import List
from ...schema.modeling.entities import Declarable


def get_full_name(current: Declarable) -> str:
    if current is not None:
        return get_full_name(current.parent) + utils.capitalize_camel_case(current.name)
    else:
        return ''


def make_imports(items: List[str]) -> List[str]:
    if not items:
        return []
    result = []
    for item in items:
        result.append(f"import '{utils.snake_case(item)}.dart';")
    return result


dart_keywords = ['default']


def allowed_name(name: str) -> str:
    if name in dart_keywords:
        return f'{name}_'
    else:
        return name
