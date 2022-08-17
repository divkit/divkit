from typing import List, Dict, Any
import re
import os
import shutil
import json


def __camel_case_components(string: str) -> List[str]:
    def is_upper_letter(letter: str) -> bool:
        return 'A' <= letter <= 'Z'
    result: List[str] = []
    start_component_pos = 0
    for current_pos, char in enumerate(string):
        if is_upper_letter(char):
            if start_component_pos != current_pos:
                result.append(string[start_component_pos:current_pos])
                start_component_pos = current_pos
    result.append(string[start_component_pos::])
    return result


def name_components(string: str) -> List[str]:
    components = []
    for component in re.split(r'[_\-:\s]+', string):
        components.extend(__camel_case_components(component))
    return list(filter(lambda x: x, components))


def capitalize_camel_case(string: str) -> str:
    return ''.join(map(lambda component: component.capitalize(), name_components(string)))


def lower_camel_case(string: str) -> str:
    components = name_components(string)
    if not components:
        return ''
    return ''.join([components[0].lower()] + list(map(lambda component: component.capitalize(), components[1::])))


def fixing_first_digit(string: str) -> str:
    if not string:
        return string
    first_char = string[0]
    if first_char.isdigit():
        return f'_{string}'
    return string


def clear_content_of_directory(directory: str) -> None:
    for filename in os.listdir(directory):
        file_path = os.path.join(directory, filename)
        try:
            if os.path.isfile(file_path) or os.path.islink(file_path):
                os.unlink(file_path)
            elif os.path.isdir(file_path):
                shutil.rmtree(file_path)
        except Exception as e:
            print(f'Failed to delete {file_path}. Reason: {e}')


def json_dict(string: str) -> Dict[str, Any]:
    return json.loads(string)


def indented(string: str, level: int = 1, indent_width: int = 2) -> str:
    indent = ' ' * level * indent_width
    return indent + string.replace('\n', '\n' + indent)
