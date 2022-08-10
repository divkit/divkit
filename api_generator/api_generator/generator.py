from .config import Config, GeneratedLanguage
from .schema.preprocessing import schema_preprocessing
from .schema.modeling import build_objects
from .generators import Generator, SwiftGenerator, KotlinGenerator, DocumentationGenerator, TypeScriptGenerator


def __build_generator(config: Config) -> Generator:
    lang = config.generation.lang
    generator_dict = {
        GeneratedLanguage.SWIFT: SwiftGenerator,
        GeneratedLanguage.KOTLIN: KotlinGenerator,
        GeneratedLanguage.DOCUMENTATION: DocumentationGenerator,
        GeneratedLanguage.TYPE_SCRIPT: TypeScriptGenerator,
    }
    generator = generator_dict.get(lang, None)
    if generator is None:
        raise NotImplementedError
    return generator(config)


def generate_api(config: Config):
    root_directory = schema_preprocessing(config)
    objects = build_objects(root_directory, config.generation)

    generator = __build_generator(config)
    generator.generate(objects)
