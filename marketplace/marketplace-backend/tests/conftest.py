# pylint: disable=redefined-outer-name
# pylint: disable=unused-argument
from asyncio import get_event_loop_policy
from os import environ
from types import SimpleNamespace
from typing import AsyncGenerator
from uuid import uuid4

import pytest
from alembic.command import upgrade
from alembic.config import Config
from factory import Factory, Faker
from fastapi import FastAPI
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import AsyncEngine, AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy_utils import create_database, database_exists, drop_database

from app.__main__ import get_application
from app.config import get_api_settings, get_db_settings
from app.db.models import Tags, Templates, Templates2Tags
from tests.utils import make_alembic_config


@pytest.fixture
def _app() -> FastAPI:
    """Get application instance."""
    return get_application()


@pytest.fixture
async def async_client(_app: FastAPI) -> AsyncGenerator[AsyncClient, None]:
    """Create async client with overriding dependencies."""
    settings = get_api_settings()
    async with AsyncClient(app=_app, base_url=f"{settings.base_url}/v1") as client:
        yield client


@pytest.fixture(scope="session")
def event_loop():  # type: ignore
    """
    Creates event loop for tests.
    """
    policy = get_event_loop_policy()
    loop = policy.new_event_loop()
    yield loop
    loop.close()


@pytest.fixture
async def postgres():  # type: ignore
    """
    Создает временную БД для запуска теста.
    """
    settings = get_db_settings()

    tmp_name = "db.pytest"
    settings.db = tmp_name
    environ["POSTGRES_DB"] = tmp_name

    tmp_url = "postgresql://" + settings.db_url.split("://")[1]
    if not database_exists(tmp_url):
        create_database(tmp_url)
    try:
        yield settings.db_url
    finally:
        drop_database(tmp_url)


@pytest.fixture
def alembic_config(postgres: str) -> Config:
    """
    Создает файл конфигурации для alembic.
    """
    cmd_ops = SimpleNamespace(
        config="", name="alembic", pg_url=postgres, raiseerr=False, x=None
    )
    return make_alembic_config(cmd_ops)


def run_upgrade(connection, cfg: Config) -> None:  # type: ignore
    cfg.attributes["connection"] = connection
    upgrade(cfg, "head")


@pytest.fixture
def migrated_postgres(postgres, alembic_config: Config) -> None:  # type: ignore
    """
    Проводит миграции.
    """
    run_upgrade(postgres, alembic_config)


@pytest.fixture
async def engine_async(postgres, migrated_postgres) -> AsyncEngine:  # type: ignore
    settings = get_db_settings()
    engine = create_async_engine(settings.db_url, future=True)
    yield engine
    await engine.dispose()


@pytest.fixture
def session_factory_async(engine_async) -> sessionmaker:  # type: ignore
    return sessionmaker(engine_async, class_=AsyncSession, expire_on_commit=False)


@pytest.fixture
async def session(session_factory_async) -> AsyncSession:  # type: ignore
    async with session_factory_async() as session:
        yield session


class TagFactory(Factory):
    class Meta:
        model = Tags

    tag = Faker("color")


async def create_new_tag() -> Tags:
    new_tag = TagFactory.build()
    new_tag.id = str(uuid4())
    return new_tag


@pytest.fixture
async def tags() -> list[Tags]:
    new_tags = [await create_new_tag() for i in range(5)]
    return new_tags


@pytest.fixture
async def tag() -> Tags:
    new_tag = await create_new_tag()
    return new_tag


class TemplatesFactory(Factory):
    class Meta:
        model = Templates

    description = Faker("sentence")
    title = Faker("word")
    template = Faker("json")
    dt_updated = Faker("date_object")
    preview_filename = Faker("word")


async def create_new_temp() -> Templates:
    new_temp = TemplatesFactory.build()
    new_temp.id = str(uuid4())
    return new_temp


@pytest.fixture
async def template() -> Templates:
    new_temp = await create_new_temp()
    return new_temp


@pytest.fixture
async def templates() -> list[Templates]:
    new_temps = [await create_new_temp() for i in range(5)]
    return new_temps


class Templates2TagsFactory(Factory):
    class Meta:
        model = Templates2Tags


async def create_new_temp2tag(temp: Templates, tg: Tags) -> Templates2Tags:
    new_t2t = Templates2TagsFactory.build()
    new_t2t.id = str(uuid4())
    new_t2t.template_id = temp.id
    new_t2t.tag_id = tg.id
    return new_t2t


@pytest.fixture
async def t2t(  # type: ignore
    template: Templates, tag: Tags, session: AsyncSession
) -> list[Templates2Tags, str]:  # type: ignore
    session.add(template)
    session.add(tag)
    new_t2t = await create_new_temp2tag(template, tag)
    return [new_t2t, str(tag.tag), str(template.id)]


@pytest.fixture
async def t2ts(
    templates: list[Templates], tags: list[Tags], session: AsyncSession
) -> list[list[Templates2Tags, Tags, Templates]]:  # type: ignore
    new_t2ts = []
    for i in range(2):
        session.add(tags[i])
        session.add(templates[i])
        new_t2t = await create_new_temp2tag(templates[i], tags[i])
        session.add(new_t2t)
        new_t2ts.append([new_t2t, tags[i], templates[i]])
    return new_t2ts
