# pylint: disable=duplicate-code
from typing import Optional
from uuid import UUID

from sqlalchemy import delete, func, update
from sqlalchemy.dialects.postgresql import FLOAT
from sqlalchemy.engine import Row
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy.sql import select
from sqlalchemy.sql.expression import cast

from app.db.models import Templates, Templates2Tags


async def get_template_list(
    tag_ids: Optional[list],
    title: Optional[str],
    page: int,
    size: int,
    session: AsyncSession,
) -> list[Row]:
    query = select(Templates)
    if tag_ids is not None:
        query = query.where(
            Templates.id == Templates2Tags.template_id,
            Templates2Tags.tag_id.in_(tag_ids),
        )
    if title is not None:
        query = query.where(func.lower(Templates.title).like(f"%{title.lower()}%"))
    query = query.order_by(Templates.id).limit(size).offset(page)
    template_list = await session.execute(query)
    return template_list.scalars().all()


async def get_template_list_total_pages(
    tag_ids: Optional[list],
    title: Optional[str],
    size: int,
    session: AsyncSession,
) -> Optional[int]:
    query = select(func.ceil(cast(func.count(Templates.id), FLOAT) / size))
    if tag_ids is not None:
        query = query.where(
            Templates.id == Templates2Tags.template_id,
            Templates2Tags.tag_id.in_(tag_ids),
        )
    if title is not None:
        query = query.where(func.lower(Templates.title).like(f"%{title.lower()}%"))
    template_list = await session.execute(query)
    return template_list.scalar()


async def get_template(template_id: UUID, session: AsyncSession) -> Optional[Row]:
    query = select(Templates).where(Templates.id == str(template_id))
    template = await session.execute(query)
    return template.scalar()


async def insert_template(template: Templates, session: AsyncSession) -> None:
    session.add(template)
    await session.commit()


async def update_template(template: Templates, session: AsyncSession) -> None:
    query = (
        update(Templates)
        .where(Templates.id == template.id)
        .values(
            template=template.template,
            description=template.description,
            title=template.title,
            dt_updated=template.dt_updated,
            preview_filename=template.preview_filename,
        )
    )
    await session.execute(query)
    await session.commit()


async def delete_template(template_id: UUID, session: AsyncSession) -> None:
    query = delete(Templates).where(Templates.id == str(template_id))
    await session.execute(query)
    await session.commit()
