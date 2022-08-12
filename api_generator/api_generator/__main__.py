from typing import Tuple
from argparse import ArgumentParser
import os

from .generator import generate_api
from .config import Config


def __file_path(path) -> str:
    if os.path.isfile(path):
        return path
    else:
        raise FileNotFoundError(path)


def __dir_path(path: str) -> str:
    if os.path.isdir(path):
        return path
    else:
        raise NotADirectoryError(path)


def __parse_arguments() -> Tuple[str, str, str]:
    parser = ArgumentParser(description='The script takes a JSON schema at the input and generates the code of '
                                        'objects described in the schema based on it and the config.')
    parser.add_argument('-c', '--config', type=__file_path, help='Path to config file', required=True)
    parser.add_argument('-s', '--schema', type=__dir_path, help='Path to schema directory', required=True)
    parser.add_argument('-o', '--output', type=__dir_path, help='Path to generator output directory', required=True)
    parsed_args = parser.parse_args()
    return parsed_args.config, parsed_args.schema, parsed_args.output


def main():
    config_path, schema_path, output_path = __parse_arguments()
    config = Config(config_path, schema_path, output_path)
    generate_api(config)


if __name__ == '__main__':
    main()
