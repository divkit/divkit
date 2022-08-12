from typing import List
import re
import os
import shutil


def __camel_case_components(string: str) -> List[str]:
    result: List[str] = []
    start_component_pos = 0
    for current_pos, char in enumerate(string):
        if char.isupper() and start_component_pos != current_pos:
            result.append(string[start_component_pos:current_pos])
            start_component_pos = current_pos
    result.append(string[start_component_pos::])
    return result


def name_components(string: str) -> List[str]:
    components = []
    for component in re.split(r'[_\-:\s]+', string):
        components.extend(__camel_case_components(component))
    return list(filter(None, components))


def capitalize_camel_case(string: str) -> str:
    return ''.join(map(lambda component: component.capitalize(), name_components(string)))


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
