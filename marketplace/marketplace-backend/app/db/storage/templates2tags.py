from typing import Optional
from uuid import UUID

from sqlalchemy import delete
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.sql import select

from app.db.models.templates_to_tags import Templates2Tags


async def get_template2tag(
    tag_id: UUID, template_id: UUID, session: AsyncSession
) -> Optional[UUID]:
    query = select(Templates2Tags.id).where(
        Templates2Tags.tag_id == str(tag_id),
        Templates2Tags.template_id == str(template_id),
    )
    template2tag_id = await session.execute(query)
    return template2tag_id.scalar()


async def insert_template2tag(
    template2tag: Templates2Tags, session: AsyncSession
) -> None:
    session.add(template2tag)
    await session.commit()


async def delete_template2tags(
    tag_ids: Optional[list[UUID]], template_id: UUID, session: AsyncSession
) -> None:
    query = delete(Templates2Tags).where(Templates2Tags.template_id == str(template_id))
    if tag_ids:
        query = query.where(
            Templates2Tags.tag_id.not_in(list(map(str, tag_ids))),  # type: ignore
        )
    await session.execute(query)
    await session.commit()
