from .config import Config
from .schema.preprocessing import schema_preprocessing
from .schema.modeling import build_objects
from .generators import Generator


def __build_generator(config: Config) -> Generator:
    lang = config.generation.lang
    generator_dict = {}
    generator = generator_dict.get(lang, None)
    if generator is None:
        raise NotImplementedError
    return generator()


def generate_api(config: Config):
    root_directory = schema_preprocessing(config)
    objects = build_objects(root_directory, config.generation)

    generator = __build_generator(config)
    generator.generate(objects)
