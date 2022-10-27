from io import StringIO
from typing import List, Dict, Any
import re
import os
import shutil
import json
import hashlib
from _hashlib import HASH as Hash


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


def name_components(string: str) -> List[str]:
    components = []
    for component in re.split(r'[_\-:\s]+', string):
        components.extend(__camel_case_components(component))
    return list(filter(lambda x: x, components))


def capitalize_camel_case(string: str) -> str:
    return ''.join(map(lambda component: component.capitalize(), name_components(string)))


def snake_case(string: str) -> str:
    return '_'.join(map(lambda component: component.lower(), filter(lambda s: s, name_components(string))))


def upper_snake_case(string: str) -> str:
    return '_'.join(map(lambda component: component.upper(), filter(lambda s: s, name_components(string))))


def lower_camel_case(string: str) -> str:
    components = name_components(string)
    if not components:
        return ''
    return ''.join([components[0].lower()] + list(map(lambda component: component.capitalize(), components[1::])))


def constant_upper_case(string: str):
    return '_'.join(map(lambda s: s.upper(), name_components(string)))


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


def sha256_update_from_file(filename: str, hash: Hash) -> Hash:
    assert os.path.isfile(filename)
    with open(filename, "rb") as f:
        for chunk in iter(lambda: f.read(4096), b""):
            hash.update(chunk)
    return hash


def sha256_file(filename: str) -> str:
    return str(sha256_update_from_file(filename, hashlib.sha256()).hexdigest())


def sha256_update_from_dir(directory: str, hash: Hash) -> Hash:
    assert os.path.isdir(directory)
    for file in sorted(os.listdir(directory)):
        hash.update(file.encode())
        joined_file = os.path.join(directory, file)
        if os.path.isfile(joined_file):
            hash = sha256_update_from_file(joined_file, hash)
        elif os.path.isdir(joined_file):
            hash = sha256_update_from_dir(joined_file, hash)
    return hash


def sha256_dir(directory: str) -> str:
    return str(sha256_update_from_dir(directory, hashlib.sha256()).hexdigest())


def sha256_for_filenames(filenames: List[str]) -> str:
    hash = hashlib.sha256()
    for name in sorted(filenames):
        hash.update(name.encode())
    return str(hash.hexdigest())


def indent(
    data: str, indent: int, spacer=" ",
    indent_first_line: bool = True,
    indent_last_line: bool = True,
) -> str:
    with StringIO() as fp:
        lines = data.splitlines()
        first_line = 0
        last_line = len(lines) - 1

        for idx, line in enumerate(lines):
            if idx == first_line and not indent_first_line:
                fp.write(line)
                fp.write("\n")
                continue
            if idx == last_line and not indent_last_line:
                fp.write(line)
                fp.write("\n")
                continue
            fp.write((spacer * indent) + line)
            fp.write("\n")
        return fp.getvalue()


def python_long_sting_split(string: str, max_len: int = 59) -> str:
    if len(string) < max_len:
        string = string.replace('"', '\\"')
        return f'"{string}"'

    with StringIO() as fp:
        fp.write("(\n")
        line = ""

        for word in string.split():
            # check if line too long after adding word
            if (len(line) + len(word)) > max_len:
                fp.write(f'"{line}"\n')
                line = ""

            line += word.replace('"', '\\"')
            # force split
            if len(line) > max_len:
                head, line = line[:max_len], line[max_len:]
                fp.write(f'"{head}"\n')
            line += " "

        if line:
            fp.write(f'"{line.strip()}"')

        fp.write("\n)")
        return fp.getvalue()
