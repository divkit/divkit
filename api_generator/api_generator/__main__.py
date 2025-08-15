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


def __dir_path_with_makedir(path: str) -> str:
    try:
        return __dir_path(path)
    except NotADirectoryError:
        os.makedirs(path)
        return __dir_path(path)


def __parse_arguments() -> Tuple[str, str, str, bool, bool]:
    parser = ArgumentParser(description='The script takes a JSON schema at the input and generates the code of '
                                        'objects described in the schema based on it and the config.')
    parser.add_argument('-c', '--config', type=__file_path, help='Path to config file', required=True)
    parser.add_argument('-s', '--schema', type=__dir_path, help='Path to schema directory', required=True)
    parser.add_argument('-o', '--output',
                        type=__dir_path_with_makedir,
                        help='Path to generator output directory',
                        required=True)
    parser.add_argument('--checkhash', action='store_true', help='Check the hash files in the output directory before '
                                                                 'generating.')
    parser.add_argument('--savehash', action='store_true', help='Save the hash files of the generator, config and '
                                                                'schema to the output directory.')
    parsed_args = parser.parse_args()
    return parsed_args.config, parsed_args.schema, parsed_args.output, parsed_args.checkhash, parsed_args.savehash


def main():
    config_path, schema_path, output_path, check_hash, save_hash = __parse_arguments()
    generator_path = os.path.dirname(__file__)
    if not os.path.isdir(generator_path):
        generator_path = None
    config = Config(generator_path, config_path, schema_path, output_path)
    generate_api(config, check_hash, save_hash)


if __name__ == '__main__':
    main()
