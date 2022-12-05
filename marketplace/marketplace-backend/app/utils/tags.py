from uuid import UUID, uuid4

from sqlalchemy.engine import Row
from sqlalchemy.ext.asyncio import AsyncSession

from app.config import DefaultSettings
from app.db.models.tags import Tags
from app.db.storage import tags
from app.schemas import CreateTagRequest, UpdateTagRequest

settings = DefaultSettings()


def normalize_tag_name(tag: str) -> str:
    return tag.lower()


async def get_tag_list(
    tag: str,
    page: int,
    size: int,
    session: AsyncSession,
) -> list[Row]:
    tag = normalize_tag_name(tag)
    tag_list = await tags.get_tag_list(tag, page, size, session)
    return tag_list


async def create_tag(tag: CreateTagRequest, session: AsyncSession) -> UUID:
    tag.tag = normalize_tag_name(tag.tag)
    tag_id = await tags.get_tag(tag.tag, session)
    if tag_id:
        return tag_id
    new_id = uuid4()
    new_tag = Tags(id=str(new_id), tag=tag.tag)
    await tags.insert_tag(new_tag, session)
    return new_id


async def update_tag(tag: UpdateTagRequest, session: AsyncSession) -> UUID:
    tag.tag = normalize_tag_name(tag.tag)
    old_tag = Tags(id=tag.id, tag=tag.tag)
    await tags.update_tag(old_tag, session)
    return tag.id
