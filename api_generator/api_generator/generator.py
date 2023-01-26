import os

from .config import Config, GeneratedLanguage
from .schema.preprocessing import schema_preprocessing
from .schema.modeling import build_objects
from .utils import sha256_for_filenames
from .generators import (
    DartGenerator,
    DivanGenerator,
    DocumentationGenerator,
    KotlinGenerator,
    KotlinDSLGenerator,
    PythonGenerator,
    SwiftGenerator,
    TypeScriptGenerator,
    Generator,
)


def __build_generator(config: Config) -> Generator:
    lang = config.generation.lang
    generator_dict = {
        GeneratedLanguage.SWIFT: SwiftGenerator,
        GeneratedLanguage.KOTLIN: KotlinGenerator,
        GeneratedLanguage.DOCUMENTATION: DocumentationGenerator,
        GeneratedLanguage.TYPE_SCRIPT: TypeScriptGenerator,
        GeneratedLanguage.PYTHON: PythonGenerator,
        GeneratedLanguage.KOTLIN_DSL: KotlinDSLGenerator,
        GeneratedLanguage.DIVAN: DivanGenerator,
        GeneratedLanguage.DART: DartGenerator,
    }
    generator = generator_dict.get(lang, None)
    if generator is None:
        raise NotImplementedError
    return generator(config)


def generate_api(config: Config, check_hash_files: bool = False, save_hash_files: bool = False):
    root_directory = schema_preprocessing(config)
    objects = build_objects(root_directory, config.generation)

    generator = __build_generator(config)

    filenames = list(map(lambda obj: generator.filename(obj.name), objects))
    expected_output_hash = sha256_for_filenames(filenames)

    if check_hash_files and \
            not config.generator_changed and \
            not config.config_changed and \
            config.stored_input_hash == root_directory.hash and \
            config.stored_output_hash == expected_output_hash:
        print('Input and output were not changed, won\'t write anything to the disk')
        return

    generator.generate(objects)

    if save_hash_files:
        def save_hash_file(filename: str, hash: str):
            with open(os.path.join(config.output_path, filename), 'w') as f:
                f.write(hash)

        save_hash_file('input_hash', root_directory.hash)
        save_hash_file('output_hash', expected_output_hash)
        if config.generator_hash:
            save_hash_file('generator_hash', config.generator_hash)
        save_hash_file('config_hash', config.config_hash)
