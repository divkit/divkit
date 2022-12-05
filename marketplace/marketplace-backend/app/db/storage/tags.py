from typing import Optional
from uuid import UUID

from sqlalchemy import desc, func, update
from sqlalchemy.engine import Row
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.sql import select

from app.db.models import Tags, Templates2Tags


async def get_tag_list(
    tag: str, page: int, size: int, session: AsyncSession
) -> list[Row]:
    t2t_query = select(func.count(Templates2Tags.id)).where(
        Tags.id == Templates2Tags.tag_id
    )
    query = (
        select(Tags.id, Tags.tag, t2t_query.label("max_use"))
        .where(Tags.tag.like(f"%{tag}%"))
        .order_by(desc("max_use"))
        .limit(size)
        .offset(page)
    )
    tag_list = await session.execute(query)
    return tag_list.all()


async def get_tag_list_by_template(
    template_id: UUID, session: AsyncSession
) -> list[Row]:
    query = select(Tags.id, Tags.tag).where(
        Templates2Tags.tag_id == Tags.id,
        Templates2Tags.template_id == str(template_id),
    )
    tag_list = await session.execute(query)
    return tag_list.all()


async def get_tag(tag: str, session: AsyncSession) -> Optional[UUID]:
    query = select(Tags.id).where(Tags.tag == tag)
    tag_id = await session.execute(query)
    return tag_id.scalar()


async def get_tag_by_id(tag_uuid: UUID, session: AsyncSession) -> Optional[UUID]:
    query = select(Tags.id).where(Tags.id == str(tag_uuid))
    tag_id = await session.execute(query)
    return tag_id.scalar()


async def insert_tag(tag: Tags, session: AsyncSession) -> None:
    session.add(tag)
    await session.commit()


async def update_tag(tag: Tags, session: AsyncSession) -> None:
    query = update(Tags).where(Tags.id == str(tag.id)).values(tag=tag.tag)
    await session.execute(query)
    await session.commit()
