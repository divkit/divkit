from typing import Optional
from uuid import UUID, uuid4

from sqlalchemy.engine import Row
from sqlalchemy.ext.asyncio import AsyncSession

from app.db.storage import templates as temps_storage
from app.schemas.tag import CreateTagRequest
from app.schemas.template import CreateTemplateRequest
from app.utils import tags as tags_utils
from app.utils import templates as temps_utils
from app.utils.s3 import get_client


async def create_tag(tag_name: str, session: AsyncSession) -> UUID:
    tag_schema = CreateTagRequest(tag=tag_name)
    tag_id = await tags_utils.create_tag(tag_schema, session)

    return tag_id


async def create_template(
    json_template: dict, session: AsyncSession, tag_ids: Optional[list] = None
) -> Optional[Row]:
    if tag_ids is None:
        tag_ids = [await create_tag("test_tag", session)]

    template_schema = CreateTemplateRequest(
        id=uuid4(),
        template=json_template,
        description="test description",
        title="test title",
        tag_ids=tag_ids,
    )
    template_id = await temps_utils.create_template(
        template_schema, None, get_client(), session
    )
    template = await temps_storage.get_template(template_id, session)

    return template
