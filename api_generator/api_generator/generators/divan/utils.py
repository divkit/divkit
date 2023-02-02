import re
from typing import List


def __camel_case_components(string: str) -> List[str]:
    result: List[str] = []
    start_component_pos = 0
    for current_pos, char in enumerate(string):
        if char.isupper():
            if start_component_pos != current_pos:
                result.append(string[start_component_pos:current_pos])
                start_component_pos = current_pos
    result.append(string[start_component_pos::])
    return result


def __name_components_without_div_prefix(string: str) -> List[str]:
    components = []
    for component in re.split(r'[_\-:\s]+', string):
        components.extend(__camel_case_components(component))
    components = list(filter(lambda x: x, components))
    if len(components) > 1 and components[0].lower() == 'div':
        components = components[1::]
    return components


def capitalize_camel_case(string: str) -> str:
    return ''.join(map(lambda component: component.capitalize(),
                       filter(lambda s: s, __name_components_without_div_prefix(string))))


def snake_case(string: str) -> str:
    return '_'.join(
        map(lambda component: component.lower(), filter(lambda s: s, __name_components_without_div_prefix(string))))


def capitalize_snake_case(string: str) -> str:
    return snake_case(string).capitalize()


def upper_snake_case(string: str) -> str:
    return '_'.join(
        map(lambda component: component.upper(), filter(lambda s: s, __name_components_without_div_prefix(string))))


def lower_camel_case(string: str) -> str:
    components = __name_components_without_div_prefix(string)
    if not components:
        return ''
    return ''.join([components[0].lower()] + list(map(lambda component: component.capitalize(), components[1::])))


def constant_upper_case(string: str):
    return '_'.join(map(lambda s: s.upper(), filter(lambda s: s, __name_components_without_div_prefix(string))))
